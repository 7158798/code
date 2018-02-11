package com.pay.aile.bill.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Component;

/**
 *
 * @author zhibin.cui
 * @description 邮件发送工具类
 */
@Component
public class MailSendUtil {

    public void sendUtil(String content, String subject, String recipients, String addresser, String pasword,
            String host, String port) {
        try {
            // String host = "smtp.139.com";// 这是QQ邮箱的smtp服务器地址
            // String port = "25"; // 端口号
            /*
             * Properties是一个属性对象，用来创建Session对象
             */
            Properties props = new Properties();
            props.setProperty("mail.smtp.host", host);
            props.setProperty("mail.smtp.port", port);
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.smtp.ssl.enable", "false");// "true"
            props.setProperty("mail.smtp.connectiontimeout", "5000");
            /*
             * Session类定义了一个基本的邮件对话。
             */
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    // 登录用户名密码
                    return new PasswordAuthentication(addresser, pasword);
                }
            });
            session.setDebug(true);
            /*
             * Transport类用来发送邮件。 传入参数smtp，transport将自动按照smtp协议发送邮件。
             */
            Transport transport = session.getTransport("smtp");// "smtps"
            transport.connect(host, addresser, pasword);
            /*
             * Message对象用来储存实际发送的电子邮件信息
             */
            MimeMessage message = new MimeMessage(session);
            message.setSubject(subject);
            // 消息发送者接收者设置(发件地址，昵称)，收件人看到的昵称是这里设定的
            message.setFrom(new InternetAddress(addresser));
            message.addRecipients(Message.RecipientType.TO,
                    new InternetAddress[] {
                            // 消息接收者(收件地址，昵称)
                            // 不过这个昵称貌似没有看到效果
                            new InternetAddress(recipients), });
            message.saveChanges();

            // 设置邮件内容及编码格式
            // 后一个参数可以不指定编码，如"text/plain"，但是将不能显示中文字符
            message.setContent(content, "text/html;charset=UTF-8");
            // 发送
            Transport.send(message);
            transport.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
