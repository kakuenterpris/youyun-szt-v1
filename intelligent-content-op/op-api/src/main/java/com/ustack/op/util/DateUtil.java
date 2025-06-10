package com.ustack.op.util;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * Author：PingY
 * Package：com.ustack.op.util
 * Project：intelligent-office-platform
 * Classname：DateUtil
 * Date：2025/3/27  17:31
 * Description:
 */
@Slf4j
public class DateUtil {

    /**
     * 毫秒转时长 格式：xx:xx:xx
     *
     * @param dateMillisecond
     * @return
     */
    public static String millisecondConversionTime(Long dateMillisecond) {

        if (dateMillisecond == null || dateMillisecond == 0){
            return "";
        }
        Duration duration = Duration.ofMillis(dateMillisecond);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();    // 分钟部分（去小时后的余数）
        long seconds = duration.toSecondsPart();    // 秒部分（去分钟后的余数）
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

//    public static void main(String[] args) {
//        System.out.println(millisecondConversionTime(30000L));;
//    }
}
