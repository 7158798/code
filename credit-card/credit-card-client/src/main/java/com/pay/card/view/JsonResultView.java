/**
 *
 */
package com.pay.card.view;

import com.pay.card.enums.CodeEnum;

/**
 * @author qiaohui
 *         注意，这个 view 才是所有 api 封装返回的 class
 */
public class JsonResultView<T> {

    /**
     * code
     */
    private String code = CodeEnum.SUCCESS.getCode();
    /**
     * 消息
     */
    private String msg = CodeEnum.SUCCESS.getMsg();

    /**
     * 这里存放返回的主要数据
     */
    private T object;

    /**
     * 这里可以存放需要返回的 token ，可以为空
     */
    private Object payload;

    public JsonResultView() {

    }

    public JsonResultView(CodeEnum codeEnum) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMsg();
    }

    public JsonResultView(String code) {
        this.code = code;

    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getObject() {
        return object;
    }

    public Object getPayload() {
        return payload;
    }

    public JsonResultView<T> setCode(String code) {
        this.code = code;

        return this;
    }

    public JsonResultView<T> setMsg(String msg) {
        this.msg = msg;

        return this;
    }

    public JsonResultView<T> setObject(T object) {
        this.object = object;

        return this;
    }

    public JsonResultView<T> setPayload(Object payload) {
        this.payload = payload;
        return this;
    }

}
