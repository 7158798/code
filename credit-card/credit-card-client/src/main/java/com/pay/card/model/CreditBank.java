package com.pay.card.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author qiaohui 银行
 */
@Table(name = "credit_bank")
@Entity
public class CreditBank extends BaseEntity implements Serializable {

    /**
     * @Fields field:field:{todo}(用一句话描述这个变量表示什么)
     */

    private static final long serialVersionUID = 5393897579463432731L;

    /**
     * 银行名称
     */
    private String name;

    /**
     * 简称
     */
    private String shortName;
    /**
     * 银行编码
     */
    private String code;
    /**
     * 是否支持网银
     */
    private Integer online;

    /**
     * 是否支持邮件
     */
    private Integer email;
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 还款周期
     */
    private Integer repaymentCycle;

    /**
     * 最长免息期
     */
    private Integer interestFreePeriod;

    public String getCode() {
        return code;
    }

    public Integer getEmail() {
        return email;
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * @return interestFreePeriod
     */

    public Integer getInterestFreePeriod() {
        return interestFreePeriod;
    }

    public String getName() {
        return name;
    }

    public Integer getOnline() {
        return online;
    }

    /**
     * @return repaymentCycle
     */

    public Integer getRepaymentCycle() {
        return repaymentCycle;
    }

    public String getShortName() {
        return shortName;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setEmail(Integer email) {
        this.email = email;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param interestFreePeriod
     *            the interestFreePeriod to set
     */

    public void setInterestFreePeriod(Integer interestFreePeriod) {
        this.interestFreePeriod = interestFreePeriod;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    /**
     * @param repaymentCycle
     *            the repaymentCycle to set
     */

    public void setRepaymentCycle(Integer repaymentCycle) {
        this.repaymentCycle = repaymentCycle;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CreditBank [name=" + name + ", shortName=" + shortName + ", code=" + code + ", online=" + online + ", email=" + email
                + ", userId=" + userId + ", repaymentCycle=" + repaymentCycle + ", interestFreePeriod=" + interestFreePeriod + "]";
    }

}
