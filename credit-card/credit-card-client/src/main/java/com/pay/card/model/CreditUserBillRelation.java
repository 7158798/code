package com.pay.card.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName: CreditUserBillRelation
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年11月30日
 */
@Table(name = "credit_user_bill_relation")
@Entity
public class CreditUserBillRelation implements Serializable {

    private static final long serialVersionUID = -603093795225058773L;

    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 账单id
     */
    private Long billId;
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
     * 新获取到的账单的标识 0 不是 1 是
     */
    private Integer newStatus;

    /**
     * @return billId
     */

    public Long getBillId() {
        return billId;
    }

    public Date getCreateDate() {
        return createDate;
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

    /**
     * @return newStatus
     */

    public Integer getNewStatus() {
        return newStatus;
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
     * @param billId
     *            the billId to set
     */

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @param id
     *            the id to set
     */

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param newStatus
     *            the newStatus to set
     */

    public void setNewStatus(Integer newStatus) {
        this.newStatus = newStatus;
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

}
