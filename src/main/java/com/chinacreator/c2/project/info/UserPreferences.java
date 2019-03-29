package com.chinacreator.c2.project.info;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 用户偏好设置
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.info.UserPreferences", table = "td_sm_userpreferences", ds = "dev-center")
public class UserPreferences implements Serializable {
	private static final long serialVersionUID = 2194565146640384L;
	/**
	 *用户ID
	 */
	@Column(id = "user_id", datatype = "string64")
	private java.lang.String userId;

	/**
	 *信息名称
	 */
	@Column(id = "info_name", datatype = "string512")
	private java.lang.String infoName;

	/**
	 *信息内容
	 */
	@Column(id = "info_content", datatype = "string2000")
	private java.lang.String infoContent;

	/**
	 *信息描述
	 */
	@Column(id = "info_desc", datatype = "string512")
	private java.lang.String infoDesc;

	/**
	 *最后修改时间
	 */
	@Column(id = "info_lastupdate", datatype = "timestamp")
	private java.sql.Timestamp infoLastupdate;

	/**
	 *
	 */
	@Column(id = "id", type = ColumnType.uuid, datatype = "int")
	private java.lang.Integer id;

	/**
	 * 设置用户ID
	 */
	public void setUserId(java.lang.String userId) {
		this.userId = userId;
	}

	/**
	 * 获取用户ID
	 */
	public java.lang.String getUserId() {
		return userId;
	}

	/**
	 * 设置信息名称
	 */
	public void setInfoName(java.lang.String infoName) {
		this.infoName = infoName;
	}

	/**
	 * 获取信息名称
	 */
	public java.lang.String getInfoName() {
		return infoName;
	}

	/**
	 * 设置信息内容
	 */
	public void setInfoContent(java.lang.String infoContent) {
		this.infoContent = infoContent;
	}

	/**
	 * 获取信息内容
	 */
	public java.lang.String getInfoContent() {
		return infoContent;
	}

	/**
	 * 设置信息描述
	 */
	public void setInfoDesc(java.lang.String infoDesc) {
		this.infoDesc = infoDesc;
	}

	/**
	 * 获取信息描述
	 */
	public java.lang.String getInfoDesc() {
		return infoDesc;
	}

	/**
	 * 设置最后修改时间
	 */
	public void setInfoLastupdate(java.sql.Timestamp infoLastupdate) {
		this.infoLastupdate = infoLastupdate;
	}

	/**
	 * 获取最后修改时间
	 */
	public java.sql.Timestamp getInfoLastupdate() {
		return infoLastupdate;
	}

	/**
	 * 设置
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取
	 */
	public java.lang.Integer getId() {
		return id;
	}
}
