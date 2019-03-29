package com.chinacreator.c2.project.issue;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 问题任务关联
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.issue.IssueTask", table = "C2_COMUNITY_ISSUE_TASK", ds = "dev-center")
public class IssueTask implements Serializable {
	private static final long serialVersionUID = 1036299552456704L;
	/**
	 *序号
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *问题ID
	 */
	@Column(id = "issue_id", datatype = "mediumint")
	private java.lang.Integer issueId;

	/**
	 *任务ID
	 */
	@Column(id = "task_id", datatype = "mediumint")
	private java.lang.Integer taskId;

	/**
	 * 设置序号
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取序号
	 */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * 设置问题ID
	 */
	public void setIssueId(java.lang.Integer issueId) {
		this.issueId = issueId;
	}

	/**
	 * 获取问题ID
	 */
	public java.lang.Integer getIssueId() {
		return issueId;
	}

	/**
	 * 设置任务ID
	 */
	public void setTaskId(java.lang.Integer taskId) {
		this.taskId = taskId;
	}

	/**
	 * 获取任务ID
	 */
	public java.lang.Integer getTaskId() {
		return taskId;
	}
}
