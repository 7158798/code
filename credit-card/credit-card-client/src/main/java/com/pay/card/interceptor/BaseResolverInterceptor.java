
/**  
* @Title: BaseResolverInterceptor.java
* @Package com.pay.card.interceptor
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年12月7日
* @version V1.0  
*/

package com.pay.card.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: BaseResolverInterceptor
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月7日
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER })
public @interface BaseResolverInterceptor {

}
