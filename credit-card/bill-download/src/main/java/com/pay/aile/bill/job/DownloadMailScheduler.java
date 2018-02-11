package com.pay.aile.bill.job;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.contant.Constant;
import com.pay.aile.bill.contant.ErrorCodeContants;
import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.exception.MailBillException;
import com.pay.aile.bill.service.CreditEmailService;
import com.pay.aile.bill.service.mail.download.DownloadMail;
import com.pay.aile.bill.utils.JedisClusterUtils;

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
    private static final Logger logger = LoggerFactory.getLogger(DownloadMailScheduler.class);
    // 当邮箱扫描时间间隔大约loopIntervalSeconds 重新下载邮箱 一小时
    public static final long loopIntervalSeconds = 180L;

    // 当循环线程发现没有邮件需要下载的时候，等待sleepSeconds
    private static final long sleepSeconds = 10L;

    private boolean flagJobLoop = true;
    @Autowired
    private CreditEmailService creditEmailService;
    @Autowired
    private DownloadMail downloadMail;

    @Autowired
    private RedisJobHandle redisJobHandle;

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
            // logger.info("正在处理的email{}=======",
            // JSONObject.toJSONString(creditEmail));
            if (creditEmail.getId() == null) {
                // 如果获取不到creditEmail 则任务已经删除
                continue;
            }
            if (System.currentTimeMillis() - creditEmail.getLastJobTimestamp() < loopIntervalSeconds * 1000) {
                redisJobHandle.doneJob(creditEmail);
                logger.info("job id :{} is done in {}s  sleepForAWhile:{}s ", creditEmail.getId(), loopIntervalSeconds,
                        sleepSeconds);
                sleepForAWhile(sleepSeconds);
                continue;
            }
            try {
                taskExecutor.execute(() -> {
                    try {
                        logger.info("正在处理。。。。。。。。。。。。。。。。id:{}download email:{} getIsNew{} ", creditEmail.getId(),
                                creditEmail.getEmail(), creditEmail.getIsNew());
                        downloadMail.execute(creditEmail, redisJobHandle);
                    } catch (Exception e) {
                        if (e instanceof MailBillException) {
                            MailBillException me = (MailBillException) e;
                            if (me.getErrorCode() == ErrorCodeContants.EMAIL_LOGIN_FAILED_CODE) {
                                Long emailId = creditEmail.getId();

                                String fail = JedisClusterUtils
                                        .getString(Constant.REDIS_EMAIL_LOGIN_FIAL.concat(String.valueOf(emailId)));
                                if (StringUtils.hasText(fail)) {
                                    int failCount = Integer.parseInt(fail);
                                    // 超过你3此
                                    if (failCount >= 3) {
                                        JedisClusterUtils.saveString(
                                                Constant.REDIS_EMAIL_LOGIN_FIAL.concat(String.valueOf(emailId)), "");
                                    } else {
                                        failCount++;
                                        JedisClusterUtils.saveString(
                                                Constant.REDIS_EMAIL_LOGIN_FIAL.concat(String.valueOf(emailId)),
                                                String.valueOf(failCount));
                                    }

                                }

                            }
                        }
                        logger.warn("download exception:{}", creditEmail);
                    }
                });
            } catch (TaskRejectedException e) {
                logger.error("email download thread has been rejected!");
                redisJobHandle.doneJob(creditEmail);
            }

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
