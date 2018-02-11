package com.pay.card.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class DateUtil {
    static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    private static SimpleDateFormat defaultDatePattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static LocalDate dateToLocalDate(Date date) {

        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        LocalDate localDate = localDateTime.toLocalDate();

        return localDate;
    }

    /**
     * 格式化日期yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        String formatStr = "yyyy-MM-dd HH:mm:ss";
        return DateFormatUtils.format(date, formatStr);
    }

    /**
     * @Title: formatDate
     * @Description: 格式化日期时间 : yyyy/MM/dd HH:mm:ss
     * @param date
     * @return String 返回类型 @throws
     */
    public static String formatDate2(Date date) {
        String formatStr = "yyyy/MM/dd HH:mm:ss";
        return DateFormatUtils.format(date, formatStr);
    }

    public static String formatDate3(Date date) {
        String formatStr = "yyyy-MM-dd";
        return DateFormatUtils.format(date, formatStr);
    }

    /**
     * @Title: format
     * @Description:格式化日期
     * @param date
     * @return String 返回类型 @throws
     */
    public static String formatMMDD(Date date) {
        String formatStr = "MM月dd日";
        return DateFormatUtils.format(date, formatStr);
    }

    /**
     * @Title: format
     * @Description:格式化日期
     * @param date
     * @return String 返回类型 @throws
     */
    public static String formatMMDD2(Date date) {
        String formatStr = "MM/dd";
        return DateFormatUtils.format(date, formatStr);
    }

    public static String formatMMDD3(Date date) {
        String formatStr = "MM.dd";
        return DateFormatUtils.format(date, formatStr);
    }

    public static String formatMMDD4(Date date) {
        String formatStr = "MM月dd";
        return DateFormatUtils.format(date, formatStr);
    }

    public static String formatYYYY(Date date) {
        String formatStr = "yyyy年";
        return DateFormatUtils.format(date, formatStr);
    }

    public static String getBeforeDate() {
        LocalDate today = LocalDate.now();
        today = today.plusDays(-1);
        return today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    }

    /**
     * @Title: getBillCycle
     * @Description: 获取账单周期
     * @param
     * @return
     */
    public static String getBillCycle(String date) {
        String billCycle = "";
        if (StringUtils.hasText(date)) {
            String formatStr = "MM.dd";
            LocalDate localDate = LocalDate.parse(date);
            LocalDate beginDate = localDate.minusMonths(1).plusDays(1);
            LocalDate endDate = localDate;

            billCycle = DateFormatUtils.format(localDateToDate(beginDate), formatStr) + "-"
                    + DateFormatUtils.format(localDateToDate(endDate), formatStr);
            return billCycle;
        }

        return billCycle;
    }

    public static Integer getBillDay(String day) {
        try {
            // 获取账单日
            Integer d = Integer.parseInt(day);
            LocalDate localDate = LocalDate.now();
            int nd = localDate.getDayOfMonth();
            // 账单日
            if (nd == d) {
                return 0;
            } else if (d > nd) {
                // 本月未到账单日
                return d - nd;
            } else {
                // 本月已过账单日
                LocalDate lastDay = localDate.with(TemporalAdjusters.lastDayOfMonth());
                return lastDay.getDayOfMonth() - nd + d;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return -1;
    }

    public static String getCurrentDate() {
        LocalDate today = LocalDate.now();

        return today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    }

    public static Date getCurrentDate0() {
        LocalDate today = LocalDate.now();

        return localDateToDate(today);

    }

    public static int getCurrentDay() {
        LocalDate today = LocalDate.now();

        return today.getDayOfMonth();

    }

    public static int getCurrentHour() {
        LocalTime time = LocalTime.now();

        return time.getHour();

    }

    public static String getDate() {
        LocalDate today = LocalDate.now();

        return today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    }

    public static long getdifferenceDay(Date endDate, Date nowDate) {
        if (endDate == null || nowDate == null) {
            return 0;
        }
        long nd = 1000 * 24 * 60 * 60;
        // long nh = 1000 * 60 * 60;
        // long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // // 计算差多少小时
        // long hour = diff % nd / nh;
        // // 计算差多少分钟
        // long min = diff % nd % nh / nm;
        // // 计算差多少秒//输出结果
        // // long sec = diff % nd % nh % nm / ns;
        return day;
    }

    public static LocalDate getDueDate(int bill, int due, LocalDate billDate) {

        // 年
        int year = billDate.getYear();
        // 月
        int month = billDate.getMonthValue();

        LocalDate dueDate = LocalDate.of(year, month, due);
        // 账单日大于还款日
        if (bill > due) {
            dueDate = dueDate.plusMonths(1);
        }
        return dueDate;
    }

    public static LocalDate getDueDate(int bill, int due, LocalDate billDate, int freeInterestPeriod) {

        // 年
        int year = billDate.getYear();
        // 月
        int month = billDate.getMonthValue();
        LocalDate dueDate = null;
        // 固定还款日
        if (freeInterestPeriod == 0) {
            dueDate = LocalDate.of(year, month, due);
            // 账单日大于还款日
            if (bill > due) {
                dueDate = dueDate.plusMonths(1);
            }
        } else {
            // 获取账单日
            dueDate = billDate.plusDays(freeInterestPeriod);
        }

        return dueDate;
    }

    public static long getDueDay(Date dueDay) {
        LocalDate today = LocalDate.now();

        long daysDiff = ChronoUnit.DAYS.between(today, dateToLocalDate(dueDay));
        return daysDiff;
    }

    /**
     * @Title: getFreeInterestPeriod
     * @Description:获取免息期
     * @param billDay
     * @param dueDay
     * @return long 返回类型 @throws
     */
    public static long getFreeInterestPeriod(int billDay, int dueDay) {
        // 获取当前时间
        LocalDate today = LocalDate.now();
        // 年
        int year = today.getYear();
        // 月
        int month = today.getMonthValue();

        int day = today.getDayOfMonth();
        LocalDate billDate = null;

        billDate = LocalDate.of(year, month, billDay);

        if (day > billDay) {
            // 已过账单日 下月算账单日
            billDate = billDate.plusMonths(1);
        }
        LocalDate dueDate = getDueDate(billDay, dueDay, billDate);
        long daysDiff = ChronoUnit.DAYS.between(today, dueDate);
        return daysDiff;
    }

    /**
     * @Title: getFreeInterestPeriod
     * @Description:获取免息期
     * @param billDay
     * @param dueDay
     * @return long 返回类型 @throws
     */
    public static long getFreeInterestPeriod(int billDay, int dueDay, int freeInterestPeriod) {
        // 获取当前时间
        LocalDate today = LocalDate.now();
        // 年
        int year = today.getYear();
        // 月
        int month = today.getMonthValue();

        int day = today.getDayOfMonth();
        LocalDate billDate = null;

        billDate = LocalDate.of(year, month, billDay);

        if (day > billDay) {
            // 已过账单日 下月算账单日
            billDate = billDate.plusMonths(1);
        }
        LocalDate dueDate = getDueDate(billDay, dueDay, billDate, freeInterestPeriod);
        long daysDiff = ChronoUnit.DAYS.between(today, dueDate);
        return daysDiff;
    }

    public static String getFutureBillCycle(Date date) throws ParseException {
        if (date == null) {
            return "";
        }
        LocalDate loaclDate = dateToLocalDate(date);
        LocalDate beginDate = loaclDate.plusDays(1);

        LocalDate endDate = loaclDate.plusMonths(1);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM.dd");
        // logger.info("date==================={}", date);
        // String beginDate = date.split("-")[0].replaceAll("年|月|日| \\S+", "");
        // String endDate = date.split("-")[1].replaceAll("年|月|日| \\S+", "");
        // // TODO
        // String formatStr = "MM.dd";
        // SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
        // Date beginFormatdate = sdf.parse(beginDate);
        // Date endFormatdate = sdf.parse(endDate);
        // Calendar c = Calendar.getInstance();
        // c.setTime(beginFormatdate);
        // c.add(Calendar.MONTH, +1);
        // Date begin = c.getTime();
        // c.setTime(endFormatdate);
        // c.add(Calendar.MONTH, +1);
        // Date end = c.getTime();
        // return DateFormatUtils.format(begin, formatStr) + "-" +
        // DateFormatUtils.format(end, formatStr);

        return beginDate.format(format) + "-" + endDate.format(format);
    }

    public static String getFutureBillCycle(String date) throws ParseException {
        date = getBillCycle(date);
        logger.info("date==================={}", date);
        String beginDate = date.split("-")[0].replaceAll("年|月|日| \\S+", "");
        String endDate = date.split("-")[1].replaceAll("年|月|日| \\S+", "");
        // TODO
        String formatStr = "MM.dd";
        SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
        Date beginFormatdate = sdf.parse(beginDate);
        Date endFormatdate = sdf.parse(endDate);
        Calendar c = Calendar.getInstance();
        c.setTime(beginFormatdate);
        c.add(Calendar.MONTH, +1);
        Date begin = c.getTime();
        c.setTime(endFormatdate);
        c.add(Calendar.MONTH, +1);
        Date end = c.getTime();
        return DateFormatUtils.format(begin, formatStr) + "-" + DateFormatUtils.format(end, formatStr);
    }

    public static String getNextDate() {
        LocalDate today = LocalDate.now();
        today = today.plusDays(1);
        return today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    }

    public static int getNextDay() {
        LocalDate today = LocalDate.now();
        today = today.plusDays(1);
        return today.getDayOfMonth();

    }

    public static String getWeekOfDate(Date dt) {
        String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    public static Date localDateToDate(LocalDate localDate) {

        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        Date date = Date.from(zdt.toInstant());

        return date;
    }

    public static void main(String[] args) {
        Date date = stringToDate("2017-10-04 00:00:00");
        System.out.println(date);
    }

    /**
     * @Title: plusDays
     * @Description: 获取几天前的日期
     * @param day
     * @return String 返回类型 @throws
     */
    public static String plusDays(int day) {
        // 获取当前时间
        LocalDate today = LocalDate.now();
        today = today.plusDays(day);

        return today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00";
    }

    /**
     * @Title: stringToDate
     * @Description: 字符串转时间
     * @param
     * @return
     */
    public static Date stringToDate(String date) {

        try {
            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
            return sim.parse(date);
        } catch (ParseException e) {
            logger.error("{}", e);
        }
        return null;
    }

}
