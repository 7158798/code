package com.pay.card.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Table(name = "credit_login_log")
@Entity
@DynamicInsert
@DynamicUpdate
public class CreditLoginLog implements Serializable {

    private static final long serialVersionUID = -2109355977126165374L;

    private Long id;
    private Date createDate;
    private Long userId;
    private String customerNo;
    private String email;
    private String domain;
    private String reason;

    @Column(name = "create_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreateDate() {
        return createDate;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public String getDomain() {
        return domain;
    }

    public String getEmail() {
        return email;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public Long getUserId() {
        return userId;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate == null ? new Date() : createDate;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
