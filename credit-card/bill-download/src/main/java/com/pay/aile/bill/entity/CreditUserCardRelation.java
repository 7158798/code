
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

    /**
     * 创建时间
     */
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;

    /**
     * 修改时间
     */
    @TableField(value = "update_date", fill = FieldFill.UPDATE)
    private Date updateDate;

    /**
     * 有效标志
     *
     * 1:显示0:不显示-1:删除
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer status;

    /**
     * @return cardId
     */

    public Long getCardId() {
        return cardId;
    }

    /**
     * @return createDate
     */

    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @return id
     */

    public Long getId() {
        return id;
    }

    /**
     * @return status
     */

    public Integer getStatus() {
        return status;
    }

    /**
     * @return updateDate
     */

    public Date getUpdateDate() {
        return updateDate;
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

    /**
     * @param createDate
     *            the createDate to set
     */

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

    /**
     * @param status
     *            the status to set
     */

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @param updateDate
     *            the updateDate to set
     */

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
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
        // TODO Auto-generated method stub
        return id;
    }

}
