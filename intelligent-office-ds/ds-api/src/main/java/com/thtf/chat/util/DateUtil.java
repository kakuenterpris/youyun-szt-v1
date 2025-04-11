package com.thtf.chat.util;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Calendar;

/**
 * Author：PingY
 * Package：com.thtf.chat.util
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

    /**
     * 获取时间范围
     * @param timestamp
     * @return 当天、本周、近一年、更早
     */
    public static String getTimeRange(Long timestamp){
        // 添加时间范围判断
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        Calendar now = Calendar.getInstance();
        String timeRange = "";
        // 判断当天
        if (cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                && cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
            timeRange = "当天";
        }
        // 判断本周
        else if (cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                && cal.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)) {
            timeRange = "本周";
        }
        // 判断近一年
        else if ((now.getTimeInMillis() - timestamp) < 365L * 24 * 60 * 60 * 1000) {
            timeRange = "近一年";
        }
        // 更早
        else {
            timeRange = "更早";
        }
        return timeRange;
    }





//    public static void main(String[] args) {
//        System.out.println(millisecondConversionTime(30000L));;
//    }
}
