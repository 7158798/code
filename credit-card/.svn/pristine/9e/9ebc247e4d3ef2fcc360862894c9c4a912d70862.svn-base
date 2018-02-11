package com.pay.aile.bill.job;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.service.CreditEmailService;

/***
 * RedisJobSettings.java
 *
 * 任务队列，MAIL_DOWANLOD_LIST_NAME 存放id
 *
 * 任务信息，MAIL_DOWANLOD_JOB_CONTENT根据id获取
 *
 * 队列左头右尾
 *
 * @author shinelon
 *
 * @date 2017年11月9日
 *
 */
@Component
public class RedisJobHandle {

    private static final Logger logger = LoggerFactory.getLogger(RedisJobHandle.class);
    private static final long ttlInSeconds = 300;
    public static final String MAIL_DOWANLOD_LIST_NAME = "aile-mail-job-list";
    public static final String MAIL_DOWANLOD_JOB_CONTENT = "aile-mail-job-content-";

    @Autowired
    private CreditEmailService creditEmailService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /***
     * 添加任务，将任务放在队头
     *
     * @param creditEmail
     */
    public void addJob(CreditEmail creditEmail) {
        String id = creditEmail.getId().toString();
        redisTemplate.opsForValue().set(MAIL_DOWANLOD_JOB_CONTENT.concat(id),
                JSON.toJSONString(creditEmail));
        redisTemplate.opsForList().leftPush(MAIL_DOWANLOD_LIST_NAME, id);
    }

    /***
     * 执行任务完成,将任务放在队尾
     *
     * @return
     */
    public void doneJob(CreditEmail creditEmail) {
        String id = creditEmail.getId().toString();
        redisTemplate.opsForValue().set(MAIL_DOWANLOD_JOB_CONTENT.concat(id),
                JSON.toJSONString(creditEmail));
        redisTemplate.opsForList().rightPush(MAIL_DOWANLOD_LIST_NAME, id);
    }

    /***
     * 从头部获取任务执行内容
     *
     * @return
     */
    public CreditEmail getJob() {
        String id = redisTemplate.opsForList().leftPop(MAIL_DOWANLOD_LIST_NAME);
        if (StringUtils.isEmpty(id)) {
            return new CreditEmail();
        }
        String jsonString = redisTemplate.opsForValue().get(MAIL_DOWANLOD_JOB_CONTENT.concat(id));
        if (StringUtils.isEmpty(jsonString)) {
            return new CreditEmail();
        }
        return JSON.parseObject(jsonString, CreditEmail.class);
    }

    /***
     * 获取redis锁，默认时间ttlInSeconds
     *
     * @param key
     * @return
     */
    public boolean getLock(String key) {
        return incrementAndGet(key, ttlInSeconds) <= 1;
    }

    public int incrementAndGet(String key, long ttlInSeconds) {
        RedisAtomicInteger redisAtomicInteger = new RedisAtomicInteger(key,
                redisTemplate.getConnectionFactory());
        if (redisAtomicInteger.getExpire() < 0) {
            redisAtomicInteger.expire(ttlInSeconds, TimeUnit.SECONDS);
        }
        logger.debug("key:{} expire:{}", key, redisAtomicInteger.getExpire());
        return redisAtomicInteger.incrementAndGet();
    }

    /***
     * 初始化任务列表，程序启动自动调用
     */
    public void initJobList() {
        List<CreditEmail> list = creditEmailService.getCreditEmails();
        List<String> listJsonString = list.stream().map(e -> e.getId().toString())
                .collect(Collectors.toList());
        logger.debug("key:{},value:{}", MAIL_DOWANLOD_LIST_NAME, listJsonString);
        Long redisJobListSize = redisTemplate.opsForList().size(MAIL_DOWANLOD_LIST_NAME);
        if (list.size() == redisJobListSize || listJsonString == null
                || listJsonString.size() == 0) {
            return;
        }

        // 初始list
        redisTemplate.delete(MAIL_DOWANLOD_LIST_NAME);
        redisTemplate.opsForList().leftPushAll(MAIL_DOWANLOD_LIST_NAME, listJsonString);
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                DefaultStringRedisConnection stringRedisConnection = new DefaultStringRedisConnection(
                        connection);
                for (CreditEmail creditEmail : list) {
                    stringRedisConnection.set(
                            MAIL_DOWANLOD_JOB_CONTENT.concat(creditEmail.getId().toString()),
                            JSON.toJSONString(creditEmail));
                }
                return null;
            }
        });
    }

    /***
     * 删除任务
     *
     * @param id
     */
    public void removeJob(String id) {
        redisTemplate.delete(MAIL_DOWANLOD_JOB_CONTENT.concat(id));
    }

    /***
     * 解锁
     *
     * @param key
     */
    public void unLock(String key) {
        redisTemplate.delete(key);
    }

}
