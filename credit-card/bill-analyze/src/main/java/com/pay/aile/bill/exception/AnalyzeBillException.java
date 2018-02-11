package com.pay.aile.bill.exception;

/***
 * AnalyzeBillException.java
 *
 * @author shinelon
 *
 * @date 2017年10月30日
 *
 */
public class AnalyzeBillException extends Exception {

    private static final long serialVersionUID = 1980019507208526746L;

    private Integer errorCode;

    private String cardCode;

    public AnalyzeBillException() {
        super();
    }

    public AnalyzeBillException(String errormsg) {
        super(errormsg);

    }

    public AnalyzeBillException(String errormsg, Integer errorCode) {
        super(errormsg);
        this.errorCode = errorCode;
    }

    public AnalyzeBillException(String cardCode, Throwable cause) {
        super(cause);
        this.cardCode = cardCode;
    }

    /**
     * AnalyzeBillException
     *
     * @param prompt
     * @param cause
     *            抛出原因
     * @param errorCode
     *            错误类型码
     */
    public AnalyzeBillException(String prompt, Throwable cause, Integer errorCode) {
        super(prompt, cause);
        this.errorCode = errorCode;
    }

    public String getCardCode() {
        return cardCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getPrompt() {
        return getMessage();
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

}
