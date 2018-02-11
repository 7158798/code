/**
 * @Title: CreditUserFileRelation.java
 * @Package com.pay.aile.bill.entity
 * @Description: TODO(用一句话描述该文件做什么)
 * @author jing.jin
 * @date 2017年11月30日
 * @version V1.0
 */

package com.pay.card.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName: CreditUserFileRelation
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年11月30日
 */
@Table(name = "credit_user_file_relation")
@Entity
public class CreditUserFileRelation implements Serializable {

    private static final long serialVersionUID = 4726735174330040172L;

    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户邮件id
     */
    private Long fileId;

    // /**
    // * 创建时间
    // */
    // private Date createDate;
    // /**
    // * 更新时间
    // */
    // private Date updateDate;
    // /**
    // * 状态
    // */
    // private Integer status;

    // public Date getCreateDate() {
    // return createDate;
    // }

    /**
     * @return fileId
     */

    public Long getFileId() {
        return fileId;
    }

    /**
     * @return id
     */

    @Id
    public Long getId() {
        return id;
    }

    // public Integer getStatus() {
    // return status;
    // }
    //
    // public Date getUpdateDate() {
    // return updateDate;
    // }

    /**
     * @return userId
     */

    public Long getUserId() {
        return userId;
    }

    // public void setCreateDate(Date createDate) {
    // this.createDate = createDate;
    // }

    /**
     * @param fileId
     *            the fileId to set
     */

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    /**
     * @param id
     *            the id to set
     */

    public void setId(Long id) {
        this.id = id;
    }

    // public void setStatus(Integer status) {
    // this.status = status;
    // }
    //
    // public void setUpdateDate(Date updateDate) {
    // this.updateDate = updateDate;
    // }

    /**
     * @param userId
     *            the userId to set
     */

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
