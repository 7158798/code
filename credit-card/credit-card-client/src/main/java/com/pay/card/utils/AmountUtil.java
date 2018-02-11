package com.pay.card.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Pattern;

public class AmountUtil {

    /**
     * @Title: amountFormat
     * @Description:金额格式化
     * @param amount
     * @return String 返回类型 @throws
     */
    public static String amountFormat(BigDecimal amount) {
        NumberFormat nf = new DecimalFormat("0.00");
        return nf.format(amount.doubleValue());
    }

    public static String amountFormat2(BigDecimal amount) {
        NumberFormat nf = new DecimalFormat("0.##");
        return nf.format(amount.doubleValue());
    }

    public static String amountFormat2(double amount) {
        NumberFormat nf = new DecimalFormat("0.##");
        return nf.format(amount);
    }

    /**
     * 金额验证，35.3=true|35=true|-23=false
     * 
     * @return
     */
    public static boolean isAmount(String match) {
        if (match == null) {
            return false;
        }
        String regex = "\\d+.?\\d{0,}";
        return Pattern.matches(regex, match);
    }

    /**
     * 数字验证，35.3=false|35=true|-23=false|2 3=false
     * 
     * @return
     */
    public static boolean isNumber(String match) {
        if (match == null) {
            return false;
        }
        String regex = "\\d+";
        return Pattern.matches(regex, match);
    }

    public static void main(String[] args) {
        System.out.println(amountFormat2(new BigDecimal(1.00)));
    }

}
