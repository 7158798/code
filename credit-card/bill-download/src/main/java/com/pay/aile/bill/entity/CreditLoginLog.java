
/**
* @Title: CreditLoginLog.java
* @Package com.pay.aile.bill.entity
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年12月12日
* @version V1.0
*/

package com.pay.aile.bill.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;

/**
 * @ClassName: CreditLoginLog
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月12日
 *
 */
@TableName("credit_login_log")
public class CreditLoginLog extends Model<CreditTemplate> {

    /**
     * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
     */

    private static final long serialVersionUID = 9013851024302629362L;

    private Long id;
    @TableField("user_id")
    private Long userId;

    private String email;

    private String domain;

    private String reason;
    /**
     * 创建时间
     */
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;

    /**
     * @return createDate
     */

    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @return domain
     */

    public String getDomain() {
        return domain;
    }

    /**
     * @return email
     */

    public String getEmail() {
        return email;
    }

    /**
     * @return id
     */

    public Long getId() {
        return id;
    }

    /**
     * @return reason
     */

    public String getReason() {
        return reason;
    }

    /**
     * @return userId
     */

    public Long getUserId() {
        return userId;
    }

    /**
     * @param createDate
     *            the createDate to set
     */

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @param domain
     *            the domain to set
     */

    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * @param email
     *            the email to set
     */

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @param id
     *            the id to set
     */

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param reason
     *            the reason to set
     */

    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @param userId
     *            the userId to set
     */

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }

}
