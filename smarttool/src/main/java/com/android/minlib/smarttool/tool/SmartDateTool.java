package com.android.minlib.smarttool.tool;
import com.android.minlib.smarttool.tool.assist.DateDifference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 日期操作工具类.
 * SimpleDateFormat函数语法：
 * G 年代标志符
 * y 年
 * M 月
 * d 日
 * h 时 在上午或下午 (1~12)
 * H 时 在一天中 (0~23)
 * m 分
 * s 秒
 * S 毫秒
 * E 星期
 * D 一年中的第几天
 * F 一月中第几个星期几
 * w 一年中第几个星期
 * W 一月中第几个星期
 * a 上午 / 下午 标记符
 * k 时 在一天中 (1~24)
 * K 时 在上午或下午 (0~11)
 * z 时区
 */
public class SmartDateTool {


    private SmartDateTool() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    /**
     * 秒与毫秒的倍数
     */
    public static final long SEC = 1000;
    /**
     * 分与毫秒的倍数
     */
    public static final long MIN = SEC * 60;
    /**
     * 时与毫秒的倍数
     */
    public static final long HOUR = MIN * 60;
    /**
     * 天与毫秒的倍数
     */
    public static final long DAY = HOUR * 24;

    /**
     * 周与毫秒的倍数
     */
    public static final long WEEK = DAY * 7;

    /**
     * 月与毫秒的倍数
     */
    public static final long MONTH = DAY * 30;

    /**
     * 年与毫秒的倍数
     */
    public static final long YEAR = DAY * 365;

    /**
     * 默认格式
     */
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * SimpleDateFormat不是线程安全的，以下是线程安全实例化操作
     */
    private static final ThreadLocal<SimpleDateFormat> local = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat();
        }
    };

    /**
     * 获取SimpleDateFormat实例
     *
     * @param pattern 模式串
     * @return
     */
    public static SimpleDateFormat getSimpleDateFormat(String pattern) {
        SimpleDateFormat format = local.get();
        format.applyPattern(pattern);
        return format;
    }

    /**
     * 获取当前时间的字符串
     * yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getCurrentDate() {
        return format(new Date(), DEFAULT_PATTERN);
    }

    /**
     * 获取表示当前时间的字符串
     *
     * @param pattern 模式串
     * @return
     */
    public static String getCurrentDate(String pattern) {
        return format(new Date(), pattern);
    }

    /**
     * 日期时间格式化
     * yyyy-MM-dd HH:mm:ss
     * @param date Date
     * @return
     */
    public static String format(Date date) {
        SimpleDateFormat format = getSimpleDateFormat(DEFAULT_PATTERN);
        return format.format(date);
    }

    /**
     * 日期时间格式化
     *
     * @param date    Date
     * @param pattern 模式串
     * @return
     */
    public static String format(Date date, String pattern) {
        SimpleDateFormat format = getSimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 将时间戳转为时间字符串
     * <p>格式为yyyy-MM-dd HH:mm:ss</p>
     *
     * @param millis 毫秒时间戳
     * @return 时间字符串
     */
    public static String millis2String(long millis) {
        SimpleDateFormat format = getSimpleDateFormat(DEFAULT_PATTERN);
        return format.format(new Date(millis));
    }

    /**
     * 将时间戳转为时间字符串
     * <p>格式为pattern</p>
     *
     * @param millis  毫秒时间戳
     * @param pattern 时间格式
     * @return 时间字符串
     */
    public static String millis2String(long millis, String pattern) {
        SimpleDateFormat format = getSimpleDateFormat(pattern);
        return format.format(new Date(millis));
    }

    /**
     * 将时间字符串转为时间戳
     * <p>time格式为yyyy-MM-dd HH:mm:ss</p>
     *
     * @param time 时间字符串
     * @return 毫秒时间戳
     */
    public static long string2Millis(String time) {
        return string2Millis(time, DEFAULT_PATTERN);
    }

    /**
     * 将时间字符串转为时间戳
     * <p>time格式为pattern</p>
     *
     * @param time    时间字符串
     * @param pattern 时间格式
     * @return 毫秒时间戳
     */
    public static long string2Millis(String time, String pattern) {
        SimpleDateFormat format = getSimpleDateFormat(pattern);
        try {
            return format.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 将时间字符串转为Date类型
     * <p>time格式为yyyy-MM-dd HH:mm:ss</p>
     *
     * @param time 时间字符串
     * @return Date类型
     */
    public static Date string2Date(String time) {
        return string2Date(time, DEFAULT_PATTERN);
    }

    /**
     * 将时间字符串转为Date类型
     * <p>time格式为pattern</p>
     *
     * @param time    时间字符串
     * @param pattern 时间格式
     * @return Date类型
     */
    public static Date string2Date(String time, String pattern) {
        return new Date(string2Millis(time, pattern));
    }

    /**
     * 将时间字符串转为Date类型
     * <p>time格式为UTC</p>
     *
     * @param time 时间字符串
     * @return
     */
    public static Date utcString2Date(String time) throws ParseException {
        SimpleDateFormat format = getSimpleDateFormat(PATTERN_UTC);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.parse(time);
    }

    /**
     * 将Date类型转为时间字符串
     * <p>格式为yyyy-MM-dd HH:mm:ss</p>
     *
     * @param date Date类型时间
     * @return 时间字符串
     */
    public static String date2String(Date date) {
        return date2String(date, DEFAULT_PATTERN);
    }

    /**
     * 将Date类型转为时间字符串
     * <p>格式为pattern</p>
     *
     * @param date    Date类型时间
     * @param pattern 时间格式
     * @return 时间字符串
     */
    public static String date2String(Date date, String pattern) {
        SimpleDateFormat format = getSimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 将Date类型转为时间戳
     *
     * @param date Date类型时间
     * @return 毫秒时间戳
     */
    public static long date2Millis(Date date) {
        return date.getTime();
    }

    /**
     * 将时间戳转为Date类型
     *
     * @param millis 毫秒时间戳
     * @return Date类型时间
     */
    public static Date millis2Date(long millis) {
        return new Date(millis);
    }

    /**
     * 获取与当前时间的时间差
     *
     * @param date 需要计算的时间，应小于当前时间
     * @return DateDifference实体类，内封装有获取相差的毫秒、秒、分钟、小时、天的方法
     */
    public static DateDifference getTwoDataDifference(Date date) {
        return getTwoDataDifference(new Date(), date);
    }

    /**
     * 获取与当前时间的时间差
     *
     * @param str 需要计算的时间，应小于当前时间
     * @return DateDifference实体类，内封装有获取相差的毫秒、秒、分钟、小时、天的方法
     */
    public static DateDifference getTwoDataDifference(String str) {
        return getTwoDataDifference(new Date(), string2Date(str));
    }


    /**
     * 得到二个日期间的时间差
     *
     * @param str1 两个时间中较大的那个
     * @param str2 两个时间中较小的那个
     * @return DateDifference实体类，内封装有获取相差的毫秒、秒、分钟、小时、天的方法
     */
    public static DateDifference getTwoDataDifference(String str1, String str2) {
        return getTwoDataDifference(string2Date(str1), string2Date(str2));
    }

    /**
     * 得到二个日期间的时间差
     *
     * @param date1 两个时间中较大的那个
     * @param date2 两个时间中较小的那个
     * @return DateDifference实体类，内封装有获取相差的毫秒、秒、分钟、小时、天的方法
     */
    public static DateDifference getTwoDataDifference(Date date1, Date date2) {
        DateDifference difference = new DateDifference();
        long millis = Math.abs(date1.getTime() - date2.getTime());
        difference.setMillisecond(millis);
        difference.setSecond(millis/SEC);
        difference.setMinute(millis/MIN);
        difference.setHour(millis/HOUR);
        difference.setDay(millis/DAY);
        return difference;
    }


    /**
     * 判断是否同一天
     * <p>time格式为yyyy-MM-dd HH:mm:ss</p>
     *
     * @param time 时间字符串
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isSameDay(String time) {
        return isSameDay(string2Millis(time, DEFAULT_PATTERN));
    }

    /**
     * 判断是否同一天
     * <p>time格式为pattern</p>
     *
     * @param time    时间字符串
     * @param pattern 时间格式
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isSameDay(String time, String pattern) {
        return isSameDay(string2Millis(time, pattern));
    }

    /**
     * 判断是否同一天
     *
     * @param date Date类型时间
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isSameDay(Date date) {
        return isSameDay(date.getTime());
    }

    /**
     * 判断是否同一天
     *
     * @param millis 毫秒时间戳
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isSameDay(long millis) {
        long wee = (System.currentTimeMillis() / DAY) * DAY;
        return millis >= wee && millis < wee + DAY;
    }

    /**
     * 判断是否闰年
     * <p>time格式为yyyy-MM-dd HH:mm:ss</p>
     *
     * @param time 时间字符串
     * @return {@code true}: 闰年<br>{@code false}: 平年
     */
    public static boolean isLeapYear(String time) {
        return isLeapYear(string2Date(time, DEFAULT_PATTERN));
    }

    /**
     * 判断是否闰年
     * <p>time格式为pattern</p>
     *
     * @param time    时间字符串
     * @param pattern 时间格式
     * @return {@code true}: 闰年<br>{@code false}: 平年
     */
    public static boolean isLeapYear(String time, String pattern) {
        return isLeapYear(string2Date(time, pattern));
    }

    /**
     * 判断是否闰年
     *
     * @param date Date类型时间
     * @return {@code true}: 闰年<br>{@code false}: 平年
     */
    public static boolean isLeapYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        return isLeapYear(year);
    }

    /**
     * 判断是否闰年
     *
     * @param millis 毫秒时间戳
     * @return {@code true}: 闰年<br>{@code false}: 平年
     */
    public static boolean isLeapYear(long millis) {
        return isLeapYear(millis2Date(millis));
    }

    /**
     * 判断是否闰年
     *
     * @param year 年份
     * @return {@code true}: 闰年<br>{@code false}: 平年
     */
    public static boolean isLeapYear(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    /**
     * 获取星期
     * <p>time格式为yyyy-MM-dd HH:mm:ss</p>
     *
     * @param time 时间字符串
     * @return 星期
     */
    public static String getWeek(String time) {
        return getWeek(string2Date(time, DEFAULT_PATTERN));
    }

    /**
     * 获取星期
     * <p>time格式为pattern</p>
     *
     * @param time    时间字符串
     * @param pattern 时间格式
     * @return 星期
     */
    public static String getWeek(String time, String pattern) {
        return getWeek(string2Date(time, pattern));
    }

    /**
     * 获取星期
     *
     * @param date Date类型时间
     * @return 星期
     */
    public static String getWeek(Date date) {
        return new SimpleDateFormat("EEEE", Locale.getDefault()).format(date);
    }

    /**
     * 获取星期
     *
     * @param millis 毫秒时间戳
     * @return 星期
     */
    public static String getWeek(long millis) {
        return getWeek(new Date(millis));
    }


}
