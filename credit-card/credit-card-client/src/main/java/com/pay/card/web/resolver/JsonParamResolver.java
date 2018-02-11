/**
 * 
 */
package com.pay.card.web.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.alibaba.fastjson.JSONObject;
import com.pay.card.web.annotation.JsonParam;
import com.pay.card.web.context.CustomizeRequestContext;

/**
 * 
 * @author qiaohui
 *
 */
public class JsonParamResolver implements HandlerMethodArgumentResolver {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(JsonParam.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        JsonParam jsonParam = parameter.getParameterAnnotation(JsonParam.class);
        JSONObject jsonObject = CustomizeRequestContext.getRequestBodyAsJson();
        logger.info("found json param to resolver:{}////{}=>{}", jsonParam, jsonObject, jsonParam.value());
        if (jsonObject == null) {
            return null;
        }
        return jsonObject.get(jsonParam.value());
    }

}
