package com.pay.aile.bill.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * <p>
 * 用户-错误日志关系表
 * </p>
 *
 * @author yaoqiang.sun
 * @since 2017-11-30
 */
@TableName("credit_user_error_log_relation")
public class CreditUserErrorLogRelation extends Model<CreditUserErrorLogRelation> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("error_log_id")
    private Long errorLogId;
    /**
     * 有效标志1有效0无效
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer status;
    /**
     * 修改时间
     */
    @TableField(value = "update_date", fill = FieldFill.UPDATE)
    private Date updateDate;
    /**
     * 创建时间
     */
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;

    public Date getCreateDate() {
        return createDate;
    }

    public Long getErrorLogId() {
        return errorLogId;
    }

    public Long getId() {
        return id;
    }

    public Integer getStatus() {
        return status;
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

    public void setErrorLogId(Long errorLogId) {
        this.errorLogId = errorLogId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CreditUserErrorLogRelation [id=" + id + ", userId=" + userId + ", errorLogId=" + errorLogId
                + ", status=" + status + ", updateDate=" + updateDate + ", createDate=" + createDate + "]";
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
