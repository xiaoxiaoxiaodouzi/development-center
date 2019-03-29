package com.chinacreator.c2.workbench;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 助理
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.workbench.Assistant", table = "c2_assistant", ds = "dev-center")
public class Assistant implements Serializable {
	private static final long serialVersionUID = 1756893133029376L;
	/**
	 *
	 */
	@Column(id = "user_name", type = ColumnType.uuid, datatype = "char22")
	private java.lang.String userName;

	/**
	 *
	 */
	@Column(id = "user_realname", datatype = "string64")
	private java.lang.String userRealname;

	/**
	 *
	 */
	@Column(id = "org_id", datatype = "string64")
	private java.lang.String orgId;

	/**
	 *
	 */
	@Column(id = "org_name", datatype = "string64")
	private java.lang.String orgName;

	/**
	 * 设置
	 */
	public void setUserName(java.lang.String userName) {
		this.userName = userName;
	}

	/**
	 * 获取
	 */
	public java.lang.String getUserName() {
		return userName;
	}

	/**
	 * 设置
	 */
	public void setUserRealname(java.lang.String userRealname) {
		this.userRealname = userRealname;
	}

	/**
	 * 获取
	 */
	public java.lang.String getUserRealname() {
		return userRealname;
	}

	/**
	 * 设置
	 */
	public void setOrgId(java.lang.String orgId) {
		this.orgId = orgId;
	}

	/**
	 * 获取
	 */
	public java.lang.String getOrgId() {
		return orgId;
	}

	/**
	 * 设置
	 */
	public void setOrgName(java.lang.String orgName) {
		this.orgName = orgName;
	}

	/**
	 * 获取
	 */
	public java.lang.String getOrgName() {
		return orgName;
	}
}
