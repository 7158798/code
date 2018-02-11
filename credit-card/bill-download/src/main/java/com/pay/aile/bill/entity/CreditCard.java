package com.pay.aile.bill.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;

@TableName("credit_card")
public class CreditCard extends Model<CreditCard> {

    /**
     * @Fields
     */

    private static final long serialVersionUID = -8413068283560221578L;

    @TableField(exist = false)
    private String bankName;
    @TableField(exist = false)
    private String bankCode;
    /**
     * 所属银行
     */
    @TableField("bank_id")
    private Long bankId;

    /**
     * 账单日
     */
    @TableField("bill_day")
    private String billDay;

    /**
     * 持卡人
     */

    @TableField("cardholder")
    private String cardholder;

    /**
     * 取现额度
     */
    private BigDecimal cash;

    /**
     * 创建时间
     */
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;
    /**
     * 信用额度
     */
    private BigDecimal credits;
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 卡号
     */
    @TableField("complete_numbers")
    private String completeNumbers;

    /**
     * 卡号
     */
    private String numbers;

    /**
     * 预借现金额度
     */
    @TableField("prepaid_cash_amount")
    private String prepaidCashAmount;

    /**
     * 账单金额
     */
    @TableField("bill_amount")
    private BigDecimal billAmount;
    /**
     * 到期还款日期
     */
    @TableField("due_date")
    private Date dueDate;

    /**
     * 到期还款日
     */
    @TableField("due_day")
    private String dueDay;

    /**
     * 有效标志
     *
     * 1:显示0:不显示-1:删除
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer status;

    /**
     * 修改时间
     */
    @TableField(value = "update_date", fill = FieldFill.UPDATE)
    private Date updateDate;

    /**
     * 累计积分余额
     */
    private BigDecimal integral;

    @TableField("email")
    private String email;
    @TableField("begin_date")
    private Date beginDate;
    @TableField("end_date")
    private Date endDate;
    /**
     * 最低还款额
     */
    private BigDecimal minimum;

    @TableField(value = "user_id")
    private Long userId;

    public String getBankCode() {
        return bankCode;
    }

    public Long getBankId() {
        return bankId;
    }

    public String getBankName() {
        return bankName;
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

    public String getCardholder() {
        return cardholder;
    }

    public BigDecimal getCash() {
        return cash;
    }

    /**
     * @return completeNumbers
     */

    public String getCompleteNumbers() {
        return completeNumbers;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public BigDecimal getCredits() {
        return credits;
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

    public Long getId() {
        return id;
    }

    public BigDecimal getIntegral() {
        return integral;
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    public String getName() {
        return name;
    }

    public String getNumbers() {
        return numbers;
    }

    public String getPrepaidCashAmount() {
        return prepaidCashAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @return userId
     */

    public Long getUserId() {
        return userId;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public void setBillAmount(BigDecimal billAmount) {
        this.billAmount = billAmount;
    }

    public void setBillDay(String billDay) {
        this.billDay = billDay;
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

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setCredits(BigDecimal credits) {
        this.credits = credits;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setIntegral(BigDecimal integral) {
        this.integral = integral;
    }

    public void setMinimum(BigDecimal minimum) {
        this.minimum = minimum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public void setPrepaidCashAmount(String prepaidCashAmount) {
        this.prepaidCashAmount = prepaidCashAmount;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * @param userId
     *            the userId to set
     */

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CreditCard [bankName=" + bankName + ", bankCode=" + bankCode + ", bankId=" + bankId + ", billDay="
                + billDay + ", cardholder=" + cardholder + ", cash=" + cash + ", createDate=" + createDate
                + ", credits=" + credits + ", id=" + id + ", name=" + name + ", numbers=" + numbers
                + ", prepaidCashAmount=" + prepaidCashAmount + ", billAmount=" + billAmount + ", dueDate=" + dueDate
                + ", dueDay=" + dueDay + ", status=" + status + ", updateDate=" + updateDate + ", integral=" + integral
                + ", email=" + email + ", minimum=" + minimum + "]";
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }

}
