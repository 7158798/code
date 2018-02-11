package com.pay.aile.bill.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * <p>
 * 邮箱
 * </p>
 *
 * @author yaoqiang.sun
 * @since 2017-11-02
 */
@TableName("credit_email")
public class CreditEmail extends Model<CreditEmail> {

    private static final long serialVersionUID = 1L;

    /**
     * 创建时间
     */
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;
    /**
     * 商户编号
     */
    private String customerNo;
    /**
     * 邮箱
     */
    private String email;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 密码
     */
    private String password;
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
    /***
     * 密码是否加密，
     *
     * 数据库中密码统一加密，网页录入的密码不加密
     */
    @TableField(exist = false)
    private boolean encrypt = false;
    @TableField(exist = false)
    private long lastJobTimestamp;
    @TableField(exist = false)
    private long lastJobExecutionTime;
    @TableField(exist = false)
    private String Remarks;

    public CreditEmail() {

    }

    public CreditEmail(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public long getLastJobExecutionTime() {
        return lastJobExecutionTime;
    }

    public long getLastJobTimestamp() {
        return lastJobTimestamp;
    }

    public String getPassword() {
        return password;
    }

    public String getRemarks() {
        return Remarks;
    }

    public Integer getStatus() {
        return status;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLastJobExecutionTime(long lastJobExecutionTime) {
        this.lastJobExecutionTime = lastJobExecutionTime;
    }

    public void setLastJobTimestamp(long lastJobTimestamp) {
        this.lastJobTimestamp = lastJobTimestamp;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "CreditEmail{" + "id=" + id + ", customerNo=" + customerNo + ", email=" + email + ", password="
                + password + ", status=" + status + ", updateDate=" + updateDate + ", createDate=" + createDate + "}";
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
