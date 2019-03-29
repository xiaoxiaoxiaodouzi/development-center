package com.chinacreator.c2.project.label;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 标签
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.label.Label", table = "c2_comunity_label", ds = "dev-center")
public class Label implements Serializable {
	private static final long serialVersionUID = 1283990515449856L;
	/**
	 *标签ID
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *项目编号
	 */
	@Column(id = "project", datatype = "mediumint")
	private java.lang.Integer project;

	/**
	 *名称
	 */
	@Column(id = "name", datatype = "string256")
	private java.lang.String name;

	/**
	 *颜色
	 */
	@Column(id = "color", datatype = "string10")
	private java.lang.String color;

	/**
	 *创建时间
	 */
	@Column(id = "createtime", datatype = "timestamp")
	private java.sql.Timestamp createtime;

	/**
	 *排序号
	 */
	@Column(id = "rank", datatype = "mediumint")
	private java.lang.Integer rank;

	/**
	 * 设置标签ID
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取标签ID
	 */
	public java.lang.Integer getId() {
		return id;
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
	 * 设置名称
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * 获取名称
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * 设置颜色
	 */
	public void setColor(java.lang.String color) {
		this.color = color;
	}

	/**
	 * 获取颜色
	 */
	public java.lang.String getColor() {
		return color;
	}

	/**
	 * 设置创建时间
	 */
	public void setCreatetime(java.sql.Timestamp createtime) {
		this.createtime = createtime;
	}

	/**
	 * 获取创建时间
	 */
	public java.sql.Timestamp getCreatetime() {
		return createtime;
	}

	/**
	 * 设置排序号
	 */
	public void setRank(java.lang.Integer rank) {
		this.rank = rank;
	}

	/**
	 * 获取排序号
	 */
	public java.lang.Integer getRank() {
		return rank;
	}
}
