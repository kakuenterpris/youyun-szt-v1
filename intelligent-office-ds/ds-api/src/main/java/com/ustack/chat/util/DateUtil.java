package com.ustack.chat.util;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Author：PingY
 * Package：com.ustack.chat.util
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


    public static String getTimeRangeNew(Long timestamp){

        // 添加空值校验
        if (timestamp == null || timestamp <= 0) {
            return "无效时间";
        }
        // 统一使用上海时区
        TimeZone shanghai = TimeZone.getTimeZone("Asia/Shanghai");

        // 添加时间范围判断
        Calendar cal = Calendar.getInstance(shanghai);
        cal.setTimeInMillis(timestamp);
        Calendar now = Calendar.getInstance(shanghai);

        // 设置中国周规则（周一作为周首日，符合ISO标准）
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setMinimalDaysInFirstWeek(4);
        now.setFirstDayOfWeek(Calendar.MONDAY);
        now.setMinimalDaysInFirstWeek(4);
        Date time = cal.getTime();
        System.out.println(time);
        String timeRange = "";

        // 判断当天
        if (cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                && cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
            timeRange = "当天";
        }
        // 判断近一周（今天往前推6天）
        else {
            if (cal.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                    && 0 < now.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR)
                    && now.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR) < 7
            ) {
                timeRange = "近一周";
            }
        }
        // 判断近一年（排除近一周）
        if (timeRange.isEmpty() && (now.getTimeInMillis() - timestamp) < 365L * 24 * 60 * 60 * 1000) {
            timeRange = "近一年";
        }
        // 更早
        else if (timeRange.isEmpty()) {
            timeRange = "更早";
        }
        return timeRange;
    }


//    public static void main(String[] args) {
//        System.out.println(millisecondConversionTime(30000L));;
//    }
}
