package com.pay.aile.bill.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.pay.aile.bill.entity.CreditFile;
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

    public static final String MAIL_FILE_LIST = "aile-mail-file-list";
    public static final String MAIL_FILE_LIST_CONTENT = "aile-mail-file-list_content-";

    Logger logger = LoggerFactory.getLogger(FileQueueRedisHandle.class);
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     *
     * @Title: bathLeftPushFile
     * @Description:；批量保存
     * @param creditFileList
     * @return void 返回类型 @throws
     */
    public void bathLeftPushFile(List<CreditFile> creditFileList) {

        logger.info("bathLeftPushFile ============= creditFileList ==={}", JSONObject.toJSONString(creditFileList));

        Map<String, String> fileMap = new HashMap<String, String>();
        for (CreditFile file : creditFileList) {

            fileMap.put(MAIL_FILE_LIST_CONTENT + file.getFileName(), JSONObject.toJSONString(file));
            // JedisClusterUtils.saveString(MAIL_FILE_LIST_CONTENT +
            // file.getFileName(), JSONObject.toJSONString(file));
        }
        logger.info("bathLeftPushFile ============= fileMap ==={}", JSONObject.toJSONString(creditFileList));
        JedisClusterUtils.multiSet(fileMap);
        List<String> names = creditFileList.stream().map(file -> file.getFileName()).collect(Collectors.toList());

        logger.info("bathLeftPushFile ============= names ==={}", JSONObject.toJSONString(names));
        // 批量保存
        redisTemplate.opsForList().leftPushAll(MAIL_FILE_LIST, names);
    }

    /**
     *
     * @Title: bathPushFile
     * @Description:；批量保存
     * @param creditFileList
     * @return void 返回类型 @throws
     */
    public void bathPushFile(List<CreditFile> creditFileList) {
        // for (CreditFile file : creditFileList) {
        // JedisClusterUtils.saveString(MAIL_FILE_LIST_CONTENT +
        // file.getFileName(), JSONObject.toJSONString(file));
        // }
        Map<String, String> fileMap = new HashMap<String, String>();
        for (CreditFile file : creditFileList) {
            fileMap.put(MAIL_FILE_LIST_CONTENT + file.getFileName(), JSONObject.toJSONString(file));
            // JedisClusterUtils.saveString(MAIL_FILE_LIST_CONTENT +
            // file.getFileName(), JSONObject.toJSONString(file));
        }
        JedisClusterUtils.multiSet(fileMap);
        List<String> names = creditFileList.stream().map(file -> file.getFileName()).collect(Collectors.toList());
        // 批量保存
        redisTemplate.opsForList().rightPushAll(MAIL_FILE_LIST, names);

    }
}
