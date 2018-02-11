package com.pay.aile.bill.service.mail.download.impl;

import java.util.Properties;

import org.springframework.stereotype.Service;

import com.pay.aile.bill.service.mail.download.BaseMailOperation;

/***
 * Mail139OperationImpl.java
 *
 * @author shinelon
 *
 * @date 2017年10月31日
 *
 */
@Service
public class Mail139OperationImpl extends BaseMailOperation {

    @Override
    public Properties getMailProperties() {
        Properties props = new Properties();
        // 协议
        props.setProperty("mail.store.protocol", "pop3");
        // 端口
        props.setProperty("mail.pop3.port", "110");
        // 服务器地址
        props.setProperty("mail.pop3.host", "pop.139.com");
        return props;
    }

}
