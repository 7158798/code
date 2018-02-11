package com.pay.aile.bill.job;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.service.CreditEmailService;
import com.pay.aile.bill.service.mail.download.DownloadMail;

/***
 * DownloadMailScheduler.java
 *
 * @author shinelon
 *
 * @date 2017年11月8日
 *
 */
@Service
public class DownloadMailScheduler {
    private static boolean flagJobLoop = true;
    private static final Logger logger = LoggerFactory.getLogger(DownloadMailScheduler.class);
    // 当邮箱扫描时间间隔大约loopIntervalSeconds 重新下载邮箱
    private static final long loopIntervalSeconds = 180L;

    // 当循环线程发现没有邮件需要下载的时候，等待sleepSeconds
    private static final long sleepSeconds = 10L;
    @Autowired
    private CreditEmailService creditEmailService;
    @Autowired
    private DownloadMail downloadMail;

    @Autowired
    private RedisJobHandle redisJobHandle;

    // @Autowired
    @Resource(name = "mailTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    public void downLoadMail() {
        long startTime = System.currentTimeMillis();
        List<CreditEmail> list = creditEmailService.getCreditEmails();
        logger.debug("DownloadMailScheduler list:{}", list);
        CountDownLatch doneSignal = new CountDownLatch(list.size());
        for (CreditEmail creditEmail : list) {
            taskExecutor.execute(() -> {
                try {
                    downloadMail.execute(creditEmail);
                } catch (Exception e) {
                    logger.warn("download exception:{}", creditEmail);
                } finally {
                    doneSignal.countDown();
                }
            });
        }
        try {
            doneSignal.await();
            long endTime = System.currentTimeMillis();
            logger.debug("DownloadMailScheduler done:{}ms", endTime - startTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void downLoadMailLoop() {
        while (flagJobLoop) {
            CreditEmail creditEmail = redisJobHandle.getJob();
            if (creditEmail.getId() == null) {
                // 如果获取不到creditEmail 则任务已经删除
                continue;
            }

            if (System.currentTimeMillis() - creditEmail.getLastJobTimestamp() < loopIntervalSeconds * 1000) {
                redisJobHandle.doneJob(creditEmail);
                logger.debug("job id :{} is done in {}s  sleepForAWhile:{}s ", creditEmail.getId(), loopIntervalSeconds,
                        sleepSeconds);
                sleepForAWhile(sleepSeconds);
                continue;
            }
            taskExecutor.execute(() -> {
                try {
                    logger.debug("id:{}download email:{}", creditEmail.getId(), creditEmail.getEmail());
                    downloadMail.execute(creditEmail, redisJobHandle);
                } catch (Exception e) {
                    logger.warn("download exception:{}", creditEmail);
                }
            });

        }

    }

    public void offJobLoop() {
        flagJobLoop = false;
    }

    private void sleepForAWhile(long sleepSeconds) {
        try {
            Thread.sleep(sleepSeconds * 1000);
        } catch (InterruptedException e) {

        }
    }
}
