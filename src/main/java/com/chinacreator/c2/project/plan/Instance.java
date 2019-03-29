package com.chinacreator.c2.project.plan;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 测试实例
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.plan.Instance", table = "c2_community_case_instance", ds = "dev-center")
public class Instance implements Serializable {
	private static final long serialVersionUID = 2062956453560320L;
	/**
	 *
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "int")
	private java.lang.Integer id;

	/**
	 *用例名
	 */
	@Column(id = "name", datatype = "string256")
	private java.lang.String name;

	/**
	 *用例编号
	 */
	@Column(id = "no", datatype = "string128")
	private java.lang.String no;

	/**
	 *用例等级
	 */
	@Column(id = "level", datatype = "string10")
	private java.lang.String level;

	/**
	 *需求id
	 */
	@Column(id = "story_id", datatype = "int")
	private java.lang.Integer storyId;

	/**
	 *是否为公共用例
	 */
	@Column(id = "is_common", datatype = "boolean")
	private java.lang.Boolean isCommon;

	/**
	 *前置条件
	 */
	@Column(id = "precondition", datatype = "string512")
	private java.lang.String precondition;

	/**
	 *
	 */
	@Column(id = "case_id", datatype = "int")
	private java.lang.Integer caseId;

	/**
	 *
	 */
	@Column(id = "user_name", datatype = "string64")
	private java.lang.String userName;

	/**
	 *创建时间
	 */
	@Column(id = "ctime", datatype = "timestamp")
	private java.sql.Timestamp ctime;

	/**
	 *计划id
	 */
	@Column(id = "plan_id", datatype = "int")
	private java.lang.Integer planId;

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
	 * 设置用例名
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * 获取用例名
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * 设置用例编号
	 */
	public void setNo(java.lang.String no) {
		this.no = no;
	}

	/**
	 * 获取用例编号
	 */
	public java.lang.String getNo() {
		return no;
	}

	/**
	 * 设置用例等级
	 */
	public void setLevel(java.lang.String level) {
		this.level = level;
	}

	/**
	 * 获取用例等级
	 */
	public java.lang.String getLevel() {
		return level;
	}

	/**
	 * 设置需求id
	 */
	public void setStoryId(java.lang.Integer storyId) {
		this.storyId = storyId;
	}

	/**
	 * 获取需求id
	 */
	public java.lang.Integer getStoryId() {
		return storyId;
	}

	/**
	 * 设置是否为公共用例
	 */
	public void setIsCommon(java.lang.Boolean isCommon) {
		this.isCommon = isCommon;
	}

	/**
	 * 获取是否为公共用例
	 */
	public java.lang.Boolean isIsCommon() {
		return isCommon;
	}

	/**
	 * 设置前置条件
	 */
	public void setPrecondition(java.lang.String precondition) {
		this.precondition = precondition;
	}

	/**
	 * 获取前置条件
	 */
	public java.lang.String getPrecondition() {
		return precondition;
	}

	/**
	 * 设置
	 */
	public void setCaseId(java.lang.Integer caseId) {
		this.caseId = caseId;
	}

	/**
	 * 获取
	 */
	public java.lang.Integer getCaseId() {
		return caseId;
	}

	/**
	 * 设置
	 */
	public void setUserName(java.lang.String userName) {
		this.userName = userName;
	}

	/**
	 * 获取
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
	 * 设置计划id
	 */
	public void setPlanId(java.lang.Integer planId) {
		this.planId = planId;
	}

	/**
	 * 获取计划id
	 */
	public java.lang.Integer getPlanId() {
		return planId;
	}
}
