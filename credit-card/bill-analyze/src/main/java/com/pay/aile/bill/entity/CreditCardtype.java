package com.pay.aile.bill.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;

/**
 * <p>
 * 卡分类
 * </p>
 *
 * @author yaoqiang.sun
 * @since 2017-11-02
 */
@TableName("credit_cardtype")
public class CreditCardtype extends Model<CreditCardtype> {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 银行id
     */
    @TableField("bank_id")
    private Long bankId;
    /**
     * 名称
     */
    private String name;
    /**
     * 编码
     */
    private String code;
    /**
     * 有效标志1有效0无效
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer status;
    /**
     * 修改时间
     */
    @TableField(value = "update_date", fill = FieldFill.UPDATE)
    private Date updateDate;
    /**
     * 创建时间
     */
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;

    public Long getBankId() {
        return bankId;
    }

    public String getCode() {
        return code;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getStatus() {
        return status;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "CreditCardtype{" + "id=" + id + ", bankId=" + bankId + ", name=" + name + ", code=" + code + ", status="
                + status + ", updateDate=" + updateDate + ", createDate=" + createDate + "}";
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
