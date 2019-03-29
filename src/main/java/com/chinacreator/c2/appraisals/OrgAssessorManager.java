
package com.chinacreator.c2.appraisals;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 机构考核管理员
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.appraisals.OrgAssessorManager", table = "c2_org_assessor_manager", ds = "dev-center")
public class OrgAssessorManager implements Serializable {
	private static final long serialVersionUID = 3027966836097024L;
	/**
	*主键
	*/
	@Column(id = "id_", type = ColumnType.uuid, datatype = "string64")
	private java.lang.String id;

	/**
	*管理员用户id
	*/
	@Column(id = "user_id", datatype = "string64")
	private java.lang.String userId;

	/**
	*管理机构
	*/
	@Column(id = "managed_org_id", datatype = "string64")
	private java.lang.String managedOrgId;

	/**
	*管理员姓名
	*/
	@Column(id = "user_realname", datatype = "string64")
	private java.lang.String userRealname;

	/**
	*管理机构名称
	*/
	@Column(id = "managed_org_name", datatype = "string64")
	private java.lang.String managedOrgName;

	/**
	*管理员账号
	*/
	@Column(id = "user_name", datatype = "string64")
	private java.lang.String userName;

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
	* 设置管理员用户id
	*/
	public void setUserId(java.lang.String userId) {
		this.userId = userId;
	}

	/**
	* 获取管理员用户id
	*/
	public java.lang.String getUserId() {
		return userId;
	}

	/**
	* 设置管理机构
	*/
	public void setManagedOrgId(java.lang.String managedOrgId) {
		this.managedOrgId = managedOrgId;
	}

	/**
	* 获取管理机构
	*/
	public java.lang.String getManagedOrgId() {
		return managedOrgId;
	}

	/**
	* 设置管理员姓名
	*/
	public void setUserRealname(java.lang.String userRealname) {
		this.userRealname = userRealname;
	}

	/**
	* 获取管理员姓名
	*/
	public java.lang.String getUserRealname() {
		return userRealname;
	}

	/**
	* 设置管理机构名称
	*/
	public void setManagedOrgName(java.lang.String managedOrgName) {
		this.managedOrgName = managedOrgName;
	}

	/**
	* 获取管理机构名称
	*/
	public java.lang.String getManagedOrgName() {
		return managedOrgName;
	}

	/**
	* 设置管理员账号
	*/
	public void setUserName(java.lang.String userName) {
		this.userName = userName;
	}

	/**
	* 获取管理员账号
	*/
	public java.lang.String getUserName() {
		return userName;
	}
}
