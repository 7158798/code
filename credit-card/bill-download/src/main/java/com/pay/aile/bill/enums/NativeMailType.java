package com.pay.aile.bill.enums;

import com.pay.aile.bill.service.natives.mail.download.NativeMailOperation;
import com.pay.aile.bill.service.natives.mail.download.impl.NativeMail126OperationImpl;
import com.pay.aile.bill.service.natives.mail.download.impl.NativeMail163OperationImpl;
import com.pay.aile.bill.service.natives.mail.download.impl.NativeMailQQOperationImpl;
import com.pay.aile.bill.service.natives.mail.search.NativeMailSearch;
import com.pay.aile.bill.service.natives.search.impl.Native126MailSearch;
import com.pay.aile.bill.service.natives.search.impl.Native163MailSearch;
import com.pay.aile.bill.service.natives.search.impl.NativeQQMailSearch;

/***
 * MailType.java
 *
 * @author shinelon
 *
 * @date 2017年10月30日
 *
 */
public enum NativeMailType {


    MAIL_QQ("qq.com", NativeMailQQOperationImpl.class,NativeQQMailSearch.class),
    MAIL_126("126.com", NativeMail126OperationImpl.class,Native126MailSearch.class),
    MAIL_163("163.com", NativeMail163OperationImpl.class,Native163MailSearch.class)

    ;

    public static NativeMailType getMailType(String mailAddrSuffix) {
        for (NativeMailType tmpMailType : NativeMailType.values()) {
            if (tmpMailType.getKey().equals(mailAddrSuffix)) {
                return tmpMailType;
            }
        }
        return null;
    }

    private Class<? extends NativeMailOperation> classes;
    private Class<? extends NativeMailSearch> searchClasses;
    
    private String key;

    NativeMailType(String key, Class<? extends NativeMailOperation> classes, Class<? extends NativeMailSearch> searchClasses) {
        this.key = key;
        this.classes = classes;
        this.searchClasses = searchClasses;
    }

    public Class<? extends NativeMailOperation> getClzz() {
        return classes;
    }
    public Class<? extends NativeMailSearch> getSearchClzz() {
        return searchClasses;
    }

    public String getKey() {
        return key;
    }
}
