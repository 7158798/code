package com.pay.aile.bill.utils;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay.aile.bill.contant.ErrorCodeContants;
import com.pay.aile.bill.exception.MailBillException;

/***
 * MailLoginUtil.java
 *
 * @author shinelon
 *
 * @date 2017年10月31日
 *
 */
public class MailLoginUtil {
    private static final Logger logger = LoggerFactory.getLogger(MailLoginUtil.class);

    /***
     * 自定义登录页面 注意使用后是否store
     *
     * @param mailAddrWithoutSuffix
     * @param password
     * @return
     * @throws MailBillException
     */
    public static Store login(Properties mailProps, String storeType, final String mailAddrWithoutSuffix,
            final String password) throws MailBillException {
        logger.info("begin landing user mail:{}", mailAddrWithoutSuffix);
        // 创建Session实例对象
        Session session = Session.getInstance(mailProps);
        // 创建pop3协议的Store对象
        try {
            Store store = session.getStore(storeType);
            store.connect(mailAddrWithoutSuffix, password);
            boolean connected = store.isConnected();
            logger.info("mail:{} \tis connected:{}", mailAddrWithoutSuffix, connected);
            if (!connected) {
                throw MailBillExceptionUtil.getWithLog(ErrorCodeContants.EMAIL_LOGIN_FAILED_CODE,
                        ErrorCodeContants.EMAIL_LOGIN_FAILED.getMsg(), logger);
            }
            return store;
        } catch (NoSuchProviderException e) {
            throw MailBillExceptionUtil.getWithLog(e, ErrorCodeContants.EMAIL_INVALID_PROTOCOL_CODE,
                    ErrorCodeContants.EMAIL_INVALID_PROTOCOL.getMsg(), logger);
        } catch (MessagingException e) {
            throw MailBillExceptionUtil.getWithLog(e, ErrorCodeContants.POP_NOT_OPEN_EXCEPTION_CODE,
                    ErrorCodeContants.POP_NOT_OPEN_EXCEPTION.getMsg(), logger);
        }
    }

}
