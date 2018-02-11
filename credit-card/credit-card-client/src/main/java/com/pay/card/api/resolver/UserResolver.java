/**
 * @Title: UserResolver.java
 * @Package com.pay.card.api.resolver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author jing.jin
 * @date 2017年12月5日
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
import com.pay.card.enums.ChannelEnum;
import com.pay.card.interceptor.UserResolverInterceptor;
import com.pay.card.model.CreditUserInfo;
import com.pay.card.utils.JedisUtil;

/**
 * @ClassName: UserResolver
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月5日
 */

public class UserResolver extends HandlerMethodArgumentResolverComposite implements WebArgumentResolver {

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

        // if (parameter.getParameterType().equals(CreditBillBean.class)) {
        // // 账单
        // CreditBillBean bill = new CreditBillBean();
        //
        // if (StringUtils.hasText(request.getParameter("cardId"))) {
        // // 信用卡
        // // CreditCard card = new CreditCard();
        // // card.setId(new Long(request.getParameter("cardId")));
        // // bill.setCard(card);
        // bill.setCardId(new Long(request.getParameter("cardId")));
        // }
        // // TODO 修为copy固定属性
        // // BeanUtils.populate(bill, request.getParameterMap());
        // bill.setCustomerNo(customerNo);
        // bill.setChannel(channel);
        // bill.setPhoneNo(phoneNo);
        // bill.setUserId(userId);
        // return bill;
        // } else if (parameter.getParameterType().equals(CreditCardBean.class))
        // {
        // // 信用卡
        // CreditCardBean card = new CreditCardBean();
        // // BeanUtils.populate(card, request.getParameterMap());
        // card.setChannel(channel);
        // card.setCustomerNo(customerNo);
        // card.setPhoneNo(phoneNo);
        // card.setUserId(userId);
        // return card;
        // } else if (parameter.getParameterType().equals(CreditUserInfo.class))
        // {
        // CreditUserInfo user = new CreditUserInfo();
        // user.setCustomerNo(customerNo);
        // user.setChannel(channel);
        // user.setPhoneNo(phoneNo);
        // user.setId(userId);
        //
        // return user;
        // } else if (parameter.getParameterType().equals(CreditSet.class)) {
        // CreditSet set = RequestToBean.RequestToSet(request);
        // CreditUserInfo userInfo = new CreditUserInfo();
        // userInfo.setId(userId);
        // set.setUserInfo(userInfo);
        // return set;
        // } else if
        // (parameter.getParameterType().equals(CreditRepayment.class)) {
        // CreditRepayment repayment =
        // RequestToBean.RequestToRepayment(request);
        // CreditUserInfo userInfo = new CreditUserInfo();
        // userInfo.setId(userId);
        // repayment.setUserInfo(userInfo);
        // return repayment;
        // }
        //
        // return userId;

        CreditUserInfo user = new CreditUserInfo();
        user.setCustomerNo(customerNo);
        user.setChannel(channel);
        user.setPhoneNo(phoneNo);
        user.setId(userId);

        return user;

    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        boolean flag = parameter.getParameterAnnotation(UserResolverInterceptor.class) != null;
        return flag;
    }

}
