package com.pay.aile.bill.event;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.pay.aile.bill.utils.JedisClusterUtils;


/**
 * 
 * @Description: 删除client查询缓存
 * @see: ClearCacheDataListener 此处填写需要参考的类
 * @version 2018年1月12日 上午9:30:43 
 * @author zhibin.cui
 */
@Component
@Async
public class ClearCacheDataListener implements ApplicationListener<ClearCacheDataEvent> {
	private static Logger logger = LoggerFactory.getLogger(ClearCacheDataListener.class);
	
	private static final String REDIS_KEY = "credit_redis_key_";
	public static List<String> clientCacheMethodList = null;
	@Value("${client.cache.method}")
    private String clientCacheMethod = "";
	
	@Override
	public void onApplicationEvent(ClearCacheDataEvent event) {
		JSONObject json = (JSONObject) event.getSource();
        logger.info(json.toJSONString());
        if (json.get("userId") != null) {
        	Set<String> keyList = getKey(json.get("userId")+"",clientCacheMethodList);
			if (CollectionUtils.isNotEmpty(keyList)) {
				JedisClusterUtils.delete(keyList);
			}
		}
	}
	
	
	private Set<String> getKey(String userId, List<String> methodList) {
        Set<String> keyList = new HashSet<String>();
        if (StringUtils.hasText(userId)) {
        	for (String method : methodList) {
        		String key = "";
        		
        		key = REDIS_KEY + userId + method;
        		
        		keyList.add(key);
        	}
		}
        return keyList;
    }

	 @PostConstruct
	 public void setRelation() {
		 String[] array = clientCacheMethod.split(",");
         clientCacheMethodList = Arrays.asList(array);
     }
}
