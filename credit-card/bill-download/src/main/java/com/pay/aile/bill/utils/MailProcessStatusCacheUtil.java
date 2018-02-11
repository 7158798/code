package com.pay.aile.bill.utils;

import com.pay.aile.bill.contant.Constant;
import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.enums.MailLoginStatus;

/**
 *
 * @Description: 邮件下载解析过程状态处理工具类
 * @see: MailProcessCacheUtil 此处填写需要参考的类
 * @version 2017年12月13日 下午2:54:45
 * @author chao.wang
 */
public class MailProcessStatusCacheUtil {

    /**
     *
     * @Description 任务执行之初,初始化邮件下载解析执行状态
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    public static void initMailProcessStatus(CreditEmail creditEmail) {
        // credit_card_analyzed_status_+email+userId
        JedisClusterUtils.saveString(Constant.REDIS_ANALYSIS_STATUS + creditEmail.getEmail()
                + Constant.EMAIL_USERID_SEPARATOR + creditEmail.getUserId(), "0");
        JedisClusterUtils.saveString(Constant.REDIS_ANALYZED_STATUS + creditEmail.getEmail()
                + Constant.EMAIL_USERID_SEPARATOR + creditEmail.getUserId(), "0");
        // 郵件數量
        JedisClusterUtils.saveString(Constant.REDIS_EMAIL_NUMBER_STATUS + creditEmail.getEmail()
                + Constant.EMAIL_USERID_SEPARATOR + creditEmail.getUserId(), "0");
        JedisClusterUtils.saveString(Constant.REDIS_EMAIL_READ_NUMBER_STATUS + creditEmail.getEmail()
                + Constant.EMAIL_USERID_SEPARATOR + creditEmail.getUserId(), "0");

        JedisClusterUtils.delKey(Constant.REDIS_CARDS + creditEmail.getEmail() + Constant.EMAIL_USERID_SEPARATOR
                + creditEmail.getUserId());
        JedisClusterUtils
                .delKey(String.format(Constant.REDIS_EXISTS_CARD_NO, creditEmail.getEmail(), creditEmail.getUserId()));

        loginInit(creditEmail);
    }

    /**
     *
     * @Description 邮箱登录失败
     * @param creditEmail
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    public static void loginFail(CreditEmail creditEmail) {
        JedisClusterUtils.hashSet(Constant.REDIS_LOGGIN_STATUS,
                creditEmail.getEmail() + Constant.EMAIL_USERID_SEPARATOR + creditEmail.getUserId(),
                MailLoginStatus.FAIL.getValue());
    }

    /**
     *
     * @Description 邮箱登录中
     * @param creditEmail
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    public static void loginInit(CreditEmail creditEmail) {
        JedisClusterUtils.hashSet(Constant.REDIS_LOGGIN_STATUS,
                creditEmail.getEmail() + Constant.EMAIL_USERID_SEPARATOR + creditEmail.getUserId(),
                MailLoginStatus.INIT.getValue());
    }

    /**
     *
     * @Description 邮箱登录成功
     * @param creditEmail
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    public static void loginSuccess(CreditEmail creditEmail) {
        JedisClusterUtils.hashSet(Constant.REDIS_LOGGIN_STATUS,
                creditEmail.getEmail() + Constant.EMAIL_USERID_SEPARATOR + creditEmail.getUserId(),
                MailLoginStatus.SUCCESS.getValue());
    }

    /**
     *
     * @Description 设置邮件已解析数量
     * @param creditEmail
     * @param num
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    public static void setAnalyzedBillNum(CreditEmail creditEmail, int num) {
        // credit_card_analyzed_status_+email+userId
        JedisClusterUtils.saveString(Constant.REDIS_ANALYZED_STATUS + creditEmail.getEmail()
                + Constant.EMAIL_USERID_SEPARATOR + creditEmail.getUserId(), String.valueOf(num));
    }

    /**
     *
     * @Description 设置邮件已解析出的卡
     * @param creditEmail
     * @param cardjson
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    public static void setAnalyzedCards(CreditEmail creditEmail, String cardjson) {
        JedisClusterUtils.setSave(Constant.REDIS_CARDS + creditEmail.getEmail() + Constant.EMAIL_USERID_SEPARATOR
                + creditEmail.getUserId(), cardjson);
    }

    /**
     *
     * @Description 设置此次未下载到邮件
     * @param creditEmail
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    public static void setDownloadNothing(CreditEmail creditEmail) {
        setDownloadNum(creditEmail, -1);
    }

    /**
     *
     * @Description 设置下载到的邮件数量
     * @param creditEmail
     * @param num
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    public static void setDownloadNum(CreditEmail creditEmail, int num) {
        JedisClusterUtils.saveString(Constant.REDIS_ANALYSIS_STATUS + creditEmail.getEmail()
                + Constant.EMAIL_USERID_SEPARATOR + creditEmail.getUserId(), String.valueOf(num));
    }

    /**
     * 設置郵件數量
     * 
     * @param creditEmail
     * @param num
     */
    public static void setEmailNum(CreditEmail creditEmail, int num) {
        // credit_card_analyzed_status_+email+userId
        JedisClusterUtils.saveString(Constant.REDIS_EMAIL_NUMBER_STATUS + creditEmail.getEmail()
                + Constant.EMAIL_USERID_SEPARATOR + creditEmail.getUserId(), String.valueOf(num));
    }

    /**
     * 設置已讀郵件數量
     * 
     * @param creditEmail
     * @param num
     */
    public static void setReadEmailNum(CreditEmail creditEmail, int num) {
        // credit_card_analyzed_status_+email+userId
        JedisClusterUtils.saveString(Constant.REDIS_EMAIL_READ_NUMBER_STATUS + creditEmail.getEmail()
                + Constant.EMAIL_USERID_SEPARATOR + creditEmail.getUserId(), String.valueOf(num));
    }
}
