/**
 * @Title: CardResolver.java
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

import com.pay.card.bean.CreditCardBean;
import com.pay.card.interceptor.CardResolverInterceptor;

/**
 * @ClassName: CardResolver
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月7日
 */

public class CardResolver extends BaseResolver {

    @Override
    protected Object setBean(HttpServletRequest request, Long userId) {
        // 信用卡
        CreditCardBean card = new CreditCardBean();

        if (StringUtils.hasText(request.getParameter("cardId"))) {
            card.setCardId(new Long(request.getParameter("cardId")));
        }
        if (StringUtils.hasText(request.getParameter("phoneNo"))) {
            card.setPhoneNo(request.getParameter("phoneNo"));
        }

        card.setUserId(userId);

        return card;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        boolean flag = parameter.getParameterAnnotation(CardResolverInterceptor.class) != null;
        return flag;
    }

}
