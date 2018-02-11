package com.pay.card.web.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.pay.card.Constants;
import com.pay.card.enums.ChannelEnum;
import com.pay.card.utils.JedisUtil;
import com.pay.card.utils.MethodInterceptorUtils;
import com.pay.card.utils.SpringContextUtil;
import com.pay.card.web.annotation.RedisCacheInterceptor;
import com.pay.card.web.context.RedisRequestContext;

public class RedisInterceptor extends HandlerInterceptorAdapter {

    private static Logger logger = LoggerFactory.getLogger(RedisInterceptor.class);

    private static final int TIMEOUT = 24 * 60 * 60;
    private static final String REDIS_KEY = "credit_redis_key_";

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception arg3) throws Exception {
        RedisCacheInterceptor redisCacheInterceptor = MethodInterceptorUtils.getAnnotaion(handler, RedisCacheInterceptor.class);;

        if (redisCacheInterceptor != null) {
            Map<String, Object> map = getKey(req);
            String cacheValue = "";
            if (map.get("key") != null) {
                cacheValue = (String) req.getAttribute("cacheValue");
                if (StringUtils.hasText(cacheValue)) {
                    JedisUtil.saveString(map.get("key") + "", cacheValue, TIMEOUT);
                }

            }
        }
    }

    private Map<String, Object> getKey(HttpServletRequest req) {

        Map<String, Object> map = new HashMap<String, Object>();
        String key = "";
        String[] uri = req.getRequestURI().split("/");
        String method = uri[uri.length - 1];
        String customerNo = req.getParameter("customerNo");
        String channel = req.getParameter("channel");
        String phoneNo = req.getParameter("phoneNo");
        String cardId = req.getParameter("cardId");
        String billId = req.getParameter("billId");
        // Long userId = (Long) LocalCacheUtil.get(phoneNo, customerNo, channel);
        if (ChannelEnum.MPOS.getCode().equals(channel)) {
            // 手刷
            key = phoneNo + channel;
        } else if (ChannelEnum.POS.getCode().equals(channel)) {
            // 大pos
            key = customerNo + channel;
        }
        Long userId = null;
        if (StringUtils.hasText(JedisUtil.getString(Constants.REDIS_USERID_KEY + key))) {
            userId = Long.valueOf(JedisUtil.getString(Constants.REDIS_USERID_KEY + key));
        }

        if (StringUtils.hasText(billId)) {
            key = userId + "" + billId + method;
        } else if (StringUtils.hasText(cardId)) {
            key = userId + "" + cardId + method;
        } else {
            key = userId + "" + method;
        }
        map.put("userId", userId);
        map.put("method", method);
        map.put("key", REDIS_KEY + key);
        return map;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {

        RedisCacheInterceptor redisCacheInterceptor = MethodInterceptorUtils.getAnnotaion(handler, RedisCacheInterceptor.class);
        if (redisCacheInterceptor != null) {

            Map<String, Object> map = getKey(req);

            JSONObject del = new JSONObject();
            if (map.get("userId") != null && map.get("method") != null) {
                del.put("userId", map.get("userId") + "");
                del.put("method", map.get("method") + "");
                SpringContextUtil.publishCardDataEvent(del);
            }

            String cacheValue = "";
            if (map.get("key") != null) {
                cacheValue = JedisUtil.getString(map.get("key") + "");
            }
            if (StringUtils.hasText(cacheValue)) {
                RedisRequestContext.setRequestContext("cacheValue", cacheValue);
                return true;
            }
            return true;
        }

        return true;
    }

}
