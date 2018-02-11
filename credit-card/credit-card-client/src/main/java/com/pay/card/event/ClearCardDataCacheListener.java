package com.pay.card.event;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.pay.card.utils.JedisUtil;

@Component
@Async
public class ClearCardDataCacheListener implements ApplicationListener<ClearCardDataCacheEvent> {

    private static Logger logger = LoggerFactory.getLogger(ClearCardDataCacheListener.class);

    private static final String REDIS_KEY = "credit_redis_key_";

    public static List<String> cardRelationList = null;

    @Value("${redis.cache.relation.card.data}")
    private String cardRelation = "";

    private String getKey(String userId, String method, List<String> methodList) {

        methodList.contains(method);

        if (methodList.contains(method)) {
            return REDIS_KEY + userId + "findCardIndex";
        }
        return "";
    }

    @Override
    public void onApplicationEvent(ClearCardDataCacheEvent event) {
        JSONObject json = (JSONObject) event.getSource();
        logger.info(json.toJSONString());
        String userId = json.getString("userId");
        String method = json.getString("method");
        String key = getKey(userId, method, cardRelationList);
        if (StringUtils.hasText(key)) {
            JedisUtil.delKey(key);
        }

    }

    @PostConstruct
    public void setRelation() {

        // String[] array = repaymentRelation.split(",");
        // repaymentRelationList = Arrays.asList(array);
        String[] array = cardRelation.split(",");
        cardRelationList = Arrays.asList(array);

    }
}
