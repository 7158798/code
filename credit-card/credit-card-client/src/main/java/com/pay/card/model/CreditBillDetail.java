package com.pay.card.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * @ClassName: CreditBillDetail
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jinjing
 * @date 2017年11月14日
 */

@Table(name = "credit_bill_detail")
@Entity
@DynamicInsert
@DynamicUpdate
public class CreditBillDetail extends BaseEntity implements Serializable {

    /**
     * @Fields field:field:{todo}(用一句话描述这个变量表示什么)
     */

    private static final long serialVersionUID = -2032674491190360955L;
    /**
     * 入账币种/金额
     */
    private String accountableAmount;
    private String accountType;
    /**
     * 到期还款日
     */
    private CreditBill bill;
    /**
     * 记账日志
     */
    private Date billingDate;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 有效标志1有效0无效
     */
    private Integer status;
    /**
     * 交易币种/金额
     */
    private String transactionAmount;
    /**
     * 交易币种
     */
    private String transactionCurrency;

    /**
     * 交易日期
     */
    private Date transactionDate;
    /**
     * 交易说明
     */
    private String transactionDescription;

    /**
     * 修改时间
     */
    private Date updateDate;

    /**
     * 用户id
     */
    private Long userId;

    private Long cardId;

    public String getAccountableAmount() {
        return accountableAmount;
    }

    public String getAccountType() {
        return accountType;
    }

    @ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "bill_id")
    public CreditBill getBill() {
        return bill;
    }

    public Date getBillingDate() {
        return billingDate;
    }

    public Long getCardId() {
        return cardId;
    }

    @Override
    public Date getCreateDate() {
        return createDate;
    }

    @Override
    public Integer getStatus() {
        return status;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public String getTransactionCurrency() {
        return transactionCurrency;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    @Override
    public Date getUpdateDate() {
        return updateDate;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    public void setAccountableAmount(String accountableAmount) {
        this.accountableAmount = accountableAmount;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setBill(CreditBill bill) {
        this.bill = bill;
    }

    public void setBillingDate(Date billingDate) {
        this.billingDate = billingDate;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    @Override
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public void setTransactionCurrency(String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    @Override
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CreditBillDetail [accountableAmount=" + accountableAmount + ", accountType=" + accountType + ", bill=" + bill
                + ", billingDate=" + billingDate + ", createDate=" + createDate + ", status=" + status + ", transactionAmount="
                + transactionAmount + ", transactionCurrency=" + transactionCurrency + ", transactionDate=" + transactionDate
                + ", transactionDescription=" + transactionDescription + ", updateDate=" + updateDate + ", userId=" + userId + ", cardId="
                + cardId + "]";
    }

}
