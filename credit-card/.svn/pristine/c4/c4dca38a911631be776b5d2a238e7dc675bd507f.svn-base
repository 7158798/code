package com.pay.aile.bill.job;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.pay.aile.bill.entity.CreditEmail;

/**
 *
 * @Description:
 * @see: CoptRelationRedisJobHandle 此处填写需要参考的类
 * @version 2017年12月7日 下午3:30:10
 * @author chao.wang
 */
@Component
public class CopyRelationRedisJobHandle {

    public static final String COPY_RELATION_EMAIL_LIST = "aile-mail-copy-relation-email-list";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     *
     * @Description 将任务添加到队列尾部
     * @param creditEmail
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    public void addJob(CreditEmail creditEmail) {
        redisTemplate.opsForList().rightPush(COPY_RELATION_EMAIL_LIST, JSON.toJSONString(creditEmail));
    }

    /***
     * 从头部获取任务执行内容
     *
     * @return
     */
    public CreditEmail getJob() {
        String jsonString = redisTemplate.opsForList().leftPop(COPY_RELATION_EMAIL_LIST);
        if (StringUtils.isEmpty(jsonString)) {
            return new CreditEmail();
        }
        return JSON.parseObject(jsonString, CreditEmail.class);
    }

    /**
     *
     * @Description 若任务取出之后发现当前不满足执行的条件，那么重新加入人物队列等待条件满足
     * @param creditEmail
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    public void resetJob(CreditEmail creditEmail) {
        redisTemplate.opsForList().rightPush(COPY_RELATION_EMAIL_LIST, JSON.toJSONString(creditEmail));
    }

}
