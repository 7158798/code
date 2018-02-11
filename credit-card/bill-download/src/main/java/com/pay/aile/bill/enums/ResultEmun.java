package com.pay.aile.bill.enums;

import com.alibaba.fastjson.JSONObject;

public enum ResultEmun {
    SUCCESS("0000", "操作成功"), FAIL("0001", "操作失败"), USER_REEOR("0002", "用户已存在"), REPEAT("0004", "卡号重复"), EMAIL_LOGIN_FAIL("0005",
            "账号或密码错误或POP3服务未开启，请检查后重试"), EMAIL_LOGIN_OR_SERVER_FAIL("0006", "账号或密码错误或服务器设置错误，请检查后重试");

    /**
     * 消息
     */
    private String msg;
    /**
     * 编码
     */
    private String code;

    private ResultEmun(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public JSONObject getJsonMsg() {
        String resultMsg = "{\"code\":\"%s\",\"msg\":\"%s\"}";
        return JSONObject.parseObject(String.format(resultMsg, code, msg));
    }

    public String getMsg() {
        String resultMsg = "{\"code\":\"%s\",\"msg\":\"%s\"}";
        return String.format(resultMsg, code, msg);
    }
}
