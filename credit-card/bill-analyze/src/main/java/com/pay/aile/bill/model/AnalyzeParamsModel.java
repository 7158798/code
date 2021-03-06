package com.pay.aile.bill.model;

import java.util.Date;

/**
 *
 * @author Charlie
 * @description 存放邮件解析过程中需要传递的数据
 */
public class AnalyzeParamsModel implements Cloneable {

    /*
     * (非 Javadoc)
     *
     *
     * @return
     *
     * @throws CloneNotSupportedException
     *
     * @see java.lang.Object#clone()
     */

    private String bankCode;// 银行编码

    private Long bankId;// 银行id

    private String cardCode;// 卡片类型

    private Long cardtypeId;// 卡片类型ID
    private String content;// 邮件内容
    private String originContent;// 未格式化的内容
    private String attachment;// 邮件内容
    private String email;// 用户邮箱
    private Long emailId;// 邮箱id
    private AnalyzeResult result;// 解析之后的规则数据
    private Date sentDate;// 邮件发送日期
    private Long userId;// 用户id
    private Long fileId;// 文件id
    private boolean isNew;
    private Exception error;// 解析过程中的异常
    private String fileName;
    // 邮件主题
    private String subject;

    @Override
    public Object clone() {

        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getAttachment() {
        return attachment;
    }

    public String getBankCode() {
        return bankCode;
    }

    public Long getBankId() {
        return bankId;
    }

    public String getCardCode() {
        return cardCode;
    }

    public Long getCardtypeId() {
        return cardtypeId;
    }

    public String getContent() {
        return content;
    }

    public String getEmail() {
        return email;
    }

    public Long getEmailId() {
        return emailId;
    }

    public Exception getError() {
        return error;
    }

    /**
     * @return fileId
     */

    public Long getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public String getOriginContent() {
        return originContent;
    }

    public AnalyzeResult getResult() {
        return result;
    }

    public Date getSentDate() {
        return sentDate;
    }

    /**
     * @return subject
     */

    public String getSubject() {
        return subject;
    }

    public Long getUserId() {
        return userId;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public void setCardtypeId(Long cardtypeId) {
        this.cardtypeId = cardtypeId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmailId(Long emailId) {
        this.emailId = emailId;
    }

    public void setError(Exception error) {
        this.error = error;
    }

    public void setFail() {
        result = null;
    }

    /**
     * @param fileId
     *            the fileId to set
     */

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public void setOriginContent(String originContent) {
        this.originContent = originContent;
    }

    public void setResult(AnalyzeResult result) {
        this.result = result;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    /**
     * @param subject
     *            the subject to set
     */

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean success() {
        return result != null;
    }

    @Override
    public String toString() {
        return "AnalyzeParamsModel [bankCode=" + bankCode + ", bankId=" + bankId + ", cardCode=" + cardCode
                + ", cardtypeId=" + cardtypeId + ", content=" + content + ", attachment=" + attachment + ", email="
                + email + ", emailId=" + emailId + ", result=" + result + ", sentDate=" + sentDate + ", userId="
                + userId + ", fileId=" + fileId + ", isNew=" + isNew + ", error=" + error + ", fileName=" + fileName
                + "]";
    }

}
