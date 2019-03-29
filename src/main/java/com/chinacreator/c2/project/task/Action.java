package com.chinacreator.c2.project.task;

import java.io.Serializable;
import java.util.Collection;

import com.chinacreator.c2.annotation.Children;
import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 操作
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.task.action", table = "c2_comunity_action", ds = "mysql")
public class Action implements Serializable {
	private static final long serialVersionUID = 1238707939835904L;
	/**
	 *操作id
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *操作对象类型
	 */
	@Column(id = "target_type", datatype = "string32")
	private java.lang.String targetType;

	/**
	 *操作对象id
	 */
	@Column(id = "target_id", datatype = "mediumint")
	private java.lang.Integer targetId;

	/**
	 *所属项目
	 */
	@Column(id = "project_id", datatype = "mediumint")
	private java.lang.Integer projectId;

	/**
	 *操作人
	 */
	@Column(id = "actor", datatype = "string64")
	private java.lang.String actor;

	/**
	 *操作类型
	 */
	@Column(id = "type_", datatype = "string32")
	private java.lang.String type;

	/**
	 *操作时间
	 */
	@Column(id = "date_", datatype = "timestamp")
	private java.sql.Timestamp date;

	/**
	 *操作描述
	 */
	@Column(id = "comment", datatype = "text")
	private java.lang.String comment;

	/**
	 *额外信息
	 */
	@Column(id = "extra", datatype = "text")
	private java.lang.String extra;

	/**
	 *额外信息
	 */
	@Column(id = "extra1", datatype = "text")
	private java.lang.String extra1;

	/**
	 *操作详情
	 */
	@Children(id = "history", fk = "id:action_id")
	private Collection<History> history;

	/**
	 * 设置操作id
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取操作id
	 */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * 设置操作对象类型
	 */
	public void setTargetType(java.lang.String targetType) {
		this.targetType = targetType;
	}

	/**
	 * 获取操作对象类型
	 */
	public java.lang.String getTargetType() {
		return targetType;
	}

	/**
	 * 设置操作对象id
	 */
	public void setTargetId(java.lang.Integer targetId) {
		this.targetId = targetId;
	}

	/**
	 * 获取操作对象id
	 */
	public java.lang.Integer getTargetId() {
		return targetId;
	}

	/**
	 * 设置所属项目
	 */
	public void setProjectId(java.lang.Integer projectId) {
		this.projectId = projectId;
	}

	/**
	 * 获取所属项目
	 */
	public java.lang.Integer getProjectId() {
		return projectId;
	}

	/**
	 * 设置操作人
	 */
	public void setActor(java.lang.String actor) {
		this.actor = actor;
	}

	/**
	 * 获取操作人
	 */
	public java.lang.String getActor() {
		return actor;
	}

	/**
	 * 设置操作类型
	 */
	public void setType(java.lang.String type) {
		this.type = type;
	}

	/**
	 * 获取操作类型
	 */
	public java.lang.String getType() {
		return type;
	}

	/**
	 * 设置操作时间
	 */
	public void setDate(java.sql.Timestamp date) {
		this.date = date;
	}

	/**
	 * 获取操作时间
	 */
	public java.sql.Timestamp getDate() {
		return date;
	}

	/**
	 * 设置操作描述
	 */
	public void setComment(java.lang.String comment) {
		this.comment = comment;
	}

	/**
	 * 获取操作描述
	 */
	public java.lang.String getComment() {
		return comment;
	}

	/**
	 * 设置额外信息
	 */
	public void setExtra(java.lang.String extra) {
		this.extra = extra;
	}

	/**
	 * 获取额外信息
	 */
	public java.lang.String getExtra() {
		return extra;
	}

	/**
	 * 设置额外信息
	 */
	public void setExtra1(java.lang.String extra1) {
		this.extra1 = extra1;
	}

	/**
	 * 获取额外信息
	 */
	public java.lang.String getExtra1() {
		return extra1;
	}

	/**
	 * 设置操作详情
	 */
	public void setHistory(Collection<History> history) {
		this.history = history;
	}

	/**
	 * 获取操作详情
	 */
	public Collection<History> getHistory() {
		return history;
	}
}
