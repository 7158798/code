package com.pay.aile.bill.job;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.service.mail.download.DownloadMail;

/**
 *
 * @Description:
 * @see: CopyRelationScheduler 此处填写需要参考的类
 * @version 2017年12月7日 下午3:37:00
 * @author chao.wang
 */
@Service
public class CopyRelationScheduler {
    private static final Logger logger = LoggerFactory.getLogger(CopyRelationScheduler.class);

    // 当循环线程发现没有任务需要处理，等待sleepSeconds
    private static final int sleepSeconds = 1;
    // 当前任务上一次执行完毕的时间距离当前时间间隔如果小于此值,说明此任务可能一时无法执行,则让循环线程睡眠一段时间,等待条件满足时再执行
    public static final int loopIntervalSeconds = 5;

    private boolean stop = false;

    @Autowired
    private CopyRelationRedisJobHandle copyRelationRedisJobHandle;

    @Resource(name = "copyRelationTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private DownloadMail downloadMail;

    public void copyRelationLoop() {
        while (!stop) {
            CreditEmail creditEmail = copyRelationRedisJobHandle.getJob();
            if (creditEmail.getId() == null) {
                // 如果获取不到creditEmail 说明队列中已没有任务,则睡眠1s 防止资源浪费
                sleepForAWhile(sleepSeconds);
                continue;
            }
            if (System.currentTimeMillis() - creditEmail.getLastJobTimestamp() < loopIntervalSeconds * 1000) {
                copyRelationRedisJobHandle.resetJob(creditEmail);
                sleepForAWhile(sleepSeconds);
                continue;
            }
            taskExecutor.execute(() -> {
                try {
                    logger.info("正在处理********id:{} copyRelation email:{}", creditEmail.getId(), creditEmail.getEmail());
                    downloadMail.copyRelation(creditEmail);
                } catch (Exception e) {
                    logger.warn("copyRelationLoop exception:{}", creditEmail);
                }
            });
        }
    }

    public void stop() {
        stop = true;
    }

    private void sleepForAWhile(long sleepSeconds) {
        try {
            Thread.sleep(sleepSeconds * 1000);
        } catch (InterruptedException e) {

        }
    }
}
