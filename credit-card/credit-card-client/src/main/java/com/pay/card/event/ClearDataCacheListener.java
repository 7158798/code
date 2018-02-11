/**
 * @Title: ClearDataCacheListener.java
 * @Package com.pay.card.event
 * @Description: TODO(用一句话描述该文件做什么)
 * @author jing.jin
 * @date 2017年12月14日
 * @version V1.0
 */

package com.pay.card.event;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

/**
 * @ClassName: ClearDataCacheListener
 * @Description: 清除数据缓存
 * @author jing.jin
 * @date 2017年12月14日
 */

@Component
@Async
public class ClearDataCacheListener implements ApplicationListener<ClearDataCacheEvent> {
    private static Logger logger = LoggerFactory.getLogger(ClearDataCacheListener.class);
    private static final String REDIS_KEY = "credit_redis_key_";

    public static List<String> repaymentRelationList = null;
    public static List<String> cardRelationList = null;
    @Value("${redis.cache.relation.repayment}")
    private String repaymentRelation = "";

    @Value("${redis.cache.relation.card}")
    private String cardRelation = "";

    /**
     * @Title: getKey
     * @Description: 获取缓存的可用
     * @param userId
     * @param billId
     * @param cardId
     * @param methodList
     * @return Set<String> 返回类型 @throws
     */

    private Set<String> getKey(String userId, String billId, String cardId, List<String> methodList) {
        Set<String> keyList = new HashSet<String>();
        for (String method : methodList) {
            String key = "";
            if (StringUtils.hasText(billId)) {
                key = REDIS_KEY + userId + "" + billId + method;
            } else if (StringUtils.hasText(cardId)) {
                key = REDIS_KEY + userId + "" + cardId + method;
            } else {
                key = REDIS_KEY + userId + "" + method;
            }
            keyList.add(key);
        }
        return keyList;
    }

    @Override
    public void onApplicationEvent(ClearDataCacheEvent event) {
        JSONObject json = (JSONObject) event.getSource();
        logger.info(json.toJSONString());
        String userId = json.getString("userId");
        String cardId = json.getString("cardId");
        String billId = json.getString("billId");
        if ("card".equals(json.getString("type"))) {
            JedisUtil.delete(getKey(userId, cardId, billId, cardRelationList));
        } else {
            JedisUtil.delete(getKey(userId, cardId, billId, repaymentRelationList));
        }
    }

    @PostConstruct
    public void setRelation() {

        String[] array = repaymentRelation.split(",");
        repaymentRelationList = Arrays.asList(array);
        array = cardRelation.split(",");
        cardRelationList = Arrays.asList(array);

    }
}
