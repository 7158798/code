/**
 * @Title: CreditUserCardRelation.java
 * @Package com.pay.aile.bill.entity
 * @Description: TODO(用一句话描述该文件做什么)
 * @author jing.jin
 * @date 2017年11月30日
 * @version V1.0
 */

package com.pay.card.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName: CreditUserCardRelation
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年11月30日
 */
@Table(name = "credit_user_card_relation")
@Entity
public class CreditUserCardRelation implements Serializable {

    private static final long serialVersionUID = -2683581145067469187L;

    private Long id;

    private Long userId;

    private Long cardId;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 更新时间
     */
    private Date updateDate;
    /**
     * 状态
     */
    private Integer status;

    /**
     * 还款日类型 0 固定 1 账单日后多少天
     */
    private Integer dueType;

    /**
     * 还款日
     */
    private Integer dueDay;

    /**
     * 账单日
     */
    private Integer billDay;

    /**
     * 还款金额
     */
    private BigDecimal repayment;

    /**
     * @return billDay
     */

    public Integer getBillDay() {
        return billDay;
    }

    /**
     * @return cardId
     */

    public Long getCardId() {
        return cardId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @return dueDay
     */

    public Integer getDueDay() {
        return dueDay;
    }

    /**
     * @return dueType
     */

    public Integer getDueType() {
        return dueType;
    }

    /**
     * @return id
     */

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public BigDecimal getRepayment() {
        return repayment;
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

    /**
     * @param billDay
     *            the billDay to set
     */

    public void setBillDay(Integer billDay) {
        this.billDay = billDay;
    }

    /**
     * @param cardId
     *            the cardId to set
     */

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @param dueDay
     *            the dueDay to set
     */

    public void setDueDay(Integer dueDay) {
        this.dueDay = dueDay;
    }

    /**
     * @param dueType
     *            the dueType to set
     */

    public void setDueType(Integer dueType) {
        this.dueType = dueType;
    }

    /**
     * @param id
     *            the id to set
     */

    public void setId(Long id) {
        this.id = id;
    }

    public void setRepayment(BigDecimal repayment) {
        this.repayment = repayment == null ? new BigDecimal(0) : repayment;
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
        return "CreditUserCardRelation [id=" + id + ", userId=" + userId + ", cardId=" + cardId + ", createDate=" + createDate
                + ", updateDate=" + updateDate + ", status=" + status + ", dueType=" + dueType + ", dueDay=" + dueDay + ", billDay="
                + billDay + ", repayment=" + repayment + "]";
    }

}
