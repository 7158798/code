
/**  
* @Title: SetUserInterceptor.java
* @Package com.pay.card.interceptor
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年12月5日
* @version V1.0  
*/

package com.pay.card.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: SetUserInterceptor
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月5日
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER })
public @interface UserResolverInterceptor {

}
