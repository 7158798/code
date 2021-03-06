package com.pay.card.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * @ClassName: CreditBill
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jinjing
 * @date 2017年11月14日
 */
@Table(name = "credit_bill")
@Entity
@DynamicInsert
@DynamicUpdate
public class CreditBill extends BaseEntity implements Serializable {

    /**
     * @Fields field:field:{todo}(用一句话描述这个变量表示什么)
     */

    private static final long serialVersionUID = 2891740572930033796L;
    /**
     * 商户编号
     */
    private String customerNo;
    /**
     * 账户类型
     */
    private String accountType;

    /**
     * 银行编码
     */
    private String bankCode;

    /**
     * 账单开始时间
     */
    private Date beginDate;

    /**
     * 对象卡id
     */
    private CreditCard card;

    /**
     * 取现额度
     */
    private BigDecimal cash;

    /**
     * 消费/取现/其他费用
     */
    private BigDecimal consumption;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 信用额度
     */
    private BigDecimal credits;

    /**
     * 本期应还款额
     */
    private BigDecimal currentAmount;

    /**
     * 到期还款日
     */
    private Date dueDate;

    /**
     * 用户邮箱id
     */
    private Long emailId;

    /**
     * 用户邮箱id
     */
    private String email;

    /**
     * 账单结束时间
     */
    private Date endDate;

    private BigDecimal lastAmount;

    /**
     * 最低还款额
     */
    private BigDecimal minimum;

    /**
     * 月
     */
    private String month;

    /**
     * 还款/退货/费用返还
     */
    private BigDecimal repayment;

    /**
     * 有效标志1有效0无效
     */
    private Integer status;

    /**
     * 修改时间
     */
    private Date updateDate;

    /**
     * 年
     */
    private String year;
    /**
     * 日
     */
    private String billDay;
    private Integer newStatus;
    /**
     * 账单明细
     */
    // private List<CreditBillDetail> detailList;

    /**
     * 还款明细
     */
    // private List<CreditRepayment> repaymentList;

    public String getAccountType() {
        return accountType;
    }

    public String getBankCode() {
        return bankCode;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public String getBillDay() {
        return billDay;
    }

    @ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "card_id")
    public CreditCard getCard() {
        return card;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public BigDecimal getConsumption() {
        return consumption;
    }

    @Override
    public Date getCreateDate() {
        return createDate;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getEmail() {
        return email;
    }

    public Long getEmailId() {
        return emailId;
    }

    public Date getEndDate() {
        return endDate;
    }

    // @OneToMany(cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY,
    // orphanRemoval = false)
    // @JoinColumn(name = "bill_id")
    // public List<CreditBillDetail> getDetailList() {
    // return detailList;
    // }

    public BigDecimal getLastAmount() {
        return lastAmount;
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    public String getMonth() {
        return month;
    }

    /**
     * @return newStatus
     */
    public Integer getNewStatus() {
        return newStatus;
    }

    public BigDecimal getRepayment() {
        return repayment;
    }

    @Override
    public Integer getStatus() {
        return status;
    }

    @Override
    public Date getUpdateDate() {
        return updateDate;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    // @OneToMany(cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY,
    // orphanRemoval = false)
    // @JoinColumn(name = "bill_id")
    // public List<CreditRepayment> getRepaymentList() {
    // return repaymentList;
    // }

    public String getYear() {
        return year;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public void setBillDay(String billDay) {
        this.billDay = billDay;
    }

    public void setCard(CreditCard card) {
        this.card = card;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    public void setConsumption(BigDecimal consumption) {
        this.consumption = consumption;
    }

    @Override
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setCredits(BigDecimal credits) {
        this.credits = credits;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // public void setDetailList(List<CreditBillDetail> detailList) {
    // this.detailList = detailList;
    // }

    public void setEmailId(Long emailId) {
        this.emailId = emailId;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setLastAmount(BigDecimal lastAmount) {
        this.lastAmount = lastAmount;
    }

    public void setMinimum(BigDecimal minimum) {
        this.minimum = minimum;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * @param newStatus
     *            the newStatus to set
     */
    public void setNewStatus(Integer newStatus) {
        this.newStatus = newStatus;
    }

    public void setRepayment(BigDecimal repayment) {
        this.repayment = repayment;
    }

    @Override
    public void setStatus(Integer status) {
        this.status = status;
    }

    // public void setRepaymentList(List<CreditRepayment> repaymentList) {
    // this.repaymentList = repaymentList;
    // }

    @Override
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "CreditBill [customerNo=" + customerNo + ", accountType=" + accountType + ", bankCode=" + bankCode
                + ", beginDate=" + beginDate + ", card=" + card + ", cash=" + cash + ", consumption=" + consumption
                + ", createDate=" + createDate + ", credits=" + credits + ", currentAmount=" + currentAmount
                + ", dueDate=" + dueDate + ", emailId=" + emailId + ", email=" + email + ", endDate=" + endDate
                + ", lastAmount=" + lastAmount + ", minimum=" + minimum + ", month=" + month + ", repayment="
                + repayment + ", status=" + status + ", updateDate=" + updateDate + ", year=" + year + ", billDay="
                + billDay + "]";
    }

}
