package com.pay.aile.bill.model;

import java.io.Serializable;
import java.util.Date;

public class CreditFileModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String fileName;
    private Date sentDate;
    private String subject;
    private String mailType;
    private Long emailId;
    private Integer processResult;
    private Integer status;
    private Date updateDate;
    private Date createDate;
    private String email;
    private boolean isNew;
    private Long userId;

    public Date getCreateDate() {
        return createDate;
    }

    public String getEmail() {
        return email;
    }

    public Long getEmailId() {
        return emailId;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getId() {
        return id;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public String getMailType() {
        return mailType;
    }

    public Integer getProcessResult() {
        return processResult;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public Integer getStatus() {
        return status;
    }

    public String getSubject() {
        return subject;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmailId(Long emailId) {
        this.emailId = emailId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public void setMailType(String mailType) {
        this.mailType = mailType;
    }

    public void setProcessResult(Integer processResult) {
        this.processResult = processResult;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CreditFileModel [id=" + id + ", fileName=" + fileName + ", sentDate=" + sentDate + ", subject="
                + subject + ", mailType=" + mailType + ", emailId=" + emailId + ", processResult=" + processResult
                + ", status=" + status + ", updateDate=" + updateDate + ", createDate=" + createDate + ", email="
                + email + ", isNew=" + isNew + ", userId=" + userId + "]";
    }

}
