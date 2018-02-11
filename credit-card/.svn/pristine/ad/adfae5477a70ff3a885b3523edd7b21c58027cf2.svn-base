
/**
* @Title: CreditUserCardRelation.java
* @Package com.pay.aile.bill.entity
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年11月30日
* @version V1.0
*/

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
 * @ClassName: CreditUserCardRelation
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年11月30日
 *
 */
@TableName("credit_user_card_relation")
public class CreditUserCardRelation extends Model<CreditUserCardRelation> {

    private static final long serialVersionUID = 8926865922553587318L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("card_id")
    private Long cardId;

    @TableField("status")
    private Integer status;

    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;

    /**
     * @return cardId
     */

    public Long getCardId() {
        return cardId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @return id
     */

    public Long getId() {
        return id;
    }

    public Integer getStatus() {
        return status;
    }

    /**
     * @return userId
     */

    public Long getUserId() {
        return userId;
    }

    /**
     * @param cardId
     *            the cardId to set
     */

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @param id
     *            the id to set
     */

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @param userId
     *            the userId to set
     */

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CreditUserCardRelation [id=" + id + ", userId=" + userId + ", cardId=" + cardId + ", status=" + status
                + ", createDate=" + createDate + "]";
    }

    @Override
    protected Serializable pkVal() {
        // TODO Auto-generated method stub
        return id;
    }

}
