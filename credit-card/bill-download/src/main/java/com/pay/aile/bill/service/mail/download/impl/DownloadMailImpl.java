package com.pay.aile.bill.service.mail.download.impl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.pay.aile.bill.contant.ErrorCodeContants;
import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.enums.MailType;
import com.pay.aile.bill.exception.MailBillException;
import com.pay.aile.bill.job.CopyRelationRedisJobHandle;
import com.pay.aile.bill.job.RedisJobHandle;
import com.pay.aile.bill.service.mail.download.BaseMailOperation;
import com.pay.aile.bill.service.mail.download.DownloadMail;
import com.pay.aile.bill.utils.MailProcessStatusCacheUtil;
import com.pay.aile.bill.utils.SpringContextUtil;

/***
 * DownloadMailImpl.java
 *
 * @author shinelon
 *
 * @date 2017年10月30日
 *
 */
@Service
public class DownloadMailImpl implements DownloadMail {

    private static final Logger logger = LoggerFactory.getLogger(DownloadMailImpl.class);
    /**
     * 邮箱不能在3分钟内连续登录，故设置此值
     */
    private static final String COPY_RELATION_EMAIL_EXPIRE_NAME = "bill-download-copy-relation-email-expire-name-";
    /**
     * 180s，即3分钟
     */
    private static final int COPY_RELATION_EMAIL_EXPIRE_TIME = 180;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisJobHandle redisJobHandle;
    @Autowired
    private CopyRelationRedisJobHandle copyRelationRedisJobHandle;

    @Override
    public void copyRelation(CreditEmail creditEmail) throws MailBillException {
        String creditEmailId = String.valueOf(creditEmail.getId());
        CreditEmail oldCreditEmail = redisJobHandle.getContent(creditEmailId);
        if (oldCreditEmail.getId() == null) {
            return;
        }
        if (oldCreditEmail.getIsNew()) {
            // isNew:说明有任务正在下载解析邮件,则重新加入任务队列等待执行
            logger.info("********此邮箱正在执行邮件下载解析任务,稍后执行......");
            creditEmail.setLastJobTimestamp(System.currentTimeMillis());
            copyRelationRedisJobHandle.resetJob(creditEmail);
            return;
        }
        // 任务执行之初,初始化邮件下载解析执行状态
        MailProcessStatusCacheUtil.initMailProcessStatus(creditEmail);
        String[] mailParms = StringUtils.split(creditEmail.getEmail(), "@");
        logger.info(mailParms[1]);
        logger.info(MailType.getMailType(mailParms[1]).getClzz().getName());
        BaseMailOperation mailOperation = SpringContextUtil.getBean(MailType.getMailType(mailParms[1]).getClzz());
        if (copyRelationLoginFrequently(String.valueOf(creditEmailId))) {
            // 三分钟内频繁登录,则不进行登录操作,直接copy关系
            if (!org.springframework.util.StringUtils.hasText(creditEmail.getPassword())
                    || !org.springframework.util.StringUtils.hasText(oldCreditEmail.getPassword())
                    || !creditEmail.getPassword().equals(oldCreditEmail.getPassword())) {
                // 密码错误,登录失败
                MailProcessStatusCacheUtil.loginFail(creditEmail);
                return;
            } else {
                MailProcessStatusCacheUtil.loginSuccess(creditEmail);
            }
            mailOperation.copyRelation(creditEmail);
        } else {
            setCopyRelationLoginFlag(creditEmailId);
            mailOperation.copyRelationLoginSearch(creditEmail);
        }
    }

    @Override
    public void execute(CreditEmail creditEmail) throws Exception {
        String[] mailParms = StringUtils.split(creditEmail.getEmail(), "@");
        logger.info(mailParms[1]);
        /*
         * logger.info(MailType.getMailType(mailParms[1]).getClzz().getName());
         * BaseMailOperation mailOperation =
         * SpringContextUtil.getBean(MailType.getMailType(mailParms[1]).getClzz(
         * )); if (mailOperation == null) { MailCustomOperationImpl
         * customOperation =
         * SpringContextUtil.getBean(MailCustomOperationImpl.class);
         * customOperation.setCreditEmail(creditEmail); mailOperation =
         * customOperation; }
         */
        BaseMailOperation mailOperation = null;
        if (MailType.getMailType(mailParms[1]) != null) {
            mailOperation = SpringContextUtil.getBean(MailType.getMailType(mailParms[1]).getClzz());
        } else {
            mailOperation = SpringContextUtil.getBean(MailCustomOperationImpl.class);
            ((MailCustomOperationImpl) mailOperation).setCreditEmail(creditEmail);
        }

        mailOperation.downloadMail(creditEmail);
    }

    @Override
    public void execute(CreditEmail creditEmail, RedisJobHandle redisJobHandle) throws Exception {
        String exceptinoMsg = "";
        long startTime = System.currentTimeMillis();

        try {
            execute(creditEmail);
        } catch (Exception e) {
            exceptinoMsg = e.getMessage();
            logger.warn(e.getMessage(), e);

            if (e instanceof MailBillException) {
                MailBillException me = (MailBillException) e;
                if (me.getErrorCode() == ErrorCodeContants.EMAIL_LOGIN_FAILED_CODE) {
                    throw new Exception("邮箱登录失败" + ErrorCodeContants.EMAIL_LOGIN_FAILED_CODE);
                }
            }
        }

        long endTime = System.currentTimeMillis();
        creditEmail.setLastJobTimestamp(endTime);
        creditEmail.setLastJobExecutionTime(endTime - startTime);
        creditEmail.setRemarks(exceptinoMsg);
        creditEmail.setIsNew(false);
        creditEmail.setDownload(true);
        redisJobHandle.doneJob(creditEmail);
        if (!StringUtils.isEmpty(exceptinoMsg)) {
            throw new Exception(exceptinoMsg);
        }
    }

    @Override
    public void execute(final String mailAddr, final String password) throws Exception {

        String[] mailParms = StringUtils.split(mailAddr, "@");
        BaseMailOperation mailOperation = null;
        // 做非空校验
        if (MailType.getMailType(mailParms[1]) != null) {
            mailOperation = SpringContextUtil.getBean(MailType.getMailType(mailParms[1]).getClzz());
        } else {
            mailOperation = SpringContextUtil.getBean(MailCustomOperationImpl.class);
        }
        if (mailOperation != null) {
            mailOperation.downloadMail(mailAddr, password);
        }

    }

    /**
     *
     * @Description 是否三分钟内频繁登录
     * @param id
     * @return
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    private boolean copyRelationLoginFrequently(String id) {
        String value = redisTemplate.opsForValue().get(COPY_RELATION_EMAIL_EXPIRE_NAME + id);
        return org.springframework.util.StringUtils.hasText(value);
    }

    /**
     *
     * @Description 设置已登录标识
     * @param id
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    private void setCopyRelationLoginFlag(String id) {
        redisTemplate.opsForValue().set(COPY_RELATION_EMAIL_EXPIRE_NAME + id, "1", COPY_RELATION_EMAIL_EXPIRE_TIME,
                TimeUnit.SECONDS);
    }

}
