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

    // /**
    // * 商户编号
    // */
    // private String customerNo;
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
    // 是否新导入的邮箱
    @TableField(exist = false)
    private boolean isNew;
    // 是否需要下载附件
    @TableField(exist = false)
    private boolean isDownload = true;
    /**
     * 对应的用户id以,分割
     */
    @TableField(exist = false)
    private String userId;

    @TableField(exist = false)
    private String protocol;// 自定义邮箱协议
    @TableField(exist = false)
    private String port;// 自定义邮箱端口
    @TableField(exist = false)
    private String host;// 自定义邮箱host
    @TableField(exist = false)
    private int enableSsl;// 自定义邮箱是否支持ssl

    public CreditEmail() {

    }

    public CreditEmail(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getEmail() {
        return email;
    }

    public int getEnableSsl() {
        return enableSsl;
    }

    public String getHost() {
        return host;
    }

    public Long getId() {
        return id;
    }

    public boolean getIsNew() {
        return isNew;
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

    public String getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
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

    /**
     * @return userId
     */

    public String getUserId() {
        return userId;
    }

    /**
     * @return isDownload
     */

    public boolean isDownload() {
        return isDownload;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @param isDownload
     *            the isDownload to set
     */

    public void setDownload(boolean isDownload) {
        this.isDownload = isDownload;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEnableSsl(int enableSsl) {
        this.enableSsl = enableSsl;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public void setLastJobExecutionTime(long lastJobExecutionTime) {
        this.lastJobExecutionTime = lastJobExecutionTime;
    }

    public void setLastJobTimestamp(long lastJobTimestamp) {
        this.lastJobTimestamp = lastJobTimestamp;
    }

    /**
     * @param isNew
     *            the isNew to set
     */

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
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

    /**
     * @param userId
     *            the userId to set
     */

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CreditEmail [createDate=" + createDate + ", email=" + email + ", id=" + id + ", password=" + password
                + ", status=" + status + ", updateDate=" + updateDate + ", encrypt=" + encrypt + ", lastJobTimestamp="
                + lastJobTimestamp + ", lastJobExecutionTime=" + lastJobExecutionTime + ", Remarks=" + Remarks
                + ", isNew=" + isNew + ", isDownload=" + isDownload + ", userId=" + userId + ", protocol=" + protocol
                + ", port=" + port + ", host=" + host + ", enableSsl=" + enableSsl + "]";
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
