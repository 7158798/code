package com.pay.aile.bill.enums;

/**
 *
 * @Description: 邮箱登录状态
 * @see: MailLoginStatus 此处填写需要参考的类
 * @version 2017年12月13日 下午2:09:52
 * @author chao.wang
 */
public enum MailLoginStatus {
    INIT("-1"), // 登陆中
    SUCCESS("1"), // 登录成功
    FAIL("0");
    private String value;

    MailLoginStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
