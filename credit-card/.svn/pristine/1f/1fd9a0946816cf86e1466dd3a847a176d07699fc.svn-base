package com.pay.card.model.base;

import java.util.Date;

import javax.persistence.Id;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 表结构一样的基类
 *
 * @author qiaohui
 *
 */
public abstract class BaseModel<T extends Number> implements HasCreateTime, HasStatus, HasUuid<T> {

    @Id
    private T id;

    private Date createTime;

    private int status;

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public T getId() {
        return id;
    }

    @Override
    public int getStatus() {
        return status;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setId(T id) {
        this.id = id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
