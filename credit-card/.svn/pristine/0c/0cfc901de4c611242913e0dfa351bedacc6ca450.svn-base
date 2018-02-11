package com.pay.aile.bill.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;

/**
 * <p>
 * 银行
 * </p>
 *
 * @author yaoqiang.sun
 * @since 2017-11-02
 */
@TableName("credit_bank")
public class CreditBank extends Model<CreditBank> {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 银行名称
     */
    private String name;
    /**
     * 银行编码
     */
    private String code;
    /**
     * 扩展主题关键字
     */
    @TableField("ext_keyword")
    private String extKeyword;

    /**
     * 是否支持网银
     */
    private Integer online;

    /**
     * 是否支持邮件
     */
    private Integer email;
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

    public String getCode() {
        return code;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Integer getEmail() {
        return email;
    }

    public String getExtKeyword() {
        return extKeyword;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getOnline() {
        return online;
    }

    public Integer getStatus() {
        return status;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setEmail(Integer email) {
        this.email = email;
    }

    public void setExtKeyword(String extKeyword) {
        this.extKeyword = extKeyword;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "CreditBank{" + "id=" + id + ", name=" + name + ", extKeyword=" + extKeyword
                + ", online=" + online + ", email=" + email + ", status=" + status + ", updateDate="
                + updateDate + ", createDate=" + createDate + "}";
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
