package com.chinacreator.c2.project.label;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 标签任务关联
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.label.LabelTask", table = "C2_COMUNITY_LABEL_TASK", ds = "dev-center")
public class LabelTask implements Serializable {
	private static final long serialVersionUID = 1251896119033856L;
	/**
	 *序号
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *标签ID
	 */
	@Column(id = "label_id", datatype = "mediumint")
	private java.lang.Integer labelId;

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
	 * 设置标签ID
	 */
	public void setLabelId(java.lang.Integer labelId) {
		this.labelId = labelId;
	}

	/**
	 * 获取标签ID
	 */
	public java.lang.Integer getLabelId() {
		return labelId;
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
