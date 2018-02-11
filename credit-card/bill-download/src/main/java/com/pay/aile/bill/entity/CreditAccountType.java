package com.pay.aile.bill.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;

/**
 * <p>
 * 帐户类型
 * </p>
 *
 * @author yaoqiang.sun
 * @since 2017-11-02
 */
@TableName("credit_account_type")
public class CreditAccountType extends Model<CreditAccountType> {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 标识符
     */
    private String Identifier;
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

    public Date getCreateDate() {
        return createDate;
    }

    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return Identifier;
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

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIdentifier(String Identifier) {
        this.Identifier = Identifier;
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
        return "CreditAccountType{" + "id=" + id + ", name=" + name + ", Identifier=" + Identifier + ", status="
                + status + ", updateDate=" + updateDate + ", createDate=" + createDate + "}";
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
