package com.pay.aile.bill.schedule;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pay.aile.bill.model.CreditFileModel;
import com.pay.aile.bill.service.CreditFileService;
import com.pay.aile.bill.task.FileQueueRedisHandle;

@Component
public class ScheduledTask {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private CreditFileService creditFileService;

    @Autowired
    private FileQueueRedisHandle fileQueueRedisHandle;

    /**
     *
     * @Description 每隔五分钟将一小时前下载的未解析的文件放入待解析队列
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void processOvertimeUnalanyzedFile() {
        logger.info("processOvertimeUnalanyzedFile start...");
        try {
            List<CreditFileModel> list = creditFileService.findOverTimeUnAnalyzedList();
            logger.info("processOvertimeUnalanyzedFile size={}", list == null || list.isEmpty() ? 0 : list.size());
            if (list != null) {
                list.forEach(file -> {
                    fileQueueRedisHandle.pushFileNX(file);
                });
            }
        } catch (Exception e) {
            logger.error("processOvertimeUnalanyzedFile error!", e);
        }
    }

}
