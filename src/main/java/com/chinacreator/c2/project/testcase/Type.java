package com.chinacreator.c2.project.testcase;

import java.io.Serializable;
import java.util.Collection;

import com.chinacreator.c2.annotation.Children;
import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 用例分类
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.testcase.Type", table = "c2_community_case_type", ds = "dev-center")
public class Type implements Serializable {
	private static final long serialVersionUID = 2062979225845760L;
	/**
	 *分类id
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "int")
	private java.lang.Integer id;

	/**
	 *父分类id
	 */
	@Column(id = "pid", datatype = "int")
	private java.lang.Integer pid;

	/**
	 *分类名
	 */
	@Column(id = "name", datatype = "string256")
	private java.lang.String name;

	/**
	 *所属项目id
	 */
	@Column(id = "project_id", datatype = "int")
	private java.lang.Integer projectId;

	/**
	 *排序号
	 */
	@Column(id = "no_", datatype = "int")
	private java.lang.Integer no;

	/**
	 *用例分类
	 */
	@Children(id = "type", fk = "id:pid")
	private Collection<Type> type;

	/**
	 * 设置分类id
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取分类id
	 */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * 设置父分类id
	 */
	public void setPid(java.lang.Integer pid) {
		this.pid = pid;
	}

	/**
	 * 获取父分类id
	 */
	public java.lang.Integer getPid() {
		return pid;
	}

	/**
	 * 设置分类名
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * 获取分类名
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * 设置所属项目id
	 */
	public void setProjectId(java.lang.Integer projectId) {
		this.projectId = projectId;
	}

	/**
	 * 获取所属项目id
	 */
	public java.lang.Integer getProjectId() {
		return projectId;
	}

	/**
	 * 设置排序号
	 */
	public void setNo(java.lang.Integer no) {
		this.no = no;
	}

	/**
	 * 获取排序号
	 */
	public java.lang.Integer getNo() {
		return no;
	}

	/**
	 * 设置用例分类
	 */
	public void setType(Collection<Type> type) {
		this.type = type;
	}

	/**
	 * 获取用例分类
	 */
	public Collection<Type> getType() {
		return type;
	}
}
