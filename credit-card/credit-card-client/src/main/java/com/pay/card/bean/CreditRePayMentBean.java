package com.pay.card.bean;

import java.io.Serializable;

public class CreditRePayMentBean implements Serializable {

    private static final long serialVersionUID = 6760393659617851154L;

    private Long id;

    private String type;

    private String typeName;

    private String paymentDate;

    private String paymentAamout;

    private String month;

    public Long getId() {
        return id;
    }

    public String getMonth() {
        return month;
    }

    public String getPaymentAamout() {
        return paymentAamout;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public String getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setPaymentAamout(String paymentAamout) {
        this.paymentAamout = paymentAamout;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "CreditRePayMentBean [id=" + id + ", type=" + type + ", typeName=" + typeName + ", paymentDate=" + paymentDate
                + ", paymentAamout=" + paymentAamout + ", month=" + month + "]";
    }

}
