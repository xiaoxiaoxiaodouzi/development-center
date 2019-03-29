package com.chinacreator.c2.project.issue;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 问题
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.issue.Issue", table = "c2_comunity_issue", ds = "dev-center")
public class Issue implements Serializable {
	private static final long serialVersionUID = 1018187726143488L;
	/**
	 *问题ID
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *标题
	 */
	@Column(id = "title", datatype = "string512")
	private java.lang.String title;

	/**
	 *项目编号
	 */
	@Column(id = "project", datatype = "mediumint")
	private java.lang.Integer project;

	/**
	 *状态(0:关闭,1:打开)
	 */
	@Column(id = "status", datatype = "char20")
	private java.lang.String status;

	/**
	 * 设置问题ID
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取问题ID
	 */
	public java.lang.Integer getId() {
		return id;
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
	 * 设置项目编号
	 */
	public void setProject(java.lang.Integer project) {
		this.project = project;
	}

	/**
	 * 获取项目编号
	 */
	public java.lang.Integer getProject() {
		return project;
	}

	/**
	 * 设置状态(0:关闭,1:打开)
	 */
	public void setStatus(java.lang.String status) {
		this.status = status;
	}

	/**
	 * 获取状态(0:关闭,1:打开)
	 */
	public java.lang.String getStatus() {
		return status;
	}
}
