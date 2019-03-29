package com.chinacreator.c2.project.weeklyReport;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 周报提交给
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.weeklyReport.WeeklyReportSubmit2", table = "c2_comunity_wr_submit2", ds = "dev-center")
public class WeeklyReportSubmit2 implements Serializable {
	private static final long serialVersionUID = 1292590205075456L;
	/**
	 *主键
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *项目ID
	 */
	@Column(id = "project_id", datatype = "mediumint")
	private java.lang.Integer projectId;

	/**
	 *用户id
	 */
	@Column(id = "submit_to", datatype = "char32")
	private java.lang.String submitTo;

	/**
	 * 设置主键
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取主键
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
	 * 设置用户id
	 */
	public void setSubmitTo(java.lang.String submitTo) {
		this.submitTo = submitTo;
	}

	/**
	 * 获取用户id
	 */
	public java.lang.String getSubmitTo() {
		return submitTo;
	}
}
