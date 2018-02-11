package com.pay.card.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Table(name = "credit_repayment")
@Entity
@DynamicInsert
@DynamicUpdate
public class CreditRepayment extends BaseEntity implements Serializable {

    /**
     * @Fields field:field:{todo}(用一句话描述这个变量表示什么)
     */

    private static final long serialVersionUID = 487958089381735994L;

    /**
     * 还款金额
     */
    private BigDecimal amount;
    /**
     * 账单
     */
    private CreditBill bill;
    /**
     * 还款类型 0 标记还款,1 标记已还清 ,2 实际还款 ,3 账单还款
     */
    private Integer type;
    /**
     * 信用卡id
     */
    private Long cardId;
    /**
     * 信用卡id
     */
    private String month;
    /**
     * 信用卡id
     */
    private String year;

    /**
     * 用户
     */
    private CreditUserInfo userInfo;

    public BigDecimal getAmount() {
        return amount;
    }

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "bill_id")
    public CreditBill getBill() {
        return bill;
    }

    /**
     * @return cardId
     */

    public Long getCardId() {
        return cardId;
    }

    public String getMonth() {
        return month;
    }

    public Integer getType() {
        return type;
    }

    /**
     * @return userInfo
     */
    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    public CreditUserInfo getUserInfo() {
        return userInfo;
    }

    public String getYear() {
        return year;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setBill(CreditBill bill) {
        this.bill = bill;
    }

    /**
     * @param cardId
     *            the cardId to set
     */

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @param userInfo
     *            the userInfo to set
     */

    public void setUserInfo(CreditUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "CreditRepayment [amount=" + amount + ", bill=" + bill + ", type=" + type + ", cardId=" + cardId + ", month=" + month
                + ", year=" + year + ", userInfo=" + userInfo + "]";
    }

}
