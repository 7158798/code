/**
 * @Title: BillResolver.java
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

import com.pay.card.bean.CreditBillBean;
import com.pay.card.interceptor.BillResolverInterceptor;

/**
 * @ClassName: BillResolver
 * @Description: 将request转化为bill
 * @author jing.jin
 * @date 2017年12月7日
 */

public class BillResolver extends BaseResolver {

    @Override
    protected Object setBean(HttpServletRequest request, Long userId) {
        CreditBillBean bill = new CreditBillBean();

        if (StringUtils.hasText(request.getParameter("cardId"))) {
            // 信用卡
            // CreditCard card = new CreditCard();
            // card.setId(new Long(request.getParameter("cardId")));
            // bill.setCard(card);
            bill.setCardId(new Long(request.getParameter("cardId")));
        }
        if (StringUtils.hasText(request.getParameter("billId"))) {
            bill.setBillId(new Long(request.getParameter("billId")));
        }
        if (StringUtils.hasText(request.getParameter("email"))) {
            bill.setEmail(request.getParameter("email"));
        }
        if (StringUtils.hasText(request.getParameter("yearMonth"))) {
            bill.setYearMonth(request.getParameter("yearMonth"));
        }
        if (StringUtils.hasText(request.getParameter("password"))) {
            bill.setPassword(request.getParameter("password"));
        }
        if (StringUtils.hasText(request.getParameter("phoneNo"))) {
            bill.setPhoneNo(request.getParameter("phoneNo"));
        }
        bill.setUserId(userId);
        return bill;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        boolean flag = parameter.getParameterAnnotation(BillResolverInterceptor.class) != null;
        return flag;
    }

}
