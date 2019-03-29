package com.chinacreator.c2.project.plan;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 测试计划
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.plan.Plan", table = "c2_community_case_plan", ds = "dev-center")
public class Plan implements Serializable {
	private static final long serialVersionUID = 2061564333604864L;
	/**
	 *
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "int")
	private java.lang.Integer id;

	/**
	 *计划名称
	 */
	@Column(id = "name", datatype = "string256")
	private java.lang.String name;

	/**
	 *里程碑id
	 */
	@Column(id = "milestone_id", datatype = "int")
	private java.lang.Integer milestoneId;

	/**
	 *版本
	 */
	@Column(id = "version", datatype = "string128")
	private java.lang.String version;

	/**
	 *版本说明
	 */
	@Column(id = "desc_", datatype = "string512")
	private java.lang.String desc;

	/**
	 *创建者
	 */
	@Column(id = "user_name", datatype = "string64")
	private java.lang.String userName;

	/**
	 *创建时间
	 */
	@Column(id = "ctime", datatype = "timestamp")
	private java.sql.Timestamp ctime;

	/**
	 *项目id
	 */
	@Column(id = "project_id", datatype = "int")
	private java.lang.Integer projectId;

	/**
	 * 设置
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取
	 */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * 设置计划名称
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * 获取计划名称
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * 设置里程碑id
	 */
	public void setMilestoneId(java.lang.Integer milestoneId) {
		this.milestoneId = milestoneId;
	}

	/**
	 * 获取里程碑id
	 */
	public java.lang.Integer getMilestoneId() {
		return milestoneId;
	}

	/**
	 * 设置版本
	 */
	public void setVersion(java.lang.String version) {
		this.version = version;
	}

	/**
	 * 获取版本
	 */
	public java.lang.String getVersion() {
		return version;
	}

	/**
	 * 设置版本说明
	 */
	public void setDesc(java.lang.String desc) {
		this.desc = desc;
	}

	/**
	 * 获取版本说明
	 */
	public java.lang.String getDesc() {
		return desc;
	}

	/**
	 * 设置创建者
	 */
	public void setUserName(java.lang.String userName) {
		this.userName = userName;
	}

	/**
	 * 获取创建者
	 */
	public java.lang.String getUserName() {
		return userName;
	}

	/**
	 * 设置创建时间
	 */
	public void setCtime(java.sql.Timestamp ctime) {
		this.ctime = ctime;
	}

	/**
	 * 获取创建时间
	 */
	public java.sql.Timestamp getCtime() {
		return ctime;
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
}
