package com.pay.card.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "credit_user_email_relation")
@Entity
public class CreditUserEmailRelation implements Serializable {

    private static final long serialVersionUID = -4300245670464824725L;

    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 邮箱id
     */
    private Long emailId;
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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
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

    public Integer getStatus() {
        return status;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public Long getUserId() {
        return userId;
    }

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

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
