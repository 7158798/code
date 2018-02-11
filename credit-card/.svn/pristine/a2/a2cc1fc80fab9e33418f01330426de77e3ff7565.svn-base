/**
 * 
 */
package com.pay.card.utils;

import java.lang.annotation.Annotation;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;

/**
 * @author qiaohui
 * 
 */
public final class MethodInterceptorUtils {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MethodInterceptorUtils.class);

    public static <A extends Annotation> A getAnnotaion(Object handler, Class<A> annotationClass) {
        if (!(handler instanceof HandlerMethod)) {
            return null;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        A result = null;
        if ((result = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), annotationClass)) != null) {
            logger.trace("found [{}] annotation in method-level:{}", annotationClass.getSimpleName(),
                    handlerMethod.getMethod());
        } else {
            if ((result = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), annotationClass)) != null) {
                logger.trace("found [{}] annotation in type-level:{}", annotationClass.getSimpleName(),
                        handlerMethod.getBeanType());
            }
        }
        return result;
    }
}
