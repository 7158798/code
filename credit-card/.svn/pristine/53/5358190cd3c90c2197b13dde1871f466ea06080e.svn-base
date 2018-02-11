package com.pay.card.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Table(name = "credit_set")
@Entity
@DynamicInsert
@DynamicUpdate
public class CreditSet extends BaseEntity implements Serializable {

    /**
     * @Fields field:field:{todo}(用一句话描述这个变量表示什么)
     */

    private static final long serialVersionUID = -2109355977126165374L;

    /**
     * 用户
     */
    private CreditUserInfo userInfo;

    /**
     * 是否还款提醒
     */
    // @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    private Integer repaymentReminder = 1;

    /**
     * 提前提醒的天数
     */

    private Integer advanceDay = 5;

    /**
     * 提醒时间
     */

    private Integer times = 10;

    /**
     * 账单日提醒
     */
    // @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    private Integer billdayReminder = 1;

    /**
     * 账单逾期提醒
     */
    // @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    private Integer overdueReminder = 1;

    /**
     * 出账单提醒
     */
    // @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    private Integer outBillReminder = 1;

    public Integer getAdvanceDay() {
        return advanceDay;
    }

    /**
     * @return billdayReminder
     */

    public Integer getBilldayReminder() {
        return billdayReminder;
    }

    /**
     * @return outBillReminder
     */

    public Integer getOutBillReminder() {
        return outBillReminder;
    }

    /**
     * @return overdueReminder
     */

    public Integer getOverdueReminder() {
        return overdueReminder;
    }

    /**
     * @return repaymentReminder
     */

    public Integer getRepaymentReminder() {
        return repaymentReminder;
    }

    public Integer getTimes() {
        return times;
    }

    /**
     * @return userInfo
     */
    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    public CreditUserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * @param advanceDay
     *            the advanceDay to set
     */

    public void setAdvanceDay(Integer advanceDay) {
        this.advanceDay = advanceDay;
    }

    /**
     * @param billdayReminder
     *            the billdayReminder to set
     */

    public void setBilldayReminder(Integer billdayReminder) {
        this.billdayReminder = billdayReminder;
    }

    /**
     * @param outBillReminder
     *            the outBillReminder to set
     */

    public void setOutBillReminder(Integer outBillReminder) {
        this.outBillReminder = outBillReminder;
    }

    /**
     * @param overdueReminder
     *            the overdueReminder to set
     */

    public void setOverdueReminder(Integer overdueReminder) {
        this.overdueReminder = overdueReminder;
    }

    /**
     * @param repaymentReminder
     *            the repaymentReminder to set
     */

    public void setRepaymentReminder(Integer repaymentReminder) {
        this.repaymentReminder = repaymentReminder;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    /**
     * @param userInfo
     *            the userInfo to set
     */

    public void setUserInfo(CreditUserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
