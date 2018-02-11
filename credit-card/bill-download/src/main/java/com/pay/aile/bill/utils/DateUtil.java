package com.pay.aile.bill.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Charlie
 * @description
 */
public class DateUtil {
    private static SimpleDateFormat defaultDatePattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date dateCompute(Date date, int field, int offset) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(field, offset);
        return c.getTime();
    }

    public static LocalDate dateToLocalDate(Date date) {

        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        LocalDate localDate = localDateTime.toLocalDate();

        return localDate;
    }

    public static String formatDate(Date date) {

        return defaultDatePattern.format(date);
    }
    public static String formatDate(Date date,String formate) {
        SimpleDateFormat defaultDatePattern = new SimpleDateFormat(formate);

        return defaultDatePattern.format(date);
    }
    public static Date localDateToDate(LocalDate localDate) {

        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        Date date = Date.from(zdt.toInstant());

        return date;
    }

    public static Date parseDate(String date) {

        try {
            return defaultDatePattern.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
    
    public static Date getDate(Long time) {

    	Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		Date date = cal.getTime();

        return date;
    }
    
    
    public static String getDateStr(Long time) {

    	Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		Date date = cal.getTime();
		SimpleDateFormat defaultDatePattern = new SimpleDateFormat("yyyyMMddHH:mm:ss");
        return defaultDatePattern.format(date);
    }
}
