package com.test.utils;

import com.test.constants.SystemConstants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FindDateStatusUtil {

    public static int weekNum(Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        int week;
        week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (week == 0) week = 7;
        return week;
    }

    public static Map<String, String> getWeekDate(Date date) {
        Map<String, String> map = new HashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.MONDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        if (dayWeek == 1) dayWeek = 8;

        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - dayWeek);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        Date mondayDate = cal.getTime();
        String weekBegin = sdf.format(mondayDate);

        cal.add(Calendar.DATE, 4 + cal.getFirstDayOfWeek());
        Date sundayDate = cal.getTime();
        String weekEnd = sdf.format(sundayDate);

        map.put(SystemConstants.MONDAY_DATE, weekBegin);
        map.put(SystemConstants.SUNDAY_DATE, weekEnd);
        return map;
    }
}