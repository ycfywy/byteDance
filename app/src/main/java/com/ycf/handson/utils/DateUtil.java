package com.ycf.handson.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 格式化时间戳为相对时间工具类
 */
public class DateUtil {

    private static final long DAY_MILLIS = 24 * 60 * 60 * 1000L;

    /**
     * 将时间戳格式化为相对时间字符串。
     * 规则：
     * 1. 当天：显示具体时间 (HH:mm)
     * 2. 昨天：显示 "昨天 HH:mm"
     * 3. 2天前到6天前：显示 "X天前"
     * 4. 7天前及更早：显示具体日期 (MM-dd)
     *
     * @param timestamp 毫秒级时间戳
     * @return 格式化后的时间字符串
     */
    public static String formatRelativeTime(long timestamp) {
        long now = System.currentTimeMillis();

        // 目标时间如果大于当前时间（未来时间），简单返回完整的 MM-dd HH:mm
        if (timestamp > now) {
            return new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(new Date(timestamp));
        }

        // 1. 获取当前时间的日历对象

        long daysDiff = getDaysDiff(timestamp, now);

        // A. 当天
        if (daysDiff == 0) {
            // 规则 1：当天 (HH:mm)
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timestamp));
        }

        // B. 昨天
        else if (daysDiff == 1) {
            // 规则 2：昨天 (昨天 HH:mm)
            return "昨天 " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timestamp));
        }

        // C. 7天内（2天前到6天前）
        else if (daysDiff > 1 && daysDiff < 7) {
            // 规则 3：2天前到6天前 (X天前)
            return daysDiff + "天前";
        }

        // D. 7天及更早
        else {
            // 规则 4：7天前及更早 (MM-dd)
            return new SimpleDateFormat("MM-dd", Locale.getDefault()).format(new Date(timestamp));
        }
    }

    private static long getDaysDiff(long timestamp, long now) {
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTimeInMillis(now);

        // 2. 获取目标时间戳的日历对象
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTimeInMillis(timestamp);

        // 3. 计算日历日期的差异（基于日历日的午夜时间差）

        // 将两个时间都调整到当日的零点（00:00:00.000）
        Calendar midnightNow = (Calendar) nowCalendar.clone();
        midnightNow.set(Calendar.HOUR_OF_DAY, 0);
        midnightNow.set(Calendar.MINUTE, 0);
        midnightNow.set(Calendar.SECOND, 0);
        midnightNow.set(Calendar.MILLISECOND, 0);

        Calendar midnightTarget = (Calendar) targetCalendar.clone();
        midnightTarget.set(Calendar.HOUR_OF_DAY, 0);
        midnightTarget.set(Calendar.MINUTE, 0);
        midnightTarget.set(Calendar.SECOND, 0);
        midnightTarget.set(Calendar.MILLISECOND, 0);

        // 计算两个午夜时间之间的毫秒差，再转换为天数
        long diff = midnightNow.getTimeInMillis() - midnightTarget.getTimeInMillis();
        long daysDiff = diff / DAY_MILLIS;
        return daysDiff;
    }
}