package com.pay.aile.bill.service.mail.download.impl;

import java.util.Properties;

import org.springframework.stereotype.Service;

import com.pay.aile.bill.service.mail.download.BaseMailOperation;

/***
 * MailQQOperationImpl.java
 *
 * @author shinelon
 *
 * @date 2017年10月31日
 *
 */
@Service
public class MailQQOperationImpl extends BaseMailOperation {

    @Override
    public Properties getMailProperties() {
        Properties props = new Properties();
        props.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.pop3.socketFactory.fallback", "false");
        props.setProperty("mail.store.protocol", "pop3");
        props.setProperty("mail.pop3.port", "995");
        props.setProperty("mail.pop3.host", "pop.qq.com");
        return props;
    }

}
