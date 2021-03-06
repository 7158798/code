package com.pay.aile.bill.task;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pay.aile.bill.model.CreditFileModel;
import com.pay.aile.bill.service.CreditFileService;
import com.pay.aile.bill.utils.JedisClusterUtils;

/**
 *
 * @ClassName: FileQueueRedisHandle
 * @Description: 邮件附件的队列
 * @author jinjing
 * @date 2017年11月12日
 *
 */
@Component
public class FileQueueRedisHandle {

    private static final Logger logger = LoggerFactory.getLogger(FileQueueRedisHandle.class);
    public static final String MAIL_FILE_LIST = "aile-mail-file-list";
    public static final String MAIL_FILE_LIST_CONTENT = "aile-mail-file-list_content-";
    public static final String INIT_FILE_LIST_KEY = "bill-analyze-init-file-key";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CreditFileService creditFileService;

    /***
     * 从头部获取任务执行内容
     *
     * @return
     */
    public CreditFileModel getFile() {
        if (org.springframework.util.StringUtils.hasText(JedisClusterUtils.getString(INIT_FILE_LIST_KEY))) {
            return null;
        }
        String fileName = redisTemplate.opsForList().leftPop(MAIL_FILE_LIST);
        String fileJson = JedisClusterUtils.getString(MAIL_FILE_LIST_CONTENT + fileName);
        if (StringUtils.isEmpty(fileJson)) {
            return null;
        }
        logger.info("*****get file json={}", fileJson);
        return JSON.parseObject(fileJson, CreditFileModel.class);
    }

    /***
     * 初始化任务列表，程序启动自动调用
     */
    public void initFileList() {
        JedisClusterUtils.saveString(INIT_FILE_LIST_KEY, INIT_FILE_LIST_KEY, 15 * 60 * 1000);
        try {
            // 休眠一段时间,让正在执行的解析线程执行完毕
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }

        List<CreditFileModel> creditFileList = creditFileService.findUnAnalyzedList();
        if (creditFileList != null && creditFileList.size() > 0) {
            for (CreditFileModel file : creditFileList) {
                pushFileNX(file);
            }
        }
        JedisClusterUtils.delKey(INIT_FILE_LIST_KEY);
    }

    /***
     * 执行任务完成,将任务放在队尾
     *
     * @return
     */
    public void pushFileNX(CreditFileModel creditFile) {
        String script = "if redis.call('lrem',KEYS[1],0,KEYS[2]) == 0 then redis.call('set',KEYS[3],KEYS[4]) redis.call('rpush',KEYS[1],KEYS[2]) return 'true' else redis.call('rpush',KEYS[1],KEYS[2]) return 'false' end";
        String[] keys = new String[] { MAIL_FILE_LIST, creditFile.getFileName(),
                MAIL_FILE_LIST_CONTENT + creditFile.getFileName(), JSON.toJSONString(creditFile) };
        JedisClusterUtils.executeEval(script, Arrays.asList(keys));
    }

}
