package com.chinacreator.c2.analysis;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 统计分析子项
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.analysis.StatisticAnalysisOption", table = "c2_statistic_analysis_option", ds = "dev-center")
public class StatisticAnalysisOption implements Serializable {
	private static final long serialVersionUID = 1704428968230912L;
	/**
	 *主键
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "int")
	private java.lang.Integer id;

	/**
	 *统计分析主键
	 */
	@Column(id = "stat_id", datatype = "int")
	private java.lang.Integer statId;

	/**
	 *团队主键
	 */
	@Column(id = "team_id", datatype = "string64")
	private java.lang.String teamId;

	/**
	 *项目编号
	 */
	@Column(id = "project_id", datatype = "int")
	private java.lang.Integer projectId;

	/**
	 *用户编号
	 */
	@Column(id = "user_id", datatype = "string64")
	private java.lang.String userId;

	/**
	 *类型
	 */
	@Column(id = "type", datatype = "string128")
	private java.lang.String type;

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
	 * 设置统计分析主键
	 */
	public void setStatId(java.lang.Integer statId) {
		this.statId = statId;
	}

	/**
	 * 获取统计分析主键
	 */
	public java.lang.Integer getStatId() {
		return statId;
	}

	/**
	 * 设置团队主键
	 */
	public void setTeamId(java.lang.String teamId) {
		this.teamId = teamId;
	}

	/**
	 * 获取团队主键
	 */
	public java.lang.String getTeamId() {
		return teamId;
	}

	/**
	 * 设置项目编号
	 */
	public void setProjectId(java.lang.Integer projectId) {
		this.projectId = projectId;
	}

	/**
	 * 获取项目编号
	 */
	public java.lang.Integer getProjectId() {
		return projectId;
	}

	/**
	 * 设置用户编号
	 */
	public void setUserId(java.lang.String userId) {
		this.userId = userId;
	}

	/**
	 * 获取用户编号
	 */
	public java.lang.String getUserId() {
		return userId;
	}

	/**
	 * 设置类型
	 */
	public void setType(java.lang.String type) {
		this.type = type;
	}

	/**
	 * 获取类型
	 */
	public java.lang.String getType() {
		return type;
	}
}
