package com.pay.aile.bill.event;

import java.util.concurrent.Executor;

import javax.annotation.Resource;

import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * @Description:
 * @see: ApplicationEventMulticaster 此处填写需要参考的类
 * @version 2017年12月15日 下午1:49:02
 * @author chao.wang
 */
@Component(AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME)
public class ApplicationEventMulticaster extends SimpleApplicationEventMulticaster {

    @Resource(name = "eventListenerTaskExecutor")
    private ThreadPoolTaskExecutor eventListenerTaskExecutor;

    @Override
    protected Executor getTaskExecutor() {
        return eventListenerTaskExecutor;
    }

}
