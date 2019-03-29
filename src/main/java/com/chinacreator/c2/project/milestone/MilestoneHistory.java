package com.chinacreator.c2.project.milestone;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 里程碑历史记录
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.milestone.MilestoneHistory", table = "c2_comunity_milestone_history", ds = "dev-center", cache = false)
public class MilestoneHistory {
	/**
	 *主键
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *里程碑
	 */
	@Column(id = "milestone_id", datatype = "mediumint")
	private java.lang.Integer milestoneId;

	/**
	 *目标ID
	 */
	@Column(id = "target_id", datatype = "mediumint")
	private java.lang.Integer targetId;

	/**
	 *目标名字
	 */
	@Column(id = "target_name", datatype = "string512")
	private java.lang.String targetName;

	/**
	 *操作类型
	 */
	@Column(id = "type", datatype = "string128")
	private java.lang.String type;

	/**
	 *值
	 */
	@Column(id = "attr_val", datatype = "text")
	private java.lang.String attrVal;

	/**
	 *评论
	 */
	@Column(id = "content", datatype = "text")
	private java.lang.String content;

	/**
	 *作者
	 */
	@Column(id = "user_id", datatype = "string256")
	private java.lang.String userId;

	/**
	 *时间
	 */
	@Column(id = "create_time", datatype = "timestamp")
	private java.sql.Timestamp createTime;

	/**
	 * 设置主键
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取主键
	 */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * 设置里程碑
	 */
	public void setMilestoneId(java.lang.Integer milestoneId) {
		this.milestoneId = milestoneId;
	}

	/**
	 * 获取里程碑
	 */
	public java.lang.Integer getMilestoneId() {
		return milestoneId;
	}

	/**
	 * 设置目标ID
	 */
	public void setTargetId(java.lang.Integer targetId) {
		this.targetId = targetId;
	}

	/**
	 * 获取目标ID
	 */
	public java.lang.Integer getTargetId() {
		return targetId;
	}

	/**
	 * 设置目标名字
	 */
	public void setTargetName(java.lang.String targetName) {
		this.targetName = targetName;
	}

	/**
	 * 获取目标名字
	 */
	public java.lang.String getTargetName() {
		return targetName;
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
	 * 设置值
	 */
	public void setAttrVal(java.lang.String attrVal) {
		this.attrVal = attrVal;
	}

	/**
	 * 获取值
	 */
	public java.lang.String getAttrVal() {
		return attrVal;
	}

	/**
	 * 设置评论
	 */
	public void setContent(java.lang.String content) {
		this.content = content;
	}

	/**
	 * 获取评论
	 */
	public java.lang.String getContent() {
		return content;
	}

	/**
	 * 设置作者
	 */
	public void setUserId(java.lang.String userId) {
		this.userId = userId;
	}

	/**
	 * 获取作者
	 */
	public java.lang.String getUserId() {
		return userId;
	}

	/**
	 * 设置时间
	 */
	public void setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
	}

	/**
	 * 获取时间
	 */
	public java.sql.Timestamp getCreateTime() {
		return createTime;
	}
}
