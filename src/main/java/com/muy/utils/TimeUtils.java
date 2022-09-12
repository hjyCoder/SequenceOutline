package com.muy.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * @Author jiyanghuang
 * @Date 2022/4/19 2:39 PM
 */
public class TimeUtils {

    public static final String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    public static String YMD = "yyyy-MM-dd";
    public static String YMDH = "yyyy-MM-dd HH";
    public static String YMDHM = "yyyy-MM-dd HH:mm";
    public static String YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static String YMDHMSZ = "yyyy-MM-dd HH:mm:ss zzzz";
    public static String EYMDHMSZ = "EEEE yyyy-MM-dd HH:mm:ss zzzz";
    public static String YMDHMSSZ = "yyyy-MM-dd HH:mm:ss.SSSZ";

    public static void main(String[] args) {
        Date date = new Date();
        System.out.println(week(date));
        System.out.println(hh(date));
    }

    public static String week(Date date) {
        if (DateUtils.isSameDay(date, new Date())) {
            return "今天";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    /**
     * 注意单位是毫秒
     *
     * @param times
     * @return
     */
    public static String week(Long times) {
        Date newDate = new Date(times);
        if (DateUtils.isSameDay(newDate, new Date())) {
            return "今天";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(newDate);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    public static String hh(Date date) {
        String re = DateFormatUtils.format(date.getTime(), "HH:mm");
        return re;
    }

    /**
     * 单位是毫秒
     *
     * @param times
     * @return
     */
    public static String hh(Long times) {
        String re = DateFormatUtils.format(times, "HH:mm");
        return re;
    }


    public static String ymd(Long date) {
        return DateFormatUtils.format(date, YMD);
    }

    public static String ymdh(Long date) {
        return DateFormatUtils.format(date, YMD);
    }

    public static String ymdhm(Long date) {
        return DateFormatUtils.format(date, YMDHM);
    }

    public static String ymdhms(Long date) {
        return DateFormatUtils.format(date, YMDHMS);
    }

    public static String ymdhmsz(Long date) {
        return DateFormatUtils.format(date, YMDHMSZ);
    }

    public static String eymdhmsz(Long date) {
        return DateFormatUtils.format(date, EYMDHMSZ);
    }

    public static String ymdhmssz(Long date) {
        return DateFormatUtils.format(date, YMDHMSSZ);
    }

    public static Long ymdLong(String formatDate) {
        try {
            return DateUtils.parseDate(formatDate, YMD).getTime();
        } catch (Exception ex) {
            return -1L;
        }
    }

    public static Long ymdhLong(String formatDate) {
        try {
            return DateUtils.parseDate(formatDate, YMDH).getTime();
        } catch (Exception ex) {
            return -1L;
        }
    }

    public static Long ymdhmLong(String formatDate) {
        try {
            return DateUtils.parseDate(formatDate, YMDHM).getTime();
        } catch (Exception ex) {
            return -1L;
        }
    }

    public static Long ymdhmsLong(String formatDate) {
        try {
            return DateUtils.parseDate(formatDate, YMDHMS).getTime();
        } catch (Exception ex) {
            return -1L;
        }
    }

    public static Long ymdhmszLong(String formatDate) {
        try {
            return DateUtils.parseDate(formatDate, YMDHMSZ).getTime();
        } catch (Exception ex) {
            return -1L;
        }
    }

    public static Long eymdhmszLong(String formatDate) {
        try {
            return DateUtils.parseDate(formatDate, EYMDHMSZ).getTime();
        } catch (Exception ex) {
            return -1L;
        }
    }

    public static Long ymdhmsszLong(String formatDate) {
        try {
            return DateUtils.parseDate(formatDate, YMDHMSSZ).getTime();
        } catch (Exception ex) {
            return -1L;
        }
    }

    public static Date ymdDate(String formatDate) {
        try {
            return DateUtils.parseDate(formatDate, YMD);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Date ymdhDate(String formatDate) {
        try {
            return DateUtils.parseDate(formatDate, YMDH);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Date ymdhmDate(String formatDate) {
        try {
            return DateUtils.parseDate(formatDate, YMDHM);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Date ymdhmsDate(String formatDate) {
        try {
            return DateUtils.parseDate(formatDate, YMDHMS);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Date ymdhmszDate(String formatDate) {
        try {
            return DateUtils.parseDate(formatDate, YMDHMSZ);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Date eymdhmszDate(String formatDate) {
        try {
            return DateUtils.parseDate(formatDate, EYMDHMSZ);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Date ymdhmsszDate(String formatDate) {
        try {
            return DateUtils.parseDate(formatDate, YMDHMSSZ);
        } catch (Exception ex) {
            return null;
        }
    }
}
