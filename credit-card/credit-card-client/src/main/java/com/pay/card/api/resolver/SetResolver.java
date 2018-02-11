
/**
* @Title: SetResolver.java
* @Package com.pay.card.api.resolver
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年12月7日
* @version V1.0
*/

package com.pay.card.api.resolver;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;

import com.pay.card.interceptor.SetResolverInterceptor;
import com.pay.card.model.CreditSet;
import com.pay.card.model.CreditUserInfo;

/**
 * @ClassName: SetResolver
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月7日
 *
 */

public class SetResolver extends BaseResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        boolean flag = parameter.getParameterAnnotation(SetResolverInterceptor.class) != null;
        return flag;
    }

    @Override
    protected Object setBean(HttpServletRequest request, Long userId) {
        CreditSet set = RequestToBean.RequestToSet(request);
        CreditUserInfo userInfo = new CreditUserInfo();
        userInfo.setId(userId);
        set.setUserInfo(userInfo);
        return set;
    }
}
