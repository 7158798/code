
/**
* @Title: RepaymentResolver.java
* @Package com.pay.card.api.resolver
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年12月7日
* @version V1.0
*/

package com.pay.card.api.resolver;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;

import com.pay.card.interceptor.RepaymentResolverInterceptor;
import com.pay.card.model.CreditRepayment;
import com.pay.card.model.CreditUserInfo;

/**
 * @ClassName: RepaymentResolver
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月7日
 *
 */

public class RepaymentResolver extends BaseResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        boolean flag = parameter.getParameterAnnotation(RepaymentResolverInterceptor.class) != null;
        return flag;
    }

    @Override
    protected Object setBean(HttpServletRequest request, Long userId) {
        CreditRepayment repayment = RequestToBean.RequestToRepayment(request);
        CreditUserInfo userInfo = new CreditUserInfo();
        userInfo.setId(userId);
        repayment.setUserInfo(userInfo);
        return repayment;
    }
}
