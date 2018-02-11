package com.pay.aile.bill.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;

@TableName("credit_user_email_relation")
public class CreditUserEmailRelation extends Model<CreditUserEmailRelation> {

    private static final long serialVersionUID = -2581661263839893105L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("email_id")
    private Long emailId;

    /**
     * 创建时间
     */
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;

    /**
     * 修改时间
     */
    @TableField(value = "update_date", fill = FieldFill.UPDATE)
    private Date updateDate;

    /**
     * 有效标志
     *
     * 1:显示0:不显示-1:删除
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer status;

    /**
     * 是否支持ssl 0 不 支持1 支持
     */
    private Integer ssl;

    /**
     * 邮箱服务器
     */
    private String server;

    /**
     * 邮箱服务器端口
     */
    private Integer port;

    /**
     * @return createDate
     */

    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @return emailId
     */

    public Long getEmailId() {
        return emailId;
    }

    /**
     * @return id
     */

    public Long getId() {
        return id;
    }

    /**
     * @return port
     */

    public Integer getPort() {
        return port;
    }

    /**
     * @return server
     */

    public String getServer() {
        return server;
    }

    /**
     * @return ssl
     */

    public Integer getSsl() {
        return ssl;
    }

    /**
     * @return status
     */

    public Integer getStatus() {
        return status;
    }

    /**
     * @return updateDate
     */

    public Date getUpdateDate() {
        return updateDate;
    }

    public Long getUserId() {
        return userId;
    }

    /**
     * @param createDate
     *            the createDate to set
     */

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @param emailId
     *            the emailId to set
     */

    public void setEmailId(Long emailId) {
        this.emailId = emailId;
    }

    /**
     * @param id
     *            the id to set
     */

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param port
     *            the port to set
     */

    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @param server
     *            the server to set
     */

    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @param ssl
     *            the ssl to set
     */

    public void setSsl(Integer ssl) {
        this.ssl = ssl;
    }

    /**
     * @param status
     *            the status to set
     */

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @param updateDate
     *            the updateDate to set
     */

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CreditUserEmailRelation [id=" + id + ", userId=" + userId + ", emailId=" + emailId + ", ssl=" + ssl
                + ", server=" + server + ", port=" + port + "]";
    }

    @Override
    protected Serializable pkVal() {
        // TODO Auto-generated method stub
        return id;
    }

}
