package com.pay.aile.bill.task;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.pay.aile.bill.analyze.impl.ParseMailImpl;
import com.pay.aile.bill.model.CreditFileModel;

/***
 * DownloadMailScheduler.java
 *
 * @author shinelon
 *
 * @date 2017年11月8日
 *
 */
@Service
public class FileAnalyzeScheduler {
    private static final Logger logger = LoggerFactory.getLogger(FileAnalyzeScheduler.class);

    private static final int sleepSeconds = 10;

    private boolean flagJobLoop = true;

    @Resource(name = "fileTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;
    @Resource(name = "parseMail")
    private ParseMailImpl parseMail;
    @Autowired
    private FileQueueRedisHandle fileQueueRedisHandle;

    public void analyzeLoop() {
        while (flagJobLoop) {
            CreditFileModel file = null;
            try {
                file = fileQueueRedisHandle.getFile();
                if (file == null) {
                    Thread.sleep(sleepSeconds * 1000);
                }
            } catch (Exception e) {
                logger.error("fileQueue getFile error!", e);
            }
            CreditFileModel cfm = file;
            try {
                taskExecutor.execute(() -> {
                    try {
                        if (cfm != null) {
                            parseMail.executeParseFile(cfm);
                        }
                    } catch (Exception e) {
                        logger.warn("download exception:{}");
                    }
                });
            } catch (TaskRejectedException e) {
                logger.error("fileAnalyze thread has been rejected!");
                if (file != null) {
                    fileQueueRedisHandle.pushFileNX(file);
                }
                try {
                    Thread.sleep(sleepSeconds * 1000);
                } catch (InterruptedException e1) {
                }
            }

        }

    }

    public void offJobLoop() {
        flagJobLoop = false;
    }
}
