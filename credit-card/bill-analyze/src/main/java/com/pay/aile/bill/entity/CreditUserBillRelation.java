
/**
* @Title: CreditUserBillRelation.java
* @Package com.pay.aile.bill.entity
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年11月30日
* @version V1.0
*/

package com.pay.aile.bill.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * @ClassName: CreditUserBillRelation
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年11月30日
 *
 */
@TableName("credit_user_bill_relation")
public class CreditUserBillRelation extends Model<CreditUserBillRelation> {

    private static final long serialVersionUID = -8181256540980622383L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("bill_id")
    private Long billId;

    /**
     * @return billId
     */

    public Long getBillId() {
        return billId;
    }

    /**
     * @return id
     */

    public Long getId() {
        return id;
    }

    /**
     * @return userId
     */

    public Long getUserId() {
        return userId;
    }

    /**
     * @param billId
     *            the billId to set
     */

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    /**
     * @param id
     *            the id to set
     */

    public void setId(Long id) {
        this.id = id;
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
