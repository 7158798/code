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
public class MailSinaOperationImpl extends BaseMailOperation {
	private static final Logger logger = LoggerFactory.getLogger(MailSinaOperationImpl.class);

	/***
	 * 获取搜索邮件关键字
	 *
	 * @return
	 */

	@Override
	public Properties getMailProperties() {
		Properties props = new Properties();
		// 协议
		props.setProperty("mail.store.protocol", "pop3");
		// 端口
		props.setProperty("mail.pop3.port", "110");
		// 服务器地址
		props.setProperty("mail.pop3.host", "pop.126.com");
		return props;
	}

}
