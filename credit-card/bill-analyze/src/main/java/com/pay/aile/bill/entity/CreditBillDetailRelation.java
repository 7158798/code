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
 *
 * @author yaoqiang.sun
 * @since 2017-11-02
 */
@TableName("credit_bill_detail_relation")
public class CreditBillDetailRelation extends Model<CreditBillDetailRelation> {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 账单ID
     */
    @TableField("bill_id")
    private Long billId;
    /**
     * 账单明细ID
     */
    @TableField("bill_detail_id")
    private Long billDetailId;

    /**
     * 创建时间
     */
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;

    @TableField(value = "update_date", fill = FieldFill.UPDATE)
    private Date updateDate;

    /**
     * 有效标志1有效0无效
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer status;

    public Long getBillDetailId() {
        return billDetailId;
    }

    public Long getBillId() {
        return billId;
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

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setBillDetailId(Long billDetailId) {
        this.billDetailId = billDetailId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
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

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "CreditBillDetailRelation [id=" + id + ", billId=" + billId + ", billDetailId=" + billDetailId
                + ", createDate=" + createDate + ", updateDate=" + updateDate + ", status=" + status + "]";
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }

}
