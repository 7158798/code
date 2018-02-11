package com.pay.card.web.context;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public abstract class RedisRequestContext extends RequestContextHolder {

    public static void setRequestContext(String name, String value) {
        RequestAttributes ra = currentRequestAttributes();
        ra.setAttribute(name, value, RequestAttributes.SCOPE_REQUEST);
    }

}
