package com.pay.card.bean;

import java.io.Serializable;

public class CreditBillBean implements Serializable {

    private static final long serialVersionUID = -9035980416693684554L;

    /**
     * 账单id
     */
    private Long billId;
    /**
     * 年月 格式:yyyy_MM
     */
    private String yearMonth;
    /**
     * 卡id
     */
    private Long cardId;
    /**
     * 手机号
     */
    private String phoneNo;
    /**
     * 商户编号
     */
    private String customerNo;
    /**
     * 渠道号
     */
    private String channel;
    /**
     * 邮件
     */
    private String email;
    /**
     * 邮箱密码
     */
    private String password;
    /**
     * 用户id
     */
    private Long userId;

    public Long getBillId() {
        return billId;
    }

    public Long getCardId() {
        return cardId;
    }

    public String getChannel() {
        return channel;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public Long getUserId() {
        return userId;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    @Override
    public String toString() {
        return "CreditBillBean [billId=" + billId + ", yearMonth=" + yearMonth + ", cardId=" + cardId + ", phoneNo=" + phoneNo
                + ", customerNo=" + customerNo + ", channel=" + channel + ", email=" + email + ", password=" + password + ", userId="
                + userId + "]";
    }

}
