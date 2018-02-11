package com.pay.card.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "credit_user_info")
@Entity
public class CreditUserInfo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -6873335595608254460L;

    /**
     * 商户编号
     */
    private String customerNo;
    /**
     * 商户手机号
     */
    private String phoneNo;
    /**
     * 渠道标识
     */
    private String channel;

    public String getChannel() {
        return channel;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @Override
    public String toString() {
        return "CreditUserInfo [customerNo=" + customerNo + ", phoneNo=" + phoneNo + ", channel=" + channel + "]";
    }

}
