package com.pay.aile.bill.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;

/**
 * <p>
 * 邮件解析模板
 * </p>
 * @author yaoqiang.sun
 * @since 2017-11-02
 */
@TableName("credit_bill_detail")
public class CreditBillDetail extends Model<CreditBillDetail> {

    private static final long serialVersionUID = 1L;

    /**
     * 入账币种/金额
     */
    @TableField("accountable_amount")
    private String accountableAmount;
    @TableField("account_type")
    private String accountType;
    /**
     * 到期还款日
     */
    @TableField("bill_id")
    private Long billId;
    /**
     * 记账日志
     */
    @TableField("billing_date")
    private Date billingDate;
    /**
     * 创建时间
     */
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;
    private Long id;
    /**
     * 有效标志1有效0无效
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer status;
    /**
     * 交易币种/金额
     */
    @TableField("transaction_amount")
    private String transactionAmount;
    /**
     * 交易币种
     */
    @TableField("transaction_currency")
    private String transactionCurrency;

    /**
     * 交易日期
     */
    @TableField("transaction_date")
    private Date transactionDate;
    /**
     * 交易说明
     */
    @TableField("transaction_description")
    private String transactionDescription;

    /**
     * 修改时间
     */
    @TableField(value = "update_date", fill = FieldFill.UPDATE)
    private Date updateDate;

    @TableField(exist = false)
    private String cardNumbers;

    /**
     * 招商银行等 账单日之前会发送消费明细邮件,这种邮件解析出的明细存放在此处
     */
    @TableField(value = "card_id")
    private Long cardId;

    private Long userId;

    public String getAccountableAmount() {
        return accountableAmount;
    }

    public String getAccountType() {
        return accountType;
    }

    public Long getBillId() {
        return billId;
    }

    public Date getBillingDate() {
        return billingDate;
    }

    public Long getCardId() {
        return cardId;
    }

    public String getCardNumbers() {
        return cardNumbers;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Long getId() {
        return id;
    }

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

    public Date getUpdateDate() {
        return updateDate;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }

    public void setAccountableAmount(String accountableAmount) {
        this.accountableAmount = accountableAmount;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public void setBillingDate(Date billingDate) {
        this.billingDate = billingDate;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public void setCardNumbers(String cardNumbers) {
        this.cardNumbers = cardNumbers;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CreditBillDetail [accountableAmount=" + accountableAmount + ", accountType=" + accountType + ", billId=" + billId
                + ", billingDate=" + billingDate + ", createDate=" + createDate + ", id=" + id + ", status=" + status
                + ", transactionAmount=" + transactionAmount + ", transactionCurrency=" + transactionCurrency + ", transactionDate="
                + transactionDate + ", transactionDescription=" + transactionDescription + ", updateDate=" + updateDate + ", cardNumbers="
                + cardNumbers + ", cardId=" + cardId + "]";
    }
}
