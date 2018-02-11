package com.pay.card.utils;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class BeanUtil {

    private static Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    public static void copyPropertiesIgnoreNull(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    public static void copyPropertiesIgnoreNullCreditCard(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNamesCreditCard(src));
    }

    private static void getEmptyNames(final BeanWrapper src, java.beans.PropertyDescriptor[] pds, Set<String> emptyNames) {
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null || pd.getName().equals("bank")) {
                emptyNames.add(pd.getName());
            }
        }
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<String>();

        getEmptyNames(src, pds, emptyNames);

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);

    }

    public static String[] getNullPropertyNamesCreditCard(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<String>();

        if (1 == (Integer) src.getPropertyValue("source")) {
            getEmptyNames(src, pds, emptyNames);
        } else {
            for (java.beans.PropertyDescriptor pd : pds) {

                Object srcValue = src.getPropertyValue(pd.getName());
                if (srcValue == null || pd.getName().equals("bank") || pd.getName().equals("billDay") || pd.getName().equals("dueDay")) {
                    emptyNames.add(pd.getName());
                }
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

}
