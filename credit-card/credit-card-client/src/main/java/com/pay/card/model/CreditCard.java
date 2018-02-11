package com.pay.card.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Table(name = "credit_card")
@Entity
@DynamicInsert
@DynamicUpdate
public class CreditCard extends BaseEntity implements Serializable {

    /**
     * @Fields
     */

    private static final long serialVersionUID = -8413068283560221578L;

    /**
     * 商户编号
     */
    private String customerNo;

    /**
     * 商户手机号
     */
    private String phoneNo;

    /**
     * 所属银行
     */
    private CreditBank bank;

    /**
     * 到期还款日期
     */
    private Date dueDate;

    /**
     * 到期还款日
     */
    private String dueDay;
    /**
     * 账单日
     */
    private String billDay;

    /**
     * 账单金额
     */
    private BigDecimal billAmount;

    /**
     * 最低还款额
     */
    private BigDecimal minimum;

    /**
     * 最低还款额
     */
    private BigDecimal integral;

    /**
     * 还款金额
     */
    private BigDecimal repayment;

    /**
     * 持卡人
     */

    private String cardholder;

    /**
     * 取现额度
     */
    private BigDecimal cash;

    /**
     * 已消费金额
     */
    private BigDecimal consumption;

    /**
     * 信用额度
     */
    private BigDecimal credits;

    /**
     * 名称
     */
    private String name;

    /**
     * 完整卡号
     */
    @Column(name = "complete_numbers")
    private String numbers;

    /**
     * 原始卡号
     */
    @Column(name = "numbers")
    private String completeNumbers;
    /**
     * 预借现金额度
     */
    private String prepaidCashAmount;

    // /**
    // * 创建时间
    // */
    // private Date createDate;

    /**
     * 账单数量
     */
    private Integer billSize;

    /**
     * 未出账金额
     */
    private BigDecimal notAccounted;

    /**
     * 邮件
     */
    private String email;

    /**
     * 还款
     */
    // private List<CreditRepayment> repaymentList;

    /**
     * 还款类型 0 邮箱 1 手工添加
     */
    private Integer source;

    /**
     * 用户和卡的关系
     */
    @Transient
    private CreditUserCardRelation userCardRelation;

    /**
     * 账单开始时间
     */
    private Date beginDate;
    /**
     * 账单结束时间
     */
    private Date endDate;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "bank_id")
    public CreditBank getBank() {
        return bank;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public BigDecimal getBillAmount() {
        return billAmount;
    }

    public String getBillDay() {
        return billDay;
    }

    @Transient
    public Integer getBillSize() {
        return billSize;
    }

    public String getCardholder() {
        return cardholder;
    }

    public BigDecimal getCash() {
        return cash;
    }

    /**
     * @return completeNumbers
     */
    @Column(name = "numbers")
    public String getCompleteNumbers() {
        return completeNumbers;
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

    public String getCustomerNo() {
        return customerNo;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getDueDay() {
        return dueDay;
    }

    public String getEmail() {
        return email;
    }

    public Date getEndDate() {
        return endDate;
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * @return integral
     */

    public BigDecimal getIntegral() {
        return integral;
    }

    /**
     * @return minimum
     */

    public BigDecimal getMinimum() {
        return minimum;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getNotAccounted() {
        return notAccounted;
    }

    @Column(name = "complete_numbers")
    public String getNumbers() {
        return numbers;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getPrepaidCashAmount() {
        return prepaidCashAmount;
    }

    public BigDecimal getRepayment() {
        return repayment;
    }

    /**
     * @return source
     */

    public Integer getSource() {
        return source;
    }

    /**
     * @return userCardRelation
     */
    @Transient
    public CreditUserCardRelation getUserCardRelation() {
        return userCardRelation;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    public void setBank(CreditBank bank) {
        this.bank = bank;
    }

    // @OneToMany(mappedBy = "bill", cascade = { CascadeType.REFRESH }, fetch =
    // FetchType.EAGER, orphanRemoval = false)
    // public List<CreditRepayment> getRepaymentList() {
    // return repaymentList;
    // }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public void setBillAmount(BigDecimal billAmount) {
        this.billAmount = billAmount;
    }

    // @OneToMany(mappedBy = "bill", cascade = { CascadeType.REFRESH }, fetch =
    // FetchType.EAGER, orphanRemoval = false)
    // public List<CreditRepayment> getRepaymentList() {
    // return repaymentList;
    // }

    public void setBillDay(String billDay) {
        this.billDay = billDay;
    }

    public void setBillSize(Integer billSize) {
        this.billSize = billSize;
    }

    public void setCardholder(String cardholder) {
        this.cardholder = cardholder;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    /**
     * @param completeNumbers
     *            the completeNumbers to set
     */

    public void setCompleteNumbers(String completeNumbers) {
        this.completeNumbers = completeNumbers;
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

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setDueDay(String dueDay) {
        this.dueDay = dueDay;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @param integral
     *            the integral to set
     */

    public void setIntegral(BigDecimal integral) {
        this.integral = integral;
    }

    /**
     * @param minimum
     *            the minimum to set
     */

    public void setMinimum(BigDecimal minimum) {
        this.minimum = minimum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNotAccounted(BigDecimal notAccounted) {
        this.notAccounted = notAccounted;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setPrepaidCashAmount(String prepaidCashAmount) {
        this.prepaidCashAmount = prepaidCashAmount;
    }

    public void setRepayment(BigDecimal repayment) {
        this.repayment = repayment;
    }

    /**
     * @param source
     *            the source to set
     */

    public void setSource(Integer source) {
        this.source = source;
    }

    // public void setRepaymentList(List<CreditRepayment> repaymentList) {
    // this.repaymentList = repaymentList;
    // }

    /**
     * @param userCardRelation
     *            the userCardRelation to set
     */

    public void setUserCardRelation(CreditUserCardRelation userCardRelation) {
        this.userCardRelation = userCardRelation;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CreditCard [customerNo=" + customerNo + ", phoneNo=" + phoneNo + ", bank=" + bank + ", dueDate=" + dueDate + ", dueDay="
                + dueDay + ", billDay=" + billDay + ", billAmount=" + billAmount + ", minimum=" + minimum + ", integral=" + integral
                + ", repayment=" + repayment + ", cardholder=" + cardholder + ", cash=" + cash + ", consumption=" + consumption
                + ", credits=" + credits + ", name=" + name + ", numbers=" + numbers + ", prepaidCashAmount=" + prepaidCashAmount
                + ", billSize=" + billSize + ", notAccounted=" + notAccounted + ", email=" + email + ", source=" + source
                + ", userCardRelation=" + userCardRelation + ", beginDate=" + beginDate + ", endDate=" + endDate + "]";
    }

}
