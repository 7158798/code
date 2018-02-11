package com.pay.aile.bill.enums;

/***
 * MailType.java
 *
 * @author shinelon
 *
 * @date 2017年10月30日
 *
 */
public enum EmailEnum {

	MAIL_126("126.com", "126"), MAIL_139("139.com", "139"), MAIL_163("163.com", "163"), MAIL_ALIYUN("aliyun.com",
			"阿里云"), MAIL_HOT("outlook.com", "outlook"), MAIL_QQ("qq.com", "qq"), MAIL_SINA("sina.com", "新浪");

	public static EmailEnum getMailType(String mailAddrSuffix) {
		for (EmailEnum tmpMailType : EmailEnum.values()) {
			if (tmpMailType.getKey().equals(mailAddrSuffix)) {
				return tmpMailType;
			}
		}
		return null;
	}

	private String key;

	private String value;

	EmailEnum(String key, String vlaue) {
		this.key = key;
		value = vlaue;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
