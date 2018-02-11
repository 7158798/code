package com.pay.aile.bill.service.mail.download.impl;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay.aile.bill.service.mail.download.BaseMailOperation;

/***
 * MailOperationImpl.java
 *
 * @author shinelon
 *
 * @date 2017年10月30日
 *
 */
@Service
public class MailHotOperationImpl extends BaseMailOperation {
    private static final Logger logger = LoggerFactory.getLogger(MailHotOperationImpl.class);

    @Override
    public Properties getMailProperties() {
        Properties props = new Properties();
        props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.imap.socketFactory.port", "993");
        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imap.host", "imap-mail.outlook.com");
        props.setProperty("mail.imap.port", "993");
        props.setProperty("mail.imap.auth.login.disable", "true");

        return props;
    }

    @Override
    protected String getStoreType() {
        return BaseMailOperation.STROE_IMAP;
    }
}
