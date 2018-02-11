package com.pay.aile.bill.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.pay.aile.bill.event.AnalyzeStatusEvent;
import com.pay.aile.bill.event.ClearCacheDataEvent;
import com.pay.aile.bill.model.AnalyzeParamsModel;

/***
 * SpringContextUtil.java
 *
 * @author shinelon
 *
 * @date 2017年10月31日
 *
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(SpringContextUtil.class);

    private static ApplicationContext applicationContext;

    // 获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // 通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    // 通过name获取 Bean.
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    // 通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    public static void publishAnalyzeStatusEvent(AnalyzeParamsModel source) {
        getApplicationContext().publishEvent(new AnalyzeStatusEvent(source));
    }
    
    public static void publisCacheDataEvent(JSONObject json){
    	getApplicationContext().publishEvent(new ClearCacheDataEvent(json));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringContextUtil.applicationContext == null) {
            SpringContextUtil.applicationContext = applicationContext;
        }
    }
}
