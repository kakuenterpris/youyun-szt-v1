package com.ustack.file.utils;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 随机字符串、随机数字  生成工具类
 * @author linxin
 */
public final class RandomStringUtil {
    private static final Random rnd = new SecureRandom();

    private static final String SEED_CHARS = "0123456789";

    /**
     * 功能：生成由指定位数的随机数字而组成的一个字符串
     **/
    public static String genRandomNumberString(int length) {
        StringBuffer sb = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            int index = rnd.nextInt(SEED_CHARS.length());
            char cha = SEED_CHARS.charAt(index);
            sb.append(cha);
        }
        return sb.toString();
    }

    private static final String SEED_CHARS_TEXT = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 功能：生成指定位数的随机字符串（每一位随机由[a-z0-9A-Z]组成，即有62种可能性）
     **/
    public static String genRandomString(int length) {
        StringBuffer sb = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            int index = rnd.nextInt(SEED_CHARS_TEXT.length());
            char cha = SEED_CHARS_TEXT.charAt(index);
            sb.append(cha);
        }
        return sb.toString();
    }

    private static final AtomicInteger lastSecond = new AtomicInteger(-1);//最后生成序列号的秒数
    private static final AtomicInteger seqCounter = new AtomicInteger(-1);//1秒内的序列计数器

    /**
     * 功能：生成8位长度的数字序列号<br>
     * <ul style="list-style-type:circle;">
     *  <li>特征一：<br>&nbsp;&nbsp;对于单个JVM，每秒钟最多生成1000个序列号；<br>&nbsp;&nbsp;即单个JVM一天之内所生成的数字序列号范围区间是[00000000-86399999]，一共86400000个值；</li>
     *  <li>特征二：<br>&nbsp;&nbsp;对于分布式场景，建议每个实例传入不同的机器ID参数（机器ID的取值区间为[0-2720]）；<br>&nbsp;&nbsp;这样生成结果时会以机器ID*5秒作为偏移量，以保证分布式场景下不会出现相同的数字序列号；</li>
     *  </ul>
     **/
    @SuppressWarnings("deprecation")
    public static String gen8BitSequenceNumber(final short machineId) {
        //1)获取到当前的秒数
        Date now = new Date();
        long nowTimestemp = now.getTime();
        now.setHours(0);
        now.setMinutes(0);
        now.setSeconds(0);
        int currentSecond = (int) (nowTimestemp - now.getTime()) / 1000;
        now = null;
        if (currentSecond < 0) {//隔天时防止出现负数
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            currentSecond = 0;
        }
        //2)获得序号
        int counterValue = 0;
        if (lastSecond.get() == currentSecond) {//相同的同1秒内
            counterValue = seqCounter.incrementAndGet();//计数器递增，直至最大值999
            if (counterValue > 999) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }//阻塞到下一秒，并且计数器重置为0
                currentSecond = currentSecond + 1;
                if (currentSecond > 86399) {
                    currentSecond = 0;//这时发生了跨天，当前秒数超过86399时，重置成0秒
                }
                counterValue = 0;
                seqCounter.set(0);
            }
        } else {//秒数改变了，重置计数器为100以内的一个随机起始值（极高极高的并发场景下，这里可以直接设置为0）
            counterValue = rnd.nextInt(100);
            seqCounter.set(counterValue);
        }
        //3)设置最后的秒数
        lastSecond.set(currentSecond);
        //4)生成返回值
        short maId = machineId;
        if (maId < 0 || maId > 2720) {
            maId = 0;
        }
        int returnValue = (currentSecond + maId * 5) * 1000 + counterValue;
        return new DecimalFormat("00000000").format(returnValue);
    }

}
