package com.pay.aile.bill.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.FieldStrategy;

@TableName("credit_card")
public class CreditCard extends Model<CreditCard> {

    /**
     * @Fields
     */

    private static final long serialVersionUID = -8413068283560221578L;

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
    private String numbers;

    /**
     * 预借现金额度
     */
    @TableField("prepaid_cash_amount")
    private String prepaidCashAmount;

    /**
     * 账单金额
     */
    @TableField(value = "bill_amount", strategy = FieldStrategy.NOT_NULL)
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
     * 2:new
     *
     * 1:有效
     *
     * 0:无效
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

    /**
     * 完整卡号
     */
    private String completeNumbers;

    private Long userId;

    private Integer creditsType;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CreditCard)) {
            return false;
        }
        CreditCard c = (CreditCard) obj;
        if (!c.getNumbers().equals(getNumbers())) {
            return false;
        }
        return true;
    }

    public Long getBankId() {
        return bankId;
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

    public String getCompleteNumbers() {
        return completeNumbers;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public Integer getCreditsType() {
        return creditsType;
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

    public Long getUserId() {
        return userId;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(getNumbers());
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
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

    public void setCompleteNumbers(String completeNumbers) {
        this.completeNumbers = completeNumbers;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setCredits(BigDecimal credits) {
        this.credits = credits;
    }

    public void setCreditsType(Integer creditsType) {
        this.creditsType = creditsType;
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

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CreditCard [bankId=" + bankId + ", billDay=" + billDay + ", cardholder=" + cardholder + ", cash=" + cash
                + ", createDate=" + createDate + ", credits=" + credits + ", id=" + id + ", name=" + name + ", numbers="
                + numbers + ", prepaidCashAmount=" + prepaidCashAmount + ", billAmount=" + billAmount + ", dueDate="
                + dueDate + ", dueDay=" + dueDay + ", status=" + status + ", updateDate=" + updateDate + ", integral="
                + integral + ", email=" + email + ", beginDate=" + beginDate + ", endDate=" + endDate + ", minimum="
                + minimum + ", completeNumbers=" + completeNumbers + ", userId=" + userId + "]";
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }

}
