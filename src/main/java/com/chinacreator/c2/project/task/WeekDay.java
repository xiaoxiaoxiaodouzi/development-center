package com.chinacreator.c2.project.task;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 工作日
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.task.WeekDay", table = "c2_community_task_weekday", ds = "dev-center")
public class WeekDay implements Serializable {
	private static final long serialVersionUID = 1339150173585408L;
	/**
	 *主键ID
	 */
	@Column(id = "id", type = ColumnType.uuid, datatype = "char22")
	private java.lang.String id;

	/**
	 *工作日
	 */
	@Column(id = "workdate", datatype = "date")
	private java.sql.Date workdate;

	/**
	 *工时数
	 */
	@Column(id = "estimate", datatype = "double")
	private java.lang.Double estimate;

	/**
	 * 设置主键ID
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	 * 获取主键ID
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * 设置工作日
	 */
	public void setWorkdate(java.sql.Date workdate) {
		this.workdate = workdate;
	}

	/**
	 * 获取工作日
	 */
	public java.sql.Date getWorkdate() {
		return workdate;
	}

	/**
	 * 设置工时数
	 */
	public void setEstimate(java.lang.Double estimate) {
		this.estimate = estimate;
	}

	/**
	 * 获取工时数
	 */
	public java.lang.Double getEstimate() {
		return estimate;
	}
}
