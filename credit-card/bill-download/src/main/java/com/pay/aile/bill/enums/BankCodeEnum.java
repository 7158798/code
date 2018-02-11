package com.pay.aile.bill.enums;

/**
 *
 * @author Charlie
 * @description 银行编码
 */
public enum BankCodeEnum {

    PAB("PAB", "平安", "平安"),
    CZB("CZB", "浙商", "浙商"),
    BSB("BSB", "包商", "包商"),
    ABC("ABC", "农业", "农银"),
    BCM("BCM", "交通", "交银"),
    BOB("BOB", "北京", "北京"),
    BOC("BOC", "中国银行", "中国"),
    CCB("CCB", "建设", "建设"),
    CEB("CEB", "光大", "光大"),
    CIB("CIB", "兴业", "兴业"),
    CITIC("CITIC", "中信", "中信"),
    CMB("CMB", "招商,信用管家消费提醒", "招商"),
    CMBC("CMBC", "民生", "民生"),
    GDB("GDB", "广发", "广发"),
    HXB("HXB", "华夏", "华夏"),
    ICBC("ICBC", "工商", "工商"),
    SDB("SDB", "深圳发展", "深发"),
    SPDB("SPDB", "浦发", "浦发"),
    PSBC("PSBC", "邮储", "邮储");

    public static BankCodeEnum getByBankCode(String bankCode) {
        for (BankCodeEnum c : values()) {
            if (bankCode.equals(c.bankCode)) {
                return c;
            }
        }
        return null;
    }

    public static BankCodeEnum getByKeyword(String str) {
        for (BankCodeEnum c : values()) {
            String[] keywords = c.getKeywords().split(",");
            for (int i = 0; i < keywords.length; i++) {
                String key = keywords[i];
                if (str.contains(key)) {
                    return c;
                }
            }
        }
        return null;
    }

    private String bankCode;

    private String keywords;

    private String shortName;

    private BankCodeEnum(String bankCode, String keywords, String shortName) {
        this.bankCode = bankCode;
        this.keywords = keywords;
        this.shortName = shortName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getShortName() {
        return shortName;
    }

}
