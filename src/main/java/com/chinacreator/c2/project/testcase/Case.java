package com.chinacreator.c2.project.testcase;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 测试用例
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.testcase.Case", table = "c2_community_case", ds = "dev-center")
public class Case implements Serializable {
	private static final long serialVersionUID = 2062977797914624L;
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
	@Column(id = "precondition", datatype = "string2000")
	private java.lang.String precondition;

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
	 *项目id
	 */
	@Column(id = "project_id", datatype = "int")
	private java.lang.Integer projectId;

	/**
	 *分类id
	 */
	@Column(id = "type_id", datatype = "int")
	private java.lang.Integer typeId;

	/**
	 *排序号
	 */
	@Column(id = "order_no", datatype = "int")
	private java.lang.Integer orderNo;

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

	/**
	 * 设置分类id
	 */
	public void setTypeId(java.lang.Integer typeId) {
		this.typeId = typeId;
	}

	/**
	 * 获取分类id
	 */
	public java.lang.Integer getTypeId() {
		return typeId;
	}

	/**
	 * 设置排序号
	 */
	public void setOrderNo(java.lang.Integer orderNo) {
		this.orderNo = orderNo;
	}

	/**
	 * 获取排序号
	 */
	public java.lang.Integer getOrderNo() {
		return orderNo;
	}
}
