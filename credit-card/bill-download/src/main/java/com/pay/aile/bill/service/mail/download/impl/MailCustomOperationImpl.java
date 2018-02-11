package com.pay.aile.bill.service.mail.download.impl;

import java.util.Properties;

import org.springframework.stereotype.Service;

import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.enums.CommonStatus;
import com.pay.aile.bill.service.mail.download.BaseMailOperation;

/**
 *
 * @Description: 自定义的邮箱
 * @see: MailCustomOperationImpl 此处填写需要参考的类
 * @version 2017年12月15日 下午5:56:29
 * @author chao.wang
 */
@Service
public class MailCustomOperationImpl extends BaseMailOperation {

    private final ThreadLocal<CreditEmail> localCreditEmail = new ThreadLocal<>();

    @Override
    public Properties getMailProperties() {
        CreditEmail email = localCreditEmail.get();

        Properties props = new Properties();
        // 协议
        props.setProperty("mail.store.protocol", email.getProtocol());
        // 端口
        props.setProperty(String.format("mail.%s.port", email.getProtocol()), email.getPort());
        // 服务器地址
        props.setProperty(String.format("mail.%s.host", email.getProtocol()), email.getHost());
        if (email.getEnableSsl() == CommonStatus.AVAILABLE.value) {
            props.setProperty(String.format("mail.%s.socketFactory.class", email.getProtocol()),
                    "javax.net.ssl.SSLSocketFactory");
        }
        localCreditEmail.remove();
        return props;
    }

    public void setCreditEmail(CreditEmail creditEmail) {
        localCreditEmail.set(creditEmail);
    }

}
