package com.millet.androidlib.Utils;

/**
 * Created by Administrator on 2017/5/3 0003.
 */

import android.content.Context;
import android.os.SystemClock;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * 日期时间工具类
 *
 * @author Administrator
 */
public class DateUtils {

    /**
     * 一秒钟
     */
    public static final int SECONDE_MINITE = 1000;
    /**
     * 一分钟
     */
    public static final long ONE_MINITE = 60 * SECONDE_MINITE;
    /**
     * 一小时
     */
    public static final long ONE_HOUR = 60 * ONE_MINITE;

    /**
     * {一天，一月，一年}
     */
    public static final long ONE_DAY = 24 * ONE_HOUR;
    public static final long ONE_MONTH = 30 * 24 * ONE_HOUR;
    public static final long ONE_YEAR = 12 * 30 * 24 * ONE_HOUR;
    /**
     * 系统采用东8时区
     */
    public static final long APP_SYSTEM_TIME_ZONE = 8 * ONE_HOUR;

    /**
     * 时间格式{年-月-日} eg. 1970-01-01
     */
    public static final String FMT_YMD = "yyyy-MM-dd";

    /**
     * 时间格式{月-日} eg. 01-01
     */
    public static final String FMT_MD = "MM-dd";

    /**
     * 时间格式{时:分} eg. 13:00
     */
    public static final String FMT_HM = "HH:mm";

    /**
     * 时间格式{年-月-日 时:分} eg. 1970-01-01 13:00
     */
    public static final String FMT_YMD_HM = "yyyy-MM-dd HH:mm";

    /**
     * 时间格式{时:分:秒} eg. 13:00:00
     */
    public static final String FMT_HMS = "HH:mm:ss";

    /**
     * 时间格式{分:秒} eg. 10:59
     */
    public static final String FMT_MS = "mm:ss";

    /**
     * 时间格式{秒} eg. 10:59
     */
    public static final String FMT_S = "ss";

    /**
     * 时间格式{年-月-日 时:分:秒} eg. 1970-01-01 13:00:00
     */
    public static final String FMT_YMD_HMS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间格式{年-月-日 时:分:秒:毫秒} eg. 1970-01-01 13:00:00:000
     */
    public static final String FMT_YMD_HMSS = "yyyy-MM-dd HH:mm:ss:SSS";

    /**
     * 当前开机时间和服务器的时间差
     */
    public static long timeA = Long.MIN_VALUE;

    /**
     * 格式化时长
     *
     * @param duration
     * @return hh:mm:ss
     */
    public static String formatDuration(long duration) {
        if (duration >= 3600) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d",
                    duration / 3600, (duration % 3600) / 60, duration % 60);
        } else if (duration >= 60) {
            return String.format(Locale.getDefault(), "%02d:%02d",
                    duration / 60, duration % 60);
        } else {
            return String.format(Locale.getDefault(), "%02d''", duration);
        }
    }

    /**
     * 格式化  秒格式00:00
     *
     * @param duration
     * @return
     */
    public static String formatDurationColon(long duration) {
        if (duration >= 3600) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d",
                    duration / 3600, (duration % 3600) / 60, duration % 60);
        } else if (duration >= 60) {
            return String.format(Locale.getDefault(), "%02d:%02d",
                    duration / 60, duration % 60);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", 0, duration);
        }
    }

    /**
     * 格式化剩余时间
     * {根据大小最终以年月日显示}
     *
     * @param duration
     * @return
     */
    public static String formatLeftTime(long duration) {
        StringBuffer buffer = new StringBuffer();

        if (duration >= ONE_YEAR) {
            buffer.append(Long.valueOf((duration + getTimeOffSet()) / ONE_YEAR) + "年");
            duration = duration % ONE_YEAR;
        }

        if (duration >= ONE_MONTH) {
            buffer.append(Long.valueOf((duration + getTimeOffSet()) / ONE_MONTH) + "月");
            duration = duration % ONE_MONTH;
        }

        if (duration >= ONE_DAY) {
            buffer.append(Long.valueOf((duration + getTimeOffSet()) / ONE_DAY) + "日");
            duration = duration % ONE_DAY;
        }

        buffer.append(Long.valueOf((duration + getTimeOffSet()) / ONE_HOUR) + "小时");

        return buffer.toString();
    }

    /**
     * 格式化{年-月-日}
     *
     * @param timestamp 时间戳毫秒数
     * @return yyyy-MM-dd
     */
    public static String formatYMD(String timestamp) {
        return new SimpleDateFormat(FMT_YMD, Locale.getDefault())
                .format(new Date(Long.valueOf(timestamp) + getTimeOffSet()));
    }

    /**
     * 格式化{月-日}
     *
     * @param timestamp
     * @return
     */
    public static String formatMD(String timestamp) {
        return new SimpleDateFormat(FMT_MD, Locale.getDefault())
                .format(new Date(Long.valueOf(timestamp) + getTimeOffSet()));
    }

    /**
     * 格式化{年}
     *
     * @param timestamp
     * @return
     */
    public static String formatYYYY(long timestamp) {
        return new SimpleDateFormat("yyyy", Locale.getDefault())
                .format(new Date(Long.valueOf(timestamp + getTimeOffSet())));
    }

    /**
     * 格式化{年-月-日}
     *
     * @param timestamp
     * @return
     */
    public static String formatYMD(long timestamp) {
        return new SimpleDateFormat(FMT_YMD, Locale.getDefault())
                .format(new Date(timestamp + getTimeOffSet()));
    }

    /**
     * 格式化{MM月dd日　周三}
     *
     * @param timestamp
     * @return
     */
    public static String formatMDW(long timestamp) {
        return new SimpleDateFormat("MM月dd日 E", Locale.getDefault())
                .format(new Date(Long.valueOf(timestamp + getTimeOffSet())));
    }

    /**
     * 格式化{MM月dd日}
     *
     * @param timestamp
     * @return
     */
    public static String formatMDC(long timestamp) {
        return new SimpleDateFormat("MM月dd日", Locale.getDefault())
                .format(new Date(Long.valueOf(timestamp + getTimeOffSet())));
    }

    /**
     * 格式化{dd  MM月}
     *
     * @param timestamp
     * @return
     */
    public static String formatDM(long timestamp) {
        return new SimpleDateFormat("dd MM月", Locale.getDefault())
                .format(new Date(Long.valueOf(timestamp + getTimeOffSet())));
    }

    /**
     * 格式化{yyyy年MM月dd日 时:分}
     *
     * @param timestamp
     * @return
     */
    public static String formatYMDHM(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.getDefault())
                .format(new Date(Long.valueOf(timestamp + getTimeOffSet())));
    }

    /**
     * 格式化{yyyy年mm月dd日}
     *
     * @param timestamp
     * @return
     */
    public static String formatYYYYMM2(long timestamp) {
        return new SimpleDateFormat("yyyy年MM月", Locale.getDefault())
                .format(new Date(Long.valueOf(timestamp + getTimeOffSet())));
    }

    /**
     * 格式化{yyyyMMdd-HHmmss}
     *
     * @param timestamp
     * @return
     */
    public static String formatYMDHMS(long timestamp) {
        return new SimpleDateFormat(FMT_YMD_HMS, Locale.getDefault())
                .format(new Date(Long.valueOf(timestamp + getTimeOffSet())));
    }

    /**
     * 格式化{MM-dd HH:mm}
     *
     * @param timestamp 时间戳毫秒数
     * @return MM-dd HH:mm
     */
    public static String formatMDHM(String timestamp) {
        return formatMDHM(Long.valueOf(timestamp));
    }

    /**
     * 格式化{MM-dd HH:mm}
     *
     * @param timestamp
     * @return
     */
    public static String formatMDHM(long timestamp) {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                .format(new Date(timestamp + getTimeOffSet()));
    }

    /**
     * 格式化{yyyy-MM-dd HH:mm}
     *
     * @param timestamp
     * @return
     */
    public static String formatYMDHMA(String timestamp) {
        return formatYMDHMA(Long.valueOf(timestamp));
    }

    /**
     * 格式化{yyyy-MM-dd HH:mm}
     *
     * @param timestamp
     * @return
     */
    public static String formatYMDHMA(long timestamp) {
        return new SimpleDateFormat(FMT_YMD_HM, Locale.ENGLISH)
                .format(new Date(timestamp + getTimeOffSet()));
    }

    /**
     * 格式化{月/日}
     *
     * @param timestamp
     * @return
     */
    public static String formatMD(long timestamp) {
        return new SimpleDateFormat("MM/dd", Locale.getDefault())
                .format(new Date(timestamp + getTimeOffSet()));
    }

    /**
     * 格式化{年/月}
     *
     * @param timestamp
     * @return
     */
    public static String formatYYYYMM(long timestamp) {
        return new SimpleDateFormat("yyyy/MM", Locale.getDefault())
                .format(new Date(timestamp + getTimeOffSet()));
    }

    /**
     * 格式化{HH:mm}
     *
     * @param timestamp 时间戳毫秒数
     * @return HH:mm
     */
    public static String formatHM(String timestamp) {
        return formatHM(Long.valueOf(timestamp));
    }

    /**
     * 格式化{HH:mm}
     *
     * @param timestamp
     * @return
     */
    public static String formatHM(long timestamp) {
        return new SimpleDateFormat(FMT_HM, Locale.getDefault())
                .format(new Date(timestamp + getTimeOffSet()));
    }

    public static long currentTime() {
        if (Long.MIN_VALUE != timeA) {
            return timeA + SystemClock.elapsedRealtime();// return
            // timeServer;
        } else {
            Calendar calendar = Calendar.getInstance();
            String[] strings = TimeZone.getAvailableIDs(3600 * 8 * 1000);
            if (strings.length > 0) {
                // s4 ArrayIndexOutOfBoundsException
                calendar.setTimeZone(TimeZone.getTimeZone(strings[0]));
            }
            return calendar.getTimeInMillis();
        }
    }

    /**
     * getTimesByDate(获取制定日期0点得时间戳) (这里描述这个方法适用条件 – 可选)
     *
     * @param year
     * @param month
     * @param day
     * @return long
     * @throws
     * @since 1.0.0
     */
    public static long getTimesByDate(int year, int month, int day) {
        DateFormat format = new SimpleDateFormat(FMT_YMD, Locale.getDefault());
        String str = String.format(Locale.getDefault(), "%d-%02d-%02d", year,
                month, day);
        java.util.Date dateTime = null;
        long time = 0l;
        try {
            dateTime = format.parse(str);
            time = dateTime.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // System.out.println("Time:" + time);

        return time;
    }

    /**
     * getDateByteTime(根据时间戳返回时间或者日期) (这里描述这个方法适用条件 – 可选)
     *
     * @param time
     * @return String
     * @throws
     * @since 1.0.0
     */
    public static String getDateByteTime(long time) {
        long todayTime0 = getTodayHour0Times(DateUtils.currentTime());

        String str = null;
        if (time < todayTime0) {
            // 今天以前 返回 YYYY-MM-DD
            str = formatYMDHM(time);
            return str;
        } else {
            // 今天 返回 HH:MM
            str = formatHM(String.valueOf(time));
            return str;
        }
    }

    /**
     * 获取时间 当天是具体时间，非当天是日期+具体时间
     *
     * @param time
     * @return
     */
    public static String getCommentDate(long time) {
        long todayTime0 = getTodayHour0Times(DateUtils.currentTime());

        String str = null;
        if (time < todayTime0) {
            // 今天以前 返回 YYYY-MM-DD
            str = formatMDHM(String.valueOf(time));
            return str;
        } else {
            // 今天 返回 HH:MM
            str = formatHM(String.valueOf(time));
            return str;
        }
    }

    /**
     * 当天显示时间  否则显示月日
     *
     * @param time
     * @return
     */
    public static String getDiaryDate(long time) {
        long todayTime0 = getTodayHour0Times(DateUtils.currentTime());

        String str = null;
        if (time < todayTime0) {
            // 今天以前 返回MM-DD
            str = formatMD(String.valueOf(time));
            return str;
        } else {
            // 今天 返回 HH:MM
            str = formatHM(String.valueOf(time));
            return str;
        }
    }

    /**
     * 获取月份 eg:JAN
     *
     * @param times
     * @return
     */
    public static String getMonthEn(long times) {
        int month = Integer.parseInt(
                new SimpleDateFormat("MM", Locale.getDefault()).format(
                        new Date(Long.valueOf(times + getTimeOffSet()))));
        switch (month) {
            case 1:
                return "JAN";
            case 2:
                return "FEB";
            case 3:
                return "MAR";
            case 4:
                return "APR";
            case 5:
                return "MAY";
            case 6:
                return "JUN";
            case 7:
                return "JUL";
            case 8:
                return "AUG";
            case 9:
                return "SEP";
            case 10:
                return "OCT";
            case 11:
                return "NOV";
            case 12:
                return "DEC";
        }
        return "";
    }

    /**
     * 获取月份 eg.  一月
     *
     * @param times
     * @return
     */
    public static String getMonthZh(long times) {
        int month = Integer.parseInt(
                new SimpleDateFormat("MM", Locale.getDefault()).format(
                        new Date(Long.valueOf(times + getTimeOffSet()))));
        switch (month) {
            case 1:
                return "一月";
            case 2:
                return "二月";
            case 3:
                return "三月";
            case 4:
                return "四月";
            case 5:
                return "五月";
            case 6:
                return "六月";
            case 7:
                return "七月";
            case 8:
                return "八月";
            case 9:
                return "九月";
            case 10:
                return "十月";
            case 11:
                return "十一月";
            case 12:
                return "十二月";
        }
        return "";
    }

    public static String getMonthAndDay(long times) {
        DateFormat shortDF = DateFormat.getDateTimeInstance(
                SimpleDateFormat.SHORT,
                SimpleDateFormat.SHORT,
                Locale.getDefault());
        return shortDF.format(new Date(times + getTimeOffSet()));
//        return new SimpleDateFormat("dd MM", Locale.getDefault()).format(
//                new Date(times + getTimeOffSet()));
    }

    public static int getMonthDay(long times) {
        int day = Integer.parseInt(
                new SimpleDateFormat("dd", Locale.getDefault()).format(
                        new Date(Long.valueOf(times + getTimeOffSet()))));
        return day;
    }

    /**
     * 根据毫秒数获取0点毫秒数 getTodayHour0Times(这里用一句话描述这个方法的作用) (这里描述这个方法适用条件 – 可选)
     *
     * @param times
     * @return long
     * @throws
     * @author wanglinqi
     * @since 1.0.0
     */
    public static long getTodayHour0Times(long times) {
        times += getTime0OffSet();
        long todayTime0 = times / ONE_DAY * ONE_DAY - APP_SYSTEM_TIME_ZONE;
        // 算0
        // 的时候在GTM8和时区是无关的在同一时区下/×之后
        // 但是对于不同GTM+8 是要算的。
        return todayTime0;
    }

    public static long getTodayYear0Times(long times) {
        times += getTime0OffSet();
        long yearTime = ONE_DAY * 365;
        long todayYearTime0 = times / yearTime * yearTime;
        return todayYearTime0;
    }

    /**
     * 显示的时候用的0时间计算,format可以算显示,但是不能算出正确的当天0点
     *
     * @return
     */
    private static long getTime0OffSet() {
        TimeZone tz = TimeZone.getDefault();
        int hourOffSet = (tz.getRawOffset());
        return hourOffSet;
    }

    public static String getTitle(long time, Context context, int strHour,
                                  int strMinite, int strDay) {
        long curTime = DateUtils.currentTime();
        long curDateStart = DateUtils.getTodayHour0Times(curTime);
        long curYearStart = DateUtils.getTodayYear0Times(curTime);
        if (curTime - time < DateUtils.ONE_HOUR) {
            long i = (curTime - time) / DateUtils.ONE_MINITE;
            if (i < 1) {
                i++;
            }
            return i + context.getString(strMinite);
        } else if (curDateStart > time)
        // 跨天了
        {
            String month = DateUtils.getMonthEn(time);
            int day = DateUtils.getMonthDay(time);
            return month + "　" + ((day < 10) ? ("0" + day) : day);
        } else if (curYearStart > time)
        // 跨年了
        {
            return DateUtils.formatYYYY(time);
        } else {
            return String.valueOf((curTime - time) / DateUtils.ONE_HOUR)
                    + context.getString(strHour);
        }
    }

    /**
     * 显示的时候用的时区计算,由于用的是format,它可以把时区算对,所以不用处理,
     *
     * @return
     */
    private static long getTimeOffSet() {
        // 显示的时候用format应该是写死了，如设置到日本，在我们的几台机器上测，
        // TimeZone.getDefault()
        // libcore.util.ZoneInfo[id="Asia/Tokyo",mRawOffset=32400
        // 000,mEarliestRawOffset=32400000,mUseDst=false,mDstSavings=3600
        // 000,transitions=9]
        // TimeZone tz = TimeZone.getDefault();
        // int hourOffSet = (int) (tz.getRawOffset() - APP_SYSTEM_TIME_ZONE);
        // return hourOffSet;
        return 0;
    }

    /**
     * 格式化时间
     *
     * @param timestamp 时间戳毫秒数
     * @param fmt       时间格式
     * @return 格式化以后的时间字符串
     */
    public static String format(String timestamp, String fmt) {
        return new SimpleDateFormat(fmt, Locale.getDefault())
                .format(new Date(Long.valueOf(timestamp) + getTimeOffSet()));
    }

    /**
     * 按照指定的格式格式化时间
     *
     * @param timestamp 时间戳毫秒数
     * @param fmt       时间格式
     * @return 格式化以后的时间字符串
     */
    public static String format(long timestamp, String fmt) {
        return new SimpleDateFormat(fmt, Locale.getDefault())
                .format(new Date(timestamp + getTimeOffSet()));
    }

    /**
     * 按照指定的格式格式化当前时间
     *
     * @param fmt 时间格式
     * @return 格式化以后的时间字符串
     */
    public static String format(String fmt) {
        return new SimpleDateFormat(fmt, Locale.getDefault())
                .format(new Date(currentTime() + getTimeOffSet()));
    }

    /**
     * 获取年份
     *
     * @return
     */
    public static int getYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取月份
     *
     * @return
     */
    public static int getMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH);
    }

    /**
     * 获取日分
     *
     * @return
     */
    public static int getDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取日期  eg:2015/02/04
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String getDate(int year, int month, int day) {
        return String.format("%d/%02d/%02d", year, month, day);
    }

    /**
     * 获取年月日{201511}
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String getCalendarDayTag(int year, int month, int day) {
        return String.valueOf(year) + String.valueOf(month)
                + String.valueOf(day);
    }

    /**
     * 是不是今天
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static boolean isToday(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) == year
                && calendar.get(Calendar.MONTH) == month
                && calendar.get(Calendar.DAY_OF_MONTH) == day;
    }

    public static boolean isToday(long timeStamp) {
        boolean isToday = false;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = simpleDateFormat.format(new Date(timeStamp));
        if (year == Integer.parseInt(date.split("-")[0])
                && month == Integer.parseInt(date.split("-")[1])
                && day == Integer.parseInt(date.split("-")[2])) {
            isToday = true;
        }
        return isToday;
    }

    /**
     * 判断是否为同一天
     *
     * @param timeLeft
     * @param timeRight
     * @return
     */
    public static boolean isTheSameDay(long timeLeft, long timeRight) {
        return formatYMD(timeLeft).equals(formatYMD(timeRight));
    }

    /**
     * 获取周一到周日的索引值  {索引值从周一到周日  分别是0到6}
     *
     * @return
     */
    public static int getIndexToDayInWeek() {
        Calendar calendar = Calendar.getInstance();
        int index = calendar.get(Calendar.DAY_OF_WEEK);
        if (index == 1)
            return 6;
        return index - 2;
    }

    /**
     * 获取指定时间的星期
     */
    public static String getWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int index = calendar.get(Calendar.DAY_OF_WEEK);
        switch (index) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "未知";
        }
    }
}

