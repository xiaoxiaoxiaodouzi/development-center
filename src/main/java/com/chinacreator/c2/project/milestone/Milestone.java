package com.chinacreator.c2.project.milestone;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 里程碑
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.milestone.Milestone", table = "c2_comunity_milestone", ds = "dev-center", cache = false)
public class Milestone {
	/**
	 *里程碑ID
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "int")
	private java.lang.Integer id;

	/**
	 *项目ID
	 */
	@Column(id = "project_id", datatype = "int")
	private java.lang.Integer projectId;

	/**
	 *标题
	 */
	@Column(id = "title", datatype = "string1536")
	private java.lang.String title;

	/**
	 *描述
	 */
	@Column(id = "description", datatype = "string1536")
	private java.lang.String description;

	/**
	 *状态:0草稿，1确认，2关闭
	 */
	@Column(id = "status", datatype = "string5")
	private java.lang.String status;

	/**
	 *确认时间
	 */
	@Column(id = "opendate", datatype = "timestamp")
	private java.sql.Timestamp opendate;

	/**
	 *关闭时间
	 */
	@Column(id = "closedate", datatype = "timestamp")
	private java.sql.Timestamp closedate;

	/**
	 *截止时间
	 */
	@Column(id = "enddate", datatype = "timestamp")
	private java.sql.Timestamp enddate;

	/**
	 * 设置里程碑ID
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取里程碑ID
	 */
	public java.lang.Integer getId() {
		return id;
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

	/**
	 * 设置标题
	 */
	public void setTitle(java.lang.String title) {
		this.title = title;
	}

	/**
	 * 获取标题
	 */
	public java.lang.String getTitle() {
		return title;
	}

	/**
	 * 设置描述
	 */
	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	/**
	 * 获取描述
	 */
	public java.lang.String getDescription() {
		return description;
	}

	/**
	 * 设置状态:0草稿，1确认，2关闭
	 */
	public void setStatus(java.lang.String status) {
		this.status = status;
	}

	/**
	 * 获取状态:0草稿，1确认，2关闭
	 */
	public java.lang.String getStatus() {
		return status;
	}

	/**
	 * 设置确认时间
	 */
	public void setOpendate(java.sql.Timestamp opendate) {
		this.opendate = opendate;
	}

	/**
	 * 获取确认时间
	 */
	public java.sql.Timestamp getOpendate() {
		return opendate;
	}

	/**
	 * 设置关闭时间
	 */
	public void setClosedate(java.sql.Timestamp closedate) {
		this.closedate = closedate;
	}

	/**
	 * 获取关闭时间
	 */
	public java.sql.Timestamp getClosedate() {
		return closedate;
	}

	/**
	 * 设置截止时间
	 */
	public void setEnddate(java.sql.Timestamp enddate) {
		this.enddate = enddate;
	}

	/**
	 * 获取截止时间
	 */
	public java.sql.Timestamp getEnddate() {
		return enddate;
	}
}
