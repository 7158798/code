package com.pay.card.Exception;

import com.pay.card.enums.CodeEnum;

public class CreditClientException extends Exception {
    /**
    *
    */
    private static final long serialVersionUID = 6929934268186469897L;
    private String code;
    private String message;
    private CodeEnum codeEnum;

    public CreditClientException(CodeEnum code) {
        super(code.getCode());
        this.code = code.getCode();
        message = code.getMsg();
        codeEnum = code;
    }

    public CreditClientException(String code, Exception cause) {
        super(cause);
        this.code = code;
    }

    public CreditClientException(String code, String exceptionMsg) {
        this.code = code;
        message = exceptionMsg;
    }

    public CreditClientException(String code, String message, Exception cause) {
        super(cause);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    /**
     * @return codeEnum
     */

    public CodeEnum getCodeEnum() {
        return codeEnum;
    }

    @Override
    public String getMessage() {
        return message == null ? super.getMessage() : message + ". " + super.getMessage();
    }

    public String getOriginalMessage() {
        return message;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @param codeEnum
     *            the codeEnum to set
     */

    public void setCodeEnum(CodeEnum codeEnum) {
        this.codeEnum = codeEnum;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return code + "/" + getMessage();
    }
}
