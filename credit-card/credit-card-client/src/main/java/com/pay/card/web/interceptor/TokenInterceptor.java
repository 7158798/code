package com.pay.card.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.pay.card.service.UserService;
import com.pay.card.utils.MethodInterceptorUtils;
import com.pay.card.web.annotation.LoginRequired;
import com.pay.card.web.context.CustomizeRequestContext;

/**
 *
 * @author qiaohui
 * 
 *         token 验证拦截器
 * 
 */
public class TokenInterceptor extends HandlerInterceptorAdapter {

    private static final String TOKEN_NAME = "token";

    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        JSONObject json = CustomizeRequestContext.getRequestBodyAsJson();
        Integer userId = null;
        if (json != null) {
            String token = json.getString(TOKEN_NAME);
            if (token != null) {
                userId = userService.verifyToken(token);
                if (userId != null) {
                    CustomizeRequestContext.setUserId(userId);
                }
            }
        }
        LoginRequired loginRequired = MethodInterceptorUtils.getAnnotaion(handler, LoginRequired.class);
        if (loginRequired != null && userId == null) {
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            logger.error("login required token error:{}", json);
            return false;
        }

        return true;
    }
}
