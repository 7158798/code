package com.pay.card.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.ResourceUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.pay.card.api.resolver.BaseResolver;
import com.pay.card.api.resolver.BillResolver;
import com.pay.card.api.resolver.CardResolver;
import com.pay.card.api.resolver.RepaymentResolver;
import com.pay.card.api.resolver.SetResolver;
import com.pay.card.api.resolver.UserResolver;
import com.pay.card.web.interceptor.RedisInterceptor;

@Configuration
public class AdapterConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

        super.addArgumentResolvers(argumentResolvers);
        argumentResolvers.add(new UserResolver());
        argumentResolvers.add(new BaseResolver());
        argumentResolvers.add(new CardResolver());
        argumentResolvers.add(new BillResolver());
        argumentResolvers.add(new SetResolver());
        argumentResolvers.add(new RepaymentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        // registry.addInterceptor(new
        // JsonRequestBodyInterceptor()).excludePathPatterns("/swagger*/**")
        // .excludePathPatterns("/v2/api-docs").addPathPatterns("/**");
        // registry.addInterceptor(new
        // TokenInterceptor()).addPathPatterns("/**");

        // JsonRequestBodyInterceptor()).excludePathPatterns("/swagger*/**")
        // .excludePathPatterns("/v2/api-docs").addPathPatterns("/**");

        registry.addInterceptor(new RedisInterceptor()).addPathPatterns("/api/v1/**");
        super.addInterceptors(registry);
        // user过滤器，将手机号、商户编号、渠道转化为userId
        // registry.addInterceptor(new SetUser()).addPathPatterns("/api/**");
        // registry.addInterceptor(new
        // CheckParameterAccessInterceptor()).addPathPatterns("/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/META-INF/resources/webjars/");
        
        registry.addResourceHandler("/api/v2/static/**")
        .addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/resources/webjars/");
        super.addResourceHandlers(registry);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/html");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        super.configurePathMatch(configurer);
        configurer.setUseSuffixPatternMatch(false);
    }
}
