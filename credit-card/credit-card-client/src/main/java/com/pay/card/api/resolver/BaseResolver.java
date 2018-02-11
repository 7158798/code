/**
 * @Title: BaseResolver.java
 * @Package com.pay.card.api.resolver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author jing.jin
 * @date 2017年12月7日
 * @version V1.0
 */

package com.pay.card.api.resolver;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.pay.card.Constants;
import com.pay.card.Exception.UserNotFoundException;
import com.pay.card.enums.ChannelEnum;
import com.pay.card.interceptor.BaseResolverInterceptor;
import com.pay.card.utils.JedisUtil;

/**
 * @ClassName: BaseResolver
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月7日
 */

public class BaseResolver extends HandlerMethodArgumentResolverComposite implements WebArgumentResolver {

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        String customerNo = request.getParameter("customerNo");
        String channel = request.getParameter("channel");
        String phoneNo = request.getParameter("phoneNo");

        String key = "";
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
        // Long userId = (Long) LocalCacheUtil.get(phoneNo, customerNo, channel);

        if (userId == null) {
            throw new UserNotFoundException();
        }
        return setBean(request, userId);

    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected Object setBean(HttpServletRequest request, Long userId) {
        return userId;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        boolean flag = parameter.getParameterAnnotation(BaseResolverInterceptor.class) != null;
        return flag;

    }

}
