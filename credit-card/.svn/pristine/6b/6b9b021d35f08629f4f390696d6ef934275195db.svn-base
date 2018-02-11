package com.pay.aile.bill.enums;

/**
 * 
 * @author Charlie
 * @description 货币类型/账户类型
 */
public enum AccountTypeEnum {
    RMB("人民币"), USD("美元");

    private String cnName;

    private AccountTypeEnum(String cnName) {
        this.cnName = cnName;
    }

    public static AccountTypeEnum getByCnName(String cnName) {
        for (AccountTypeEnum a : values()) {
            if (cnName.equals(a.cnName)) {
                return a;
            }
        }
        return null;
    }
}
