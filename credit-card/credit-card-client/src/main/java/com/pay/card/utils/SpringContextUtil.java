/**
 * @Title: SpringContextUtil.java
 * @Package com.pay.card.utils
 * @Description: TODO(用一句话描述该文件做什么)
 * @author jing.jin
 * @date 2017年12月14日
 * @version V1.0
 */

package com.pay.card.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.pay.card.event.ClearCardDataCacheEvent;
import com.pay.card.event.ClearDataCacheEvent;

/**
 * @ClassName: SpringContextUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月14日
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    // 获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void publishCardDataEvent(JSONObject json) {
        applicationContext.publishEvent(new ClearCardDataCacheEvent(json));
    }

    public static void publishCardEvent(JSONObject json) {
        json.put("type", "card");
        applicationContext.publishEvent(new ClearDataCacheEvent(json));

    }

    public static void publishRepaymentEvent(JSONObject json) {
        json.put("type", "repayment");
        applicationContext.publishEvent(new ClearDataCacheEvent(json));

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringContextUtil.applicationContext == null) {
            SpringContextUtil.applicationContext = applicationContext;
        }
    }
}
