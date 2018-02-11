package com.pay.aile.bill.config;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.pay.aile.bill.job.DownloadMailScheduler;
import com.pay.aile.bill.job.RedisJobHandle;

/***
 * ApplicationStartup.java
 *
 * @author shinelon
 *
 * @date 2017年11月9日
 *
 */
@Component
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            logger.info("do starup init");
            RedisJobHandle redisJobHandle = event.getApplicationContext().getBean(RedisJobHandle.class);
            redisJobHandle.initJobList();
            DownloadMailScheduler downloadMailScheduler = event.getApplicationContext()
                    .getBean(DownloadMailScheduler.class);
            logger.info("do starup downLoadMailLoop");
            ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern("loop-mail-pool-%d").daemon(true).build());

            executorService.execute(() -> {
                downloadMailScheduler.downLoadMailLoop();
            });

        }
    }

}
