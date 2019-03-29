package com.chinacreator.c2.project.task;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 操作详情
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.task.history", table = "c2_comunity_history", ds = "mysql")
public class History implements Serializable {
	private static final long serialVersionUID = 1238710286614528L;
	/**
	 *详情id
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *操作id
	 */
	@Column(id = "action_id", datatype = "mediumint")
	private java.lang.Integer actionId;

	/**
	 *属性名
	 */
	@Column(id = "field_", datatype = "string32")
	private java.lang.String field;

	/**
	 *旧值
	 */
	@Column(id = "old_val", datatype = "text")
	private java.lang.String oldVal;

	/**
	 *新值
	 */
	@Column(id = "new_val", datatype = "text")
	private java.lang.String newVal;

	/**
	 *区别
	 */
	@Column(id = "diff", datatype = "text")
	private java.lang.String diff;

	/**
	 * 设置详情id
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取详情id
	 */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * 设置操作id
	 */
	public void setActionId(java.lang.Integer actionId) {
		this.actionId = actionId;
	}

	/**
	 * 获取操作id
	 */
	public java.lang.Integer getActionId() {
		return actionId;
	}

	/**
	 * 设置属性名
	 */
	public void setField(java.lang.String field) {
		this.field = field;
	}

	/**
	 * 获取属性名
	 */
	public java.lang.String getField() {
		return field;
	}

	/**
	 * 设置旧值
	 */
	public void setOldVal(java.lang.String oldVal) {
		this.oldVal = oldVal;
	}

	/**
	 * 获取旧值
	 */
	public java.lang.String getOldVal() {
		return oldVal;
	}

	/**
	 * 设置新值
	 */
	public void setNewVal(java.lang.String newVal) {
		this.newVal = newVal;
	}

	/**
	 * 获取新值
	 */
	public java.lang.String getNewVal() {
		return newVal;
	}

	/**
	 * 设置区别
	 */
	public void setDiff(java.lang.String diff) {
		this.diff = diff;
	}

	/**
	 * 获取区别
	 */
	public java.lang.String getDiff() {
		return diff;
	}
}
