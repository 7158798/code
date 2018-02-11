package com.pay.card.web.context;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author qiaohui
 */
public abstract class CustomizeRequestContext extends RequestContextHolder {

    private static final String USER_ID = "__CURRENT_LOGINED_USERID__";

    private static final String REQUEST_BODY_JSON = "__CURRENT_REQUEST_BODY_JSON__";

    public static int getUserId() {
        try {
            Integer userId = (Integer) currentRequestAttributes().getAttribute(USER_ID,
                    RequestAttributes.SCOPE_REQUEST);
            if (userId == null) {
                return 0;
            } else {
                return userId;
            }
        } catch (IllegalStateException e) {
            // 在非www环境使用
            return 0;
        }
    }

    public static void setUserId(int userId) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(USER_ID, userId, RequestAttributes.SCOPE_REQUEST);
    }

    public static JSONObject getRequestBodyAsJson() {
        try {
            return (JSONObject) currentRequestAttributes().getAttribute(REQUEST_BODY_JSON,
                    RequestAttributes.SCOPE_REQUEST);
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public static void setRequestBodyAsJson(JSONObject json) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(REQUEST_BODY_JSON, json, RequestAttributes.SCOPE_REQUEST);
    }

}
