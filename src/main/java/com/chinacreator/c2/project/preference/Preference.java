package com.chinacreator.c2.project.preference;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 项目偏好
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.preference.Preference", table = "c2_comunity_project_preference", ds = "dev-center")
public class Preference implements Serializable {
	private static final long serialVersionUID = 1292793733349376L;
	/**
	 *主键
	 */
	@Column(id = "prefer_id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer preferId;

	/**
	 *偏好名
	 */
	@Column(id = "prefer_name", datatype = "string32")
	private java.lang.String preferName;

	/**
	 *偏好内容
	 */
	@Column(id = "prefer_content", datatype = "string256")
	private java.lang.String preferContent;

	/**
	 *偏好描述
	 */
	@Column(id = "prefer_desc", datatype = "string256")
	private java.lang.String preferDesc;

	/**
	 *项目ID
	 */
	@Column(id = "project_id", datatype = "mediumint")
	private java.lang.Integer projectId;

	/**
	 * 设置主键
	 */
	public void setPreferId(java.lang.Integer preferId) {
		this.preferId = preferId;
	}

	/**
	 * 获取主键
	 */
	public java.lang.Integer getPreferId() {
		return preferId;
	}

	/**
	 * 设置偏好名
	 */
	public void setPreferName(java.lang.String preferName) {
		this.preferName = preferName;
	}

	/**
	 * 获取偏好名
	 */
	public java.lang.String getPreferName() {
		return preferName;
	}

	/**
	 * 设置偏好内容
	 */
	public void setPreferContent(java.lang.String preferContent) {
		this.preferContent = preferContent;
	}

	/**
	 * 获取偏好内容
	 */
	public java.lang.String getPreferContent() {
		return preferContent;
	}

	/**
	 * 设置偏好描述
	 */
	public void setPreferDesc(java.lang.String preferDesc) {
		this.preferDesc = preferDesc;
	}

	/**
	 * 获取偏好描述
	 */
	public java.lang.String getPreferDesc() {
		return preferDesc;
	}

	/**
	 * 设置项目ID
	 */
	public void setProjectId(java.lang.Integer projectId) {
		this.projectId = projectId;
	}

	/**
	 * 获取项目ID
	 */
	public java.lang.Integer getProjectId() {
		return projectId;
	}
}
