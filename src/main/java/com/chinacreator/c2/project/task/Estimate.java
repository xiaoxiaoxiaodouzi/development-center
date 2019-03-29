package com.chinacreator.c2.project.task;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 工时记录
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.task.estimate", table = "c2_comunity_task_estimate", ds = "mysql")
public class Estimate implements Serializable {
	private static final long serialVersionUID = 1238708644823040L;
	/**
	 *主键
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "int")
	private java.lang.Integer id;

	/**
	 *所属任务
	 */
	@Column(id = "task_id", datatype = "mediumint")
	private java.lang.Integer taskId;

	/**
	 *工作日期
	 */
	@Column(id = "work_date", datatype = "date")
	private java.sql.Date workDate;

	/**
	 *录入时间
	 */
	@Column(id = "record_date", datatype = "timestamp")
	private java.sql.Timestamp recordDate;

	/**
	 *剩余工时
	 */
	@Column(id = "left_", datatype = "double")
	private java.lang.Double left;

	/**
	 *消耗工时
	 */
	@Column(id = "consumed", datatype = "double")
	private java.lang.Double consumed;

	/**
	 *任务执行人
	 */
	@Column(id = "account", datatype = "string64")
	private java.lang.String account;

	/**
	 *任务描述
	 */
	@Column(id = "description", datatype = "string256")
	private java.lang.String description;

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
	 * 设置所属任务
	 */
	public void setTaskId(java.lang.Integer taskId) {
		this.taskId = taskId;
	}

	/**
	 * 获取所属任务
	 */
	public java.lang.Integer getTaskId() {
		return taskId;
	}

	/**
	 * 设置工作日期
	 */
	public void setWorkDate(java.sql.Date workDate) {
		this.workDate = workDate;
	}

	/**
	 * 获取工作日期
	 */
	public java.sql.Date getWorkDate() {
		return workDate;
	}

	/**
	 * 设置录入时间
	 */
	public void setRecordDate(java.sql.Timestamp recordDate) {
		this.recordDate = recordDate;
	}

	/**
	 * 获取录入时间
	 */
	public java.sql.Timestamp getRecordDate() {
		return recordDate;
	}

	/**
	 * 设置剩余工时
	 */
	public void setLeft(java.lang.Double left) {
		this.left = left;
	}

	/**
	 * 获取剩余工时
	 */
	public java.lang.Double getLeft() {
		return left;
	}

	/**
	 * 设置消耗工时
	 */
	public void setConsumed(java.lang.Double consumed) {
		this.consumed = consumed;
	}

	/**
	 * 获取消耗工时
	 */
	public java.lang.Double getConsumed() {
		return consumed;
	}

	/**
	 * 设置任务执行人
	 */
	public void setAccount(java.lang.String account) {
		this.account = account;
	}

	/**
	 * 获取任务执行人
	 */
	public java.lang.String getAccount() {
		return account;
	}

	/**
	 * 设置任务描述
	 */
	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	/**
	 * 获取任务描述
	 */
	public java.lang.String getDescription() {
		return description;
	}
}
