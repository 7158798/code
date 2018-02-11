package com.pay.card.enums;

public enum RedisStatusEnum {

    QUERY("query", "查询"), UPDATE("update", "更新");

    public static String getMsg(String code) {
        if (code != null) {
            for (RedisStatusEnum enums : RedisStatusEnum.values()) {
                if (enums.getCode().equals(code)) {
                    return enums.getMsg();
                }
            }
        }

        return "";
    }

    /**
     * 消息
     */
    private String msg;

    /**
     * 编码
     */
    private String code;

    private RedisStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
