package com.pay.aile.bill.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * <p>
 * 邮箱
 * </p>
 *
 * @author yaoqiang.sun
 * @since 2017-11-02
 */
@TableName("credit_native_email")
public class CreditNativeEmail extends Model<CreditNativeEmail> {
    public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getCookieMap() {
		return cookieMap;
	}

	public void setCookieMap(Map<String, String> cookieMap) {
		this.cookieMap = cookieMap;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8974426672576428737L;

	/**
     * 创建时间
     */
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;

    /**
     * 邮箱
     */
    private String email;
    @TableField(exist = false)
    private String emailFileId;
    
    public String getEmailFileId() {
		return emailFileId;
	}

	public void setEmailFileId(String emailFileId) {
		this.emailFileId = emailFileId;
	}

	@TableId(value = "id", type = IdType.AUTO)
    private Long id;
    

    private Long userId;
    
   

	/**
     * 密码
     */
    private String password;
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
    //邮件的url
    @TableField(exist = false)
    private String url;
    //cookie
    @TableField(exist = false)
    private Map<String,String> cookieMap;
    
    @TableField(exist = false)
    private Map<String,String> postDataMap;
    
    public Map<String, String> getPostDataMap() {
		return postDataMap;
	}

	public void setPostDataMap(Map<String, String> postDataMap) {
		this.postDataMap = postDataMap;
	}

	public CreditNativeEmail() {

    }

    public CreditNativeEmail(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getEmail() {
        return email;
    }

 

    public Long getId() {
        return id;
    }


    public String getPassword() {
        return password;
    }


    public Integer getStatus() {
        return status;
    }

    public Date getUpdateDate() {
        return updateDate;
    }  

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

   

    public void setEmail(String email) {
        this.email = email;
    }

    
 

    public void setId(Long id) {
        this.id = id;
    }

 

    public void setPassword(String password) {
        this.password = password;
    }

   

    

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

   

    @Override
    public String toString() {
        return "CreditEmail [createDate=" + createDate + ", email=" + email + ", id=" + id + ", password=" + password
                + ", status=" + status + ", updateDate=" + updateDate+ "]";
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }
    
    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
