package com.pay.card.enums;

public enum CreditBankCodeEnum {

    CITIC("CITIC", "中信银行"), GDB("GDB", "广发银行"), HQ("HQ", "花旗"), SH("SH", "上海");

    public static String getMsg(Integer code) {
        if (code != null) {
            for (ChannelEnum enums : ChannelEnum.values()) {
                if (enums.getCode().equals(code.toString())) {
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

    private CreditBankCodeEnum(String code, String msg) {
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
