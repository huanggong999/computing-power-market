package com.lingyang.common.core.utils;

import com.lingyang.common.core.exception.MethodExecutionException;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author scrm
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    public static String YYYY = "yyyy";
    public static String YYYY_MM = "yyyy-MM";
    public static String YYYY_MM_DD = "yyyy-MM-dd";
    public static String YYYY_MM_DD_2 = "yyyy.MM.dd";
    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate(String pattern) {
        return dateTimeNow(pattern);
    }

    /**
     * 获取日期
     * @param timeMillis 时间戳
     * @return Date
     */
    public static Date getDate(Long timeMillis) {
        return new Date(timeMillis);
    }

    public static String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }


    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }


    /**
     * 获取指定时间
     *
     * @param date   指定时间
     * @param number 数字，正数为之后，负数为之前
     * @param minute 单位
     *               Calendar.HOUR 小时
     *               Calendar.MINUTE 分钟
     *               Calendar.SECOND 秒
     *               Calendar.MILLISECOND 毫秒
     * @return 时间
     */
    public static Date getDate(Date date, int number, int minute) {
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.setTime(date);
        beforeTime.add(minute, number);
        return beforeTime.getTime();
    }


    /**
     * 获取指定单位
     *
     * @param nowDate  时间
     * @param calendar 单位
     *                 Calendar.HOUR 小时
     *                 Calendar.MINUTE 分钟
     *                 Calendar.SECOND 秒
     *                 Calendar.MILLISECOND 毫秒
     *                 Calendar.DAY_OF_WEEK 周 （ 周日为一周的开始）
     */
    public static int getDate(Date nowDate, int calendar) {
        final Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(nowDate);
        return c.get(calendar);
    }

    /**
     * 获取时间date1与date2相差的年数
     *
     * @param date1 起始时间
     * @param date2 结束时间
     * @return 返回相差的秒数
     */
    public static String getOffYear(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        return ChronoUnit.YEARS.between(localDate1, localDate2) + "";
    }

    /**
     * 获取时间date1与date2相差的秒数
     *
     * @param date1 起始时间
     * @param date2 结束时间
     * @return 返回相差的秒数
     */
    public static int getOffsetSeconds(Date date1, Date date2) {
        return (int) ((date2.getTime() - date1.getTime()) / 1000);
    }

    /**
     * 获取时间date1与date2相差的分钟数
     *
     * @param date1 起始时间
     * @param date2 结束时间
     * @return 返回相差的分钟数
     */
    public static int getOffsetMinutes(Date date1, Date date2) {
        return getOffsetSeconds(date1, date2) / 60;
    }

    /**
     * 获取时间date1与date2相差的小时数
     *
     * @param date1 起始时间
     * @param date2 结束时间
     * @return 返回相差的小时数
     */
    public static int getOffsetHours(Date date1, Date date2) {
        return getOffsetMinutes(date1, date2) / 60;
    }

    /**
     * 获取时间date1与date2相差的天数
     *
     * @param date1 起始时间
     * @param date2 结束时间
     * @return 返回相差的小时数
     */
    public static int getOffsetDays(Date date1, Date date2) {
        return getOffsetMinutes(date1, date2) / 60 / 24;
    }

    public static Date setStartTime(Date date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD);
            String dateF = dateFormat.format(date);
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
            return dateTimeFormat.parse(dateF + " 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date setEndTime(Date date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD);
            String dateF = dateFormat.format(date);
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
            return dateTimeFormat.parse(dateF + " 23:59:59");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 修改指定时间
     *
     * @param nowDate  时间
     * @param value    时间 正数为添加，负数为减少
     * @param calendar 单位
     *                 Calendar.HOUR 小时
     *                 Calendar.MINUTE 分钟
     *                 Calendar.SECOND 秒
     *                 Calendar.MILLISECOND 毫秒
     */
    public static Date updateDate(Date nowDate, int value, int calendar) {
        final Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(nowDate);
        c.add(calendar, value);
        return c.getTime();
    }

    public static Date toDate(String dateStr, String pattern) {
        try {
            return parseDate(dateStr, pattern);
        } catch (ParseException e) {
            throw new MethodExecutionException("日期格式化失败：" + e);
        }
    }

    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }
}
