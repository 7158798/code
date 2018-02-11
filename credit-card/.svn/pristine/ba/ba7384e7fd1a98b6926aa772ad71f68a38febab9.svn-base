package com.pay.card.interceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class CheckParameterAccessInterceptor extends HandlerInterceptorAdapter {
    @Value("${singKey}")
    private static String singKey = "86eb1492-6e08-481d-b377-678acd5c3fhd";

    /**
     *
     * @Title: getSign @Description: 获取签名
     *
     * @param map
     * @throws Exception
     * @return String 返回类型 @throws
     */
    public static String getSign(Map<String, String[]> map) throws Exception {
        return getHmac(sortRequestParam(map), singKey);
    }

    private static String getHmac(String data, String key) throws Exception {
        if (key == null || "".equals(key)) {
            throw new Exception("所需加密散列源为空");
        }

        // String result = DigestUtil.md5(data + key);
        String result = "";
        return result;
    }

    private static String sortRequestParam(Map<String, String[]> requestParam) throws Exception {
        StringBuilder sb = new StringBuilder("");
        if (null != requestParam && !requestParam.isEmpty()) {

            String[] keys = new String[] {};
            keys = requestParam.keySet().toArray(keys);
            Arrays.sort(keys, String.CASE_INSENSITIVE_ORDER);
            for (Object key : keys) {
                if ("sign".equals(key.toString())) {
                    continue;
                }
                String[] paramValue = requestParam.get(key);

                sb.append(key).append("=").append(paramValue[0]);
            }
        }
        return sb.toString();
    }

    Logger logger = LoggerFactory.getLogger(CheckParameterAccessInterceptor.class);

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // TODO Auto-generated method stub
        super.afterConcurrentHandlingStarted(request, response, handler);
    }

    /*
     * (非 Javadoc)
     * 
     * 
     * @param request
     * 
     * @param response
     * 
     * @param handler
     * 
     * @param modelAndView
     * 
     * @throws Exception
     * 
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#
     * postHandle(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object,
     * org.springframework.web.servlet.ModelAndView)
     */

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub
        super.postHandle(request, response, handler, modelAndView);
    }

    /*
     * (非 Javadoc)
     * 
     * 
     * @param request
     * 
     * @param response
     * 
     * @param handler
     * 
     * @throws Exception
     * 
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#
     * afterConcurrentHandlingStarted(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object)
     */

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 获取注释需要验证签名的方法
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        CheckParameters checkParameters = method.getAnnotation(CheckParameters.class);
        if (checkParameters != null) {
            // 判断签名
            String sign = request.getParameter("sign");
            String mySign = getSign(request.getParameterMap());
            if (sign.equals(mySign)) {
                return true;
            } else {
                logger.error("非正常访问");
                return false;
            }

        }

        return true;
    }
}
