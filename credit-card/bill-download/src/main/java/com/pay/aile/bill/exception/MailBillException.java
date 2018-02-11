package com.pay.aile.bill.exception;

/***
 * MailBillException.java
 *
 * @author shinelon
 *
 * @date 2017年10月30日
 *
 */
public class MailBillException extends Exception {

    private static final long serialVersionUID = 1980019507208526746L;

    private Integer errorCode;

    public MailBillException() {
        super();
    }

    public MailBillException(String errormsg) {
        super(errormsg);

    }

    public MailBillException(String errormsg, Integer errorCode) {
        super(errormsg);
        this.errorCode = errorCode;
    }

    public MailBillException(String prompt, Throwable cause) {
        super(prompt, cause);
    }

    /**
     * MailBillException
     *
     * @param prompt
     * @param cause
     *            抛出原因
     * @param errorCode
     *            错误类型码
     */
    public MailBillException(String prompt, Throwable cause, Integer errorCode) {
        super(prompt, cause);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getPrompt() {
        return getMessage();
    }

}
