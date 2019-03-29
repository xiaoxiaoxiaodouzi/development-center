package com.chinacreator.c2.appraisals;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/** 
 * 考核白名单
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.appraisals.AppraisalWitelist", table = "c2_appraisals_whitelist", ds = "")
public class AppraisalWitelist implements Serializable {
	private static final long serialVersionUID = 3032614114000896L;
	/** 
	* 主键
	*/
	@Column(id = "id", type = ColumnType.uuid, datatype = "string64")
	private java.lang.String id;
	/** 
	* 用户id
	*/
	@Column(id = "user_id", datatype = "string64")
	private java.lang.String userId;
	/** 
	* 用户姓名
	*/
	@Column(id = "user_realname", datatype = "string64")
	private java.lang.String userRealname;

	/** 
	* 设置主键
	*/
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/** 
	* 获取主键
	*/
	public java.lang.String getId() {
		return id;
	}

	/** 
	* 设置用户id
	*/
	public void setUserId(java.lang.String userId) {
		this.userId = userId;
	}

	/** 
	* 获取用户id
	*/
	public java.lang.String getUserId() {
		return userId;
	}

	/** 
	* 设置用户姓名
	*/
	public void setUserRealname(java.lang.String userRealname) {
		this.userRealname = userRealname;
	}

	/** 
	* 获取用户姓名
	*/
	public java.lang.String getUserRealname() {
		return userRealname;
	}
}
