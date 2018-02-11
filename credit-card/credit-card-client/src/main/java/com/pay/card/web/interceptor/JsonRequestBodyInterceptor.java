package com.pay.card.web.interceptor;

import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pay.card.service.RequestInfoService;
import com.pay.card.web.context.CustomizeRequestContext;

/**
 * @author qiaohui
 */
public class JsonRequestBodyInterceptor extends HandlerInterceptorAdapter {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());
    @Autowired
    private RequestInfoService aysncLog;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String rawData = IOUtils.toString(request.getInputStream(), Charset.defaultCharset());
        String rurl = request.getRequestURL().toString();
        logger.info("API请求接口URL:" + rurl);
        try {
            JSONObject json = JSON.parseObject(rawData);
            logger.info("API请求Body：" + json);
            CustomizeRequestContext.setRequestBodyAsJson(json);

            String device_info = request.getHeader("deviceinfo");
            logger.info("API请求Header：" + device_info);
            if (!StringUtils.isEmpty(device_info) && null != json) {
                aysncLog.asyncRun(device_info, rurl, rawData);
            }
        } catch (Throwable e) {
            logger.error("fail to resolve request body as json:{}", rawData, e);
        }
        return super.preHandle(request, response, handler);
    }
}
