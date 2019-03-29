
package com.chinacreator.c2.project.weeklyReport;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 周报
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.weeklyReport.WeeklyReport", table = "c2_comunity_wr", ds = "dev-center")
public class WeeklyReport implements Serializable {
	private static final long serialVersionUID = 1288493857193984L;
	/**
	*周报id
	*/
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	*项目id
	*/
	@Column(id = "project_id", datatype = "mediumint")
	private java.lang.Integer projectId;

	/**
	*年份
	*/
	@Column(id = "year", datatype = "mediumint")
	private java.lang.Integer year;

	/**
	*周编号
	*/
	@Column(id = "week", datatype = "mediumint")
	private java.lang.Integer week;

	/**
	*总结
	*/
	@Column(id = "summary", datatype = "string2000")
	private java.lang.String summary;

	/**
	*统计信息
	*/
	@Column(id = "statistics", association = true)
	private WeeklyStatistics statistics;

	/**
	*总体状态
	*/
	@Column(id = "status_", datatype = "string64")
	private java.lang.String status;

	/**
	*可见范围
	*/
	@Column(id = "visible_range", datatype = "string20")
	private java.lang.String visibleRange;

	/**
	*下周总结
	*/
	@Column(id = "next_summary", datatype = "string2000")
	private java.lang.String nextSummary;

	/**
	*可能存在的问题
	*/
	@Column(id = "exist_problems", datatype = "string2000")
	private java.lang.String existProblems;

	/**
	* 设置周报id
	*/
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	* 获取周报id
	*/
	public java.lang.Integer getId() {
		return id;
	}

	/**
	* 设置项目id
	*/
	public void setProjectId(java.lang.Integer projectId) {
		this.projectId = projectId;
	}

	/**
	* 获取项目id
	*/
	public java.lang.Integer getProjectId() {
		return projectId;
	}

	/**
	* 设置年份
	*/
	public void setYear(java.lang.Integer year) {
		this.year = year;
	}

	/**
	* 获取年份
	*/
	public java.lang.Integer getYear() {
		return year;
	}

	/**
	* 设置周编号
	*/
	public void setWeek(java.lang.Integer week) {
		this.week = week;
	}

	/**
	* 获取周编号
	*/
	public java.lang.Integer getWeek() {
		return week;
	}

	/**
	* 设置总结
	*/
	public void setSummary(java.lang.String summary) {
		this.summary = summary;
	}

	/**
	* 获取总结
	*/
	public java.lang.String getSummary() {
		return summary;
	}

	/**
	* 设置统计信息
	*/
	public void setStatistics(WeeklyStatistics statistics) {
		this.statistics = statistics;
	}

	/**
	* 获取统计信息
	*/
	public WeeklyStatistics getStatistics() {
		return statistics;
	}

	/**
	* 设置总体状态
	*/
	public void setStatus(java.lang.String status) {
		this.status = status;
	}

	/**
	* 获取总体状态
	*/
	public java.lang.String getStatus() {
		return status;
	}

	/**
	* 设置可见范围
	*/
	public void setVisibleRange(java.lang.String visibleRange) {
		this.visibleRange = visibleRange;
	}

	/**
	* 获取可见范围
	*/
	public java.lang.String getVisibleRange() {
		return visibleRange;
	}

	/**
	* 设置下周总结
	*/
	public void setNextSummary(java.lang.String nextSummary) {
		this.nextSummary = nextSummary;
	}

	/**
	* 获取下周总结
	*/
	public java.lang.String getNextSummary() {
		return nextSummary;
	}

	/**
	* 设置可能存在的问题
	*/
	public void setExistProblems(java.lang.String existProblems) {
		this.existProblems = existProblems;
	}

	/**
	* 获取可能存在的问题
	*/
	public java.lang.String getExistProblems() {
		return existProblems;
	}
}
