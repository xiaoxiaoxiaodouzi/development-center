package com.chinacreator.c2.project.bug;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * bug回复
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.bug.BugComment", table = "c2_community_bug_comments", ds = "dev-center", cache = false)
public class BugComment {
	/**
	 *
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *
	 */
	@Column(id = "bug_id", datatype = "mediumint")
	private java.lang.Integer bugId;

	/**
	 *
	 */
	@Column(id = "user_id", datatype = "string64")
	private java.lang.String userId;

	/**
	 *
	 */
	@Column(id = "create_time", datatype = "timestamp")
	private java.sql.Timestamp createTime;

	/**
	 *-1为评论类型、其它对应bug相应状态变更操作
	 */
	@Column(id = "type", datatype = "int")
	private java.lang.Integer type;

	/**
	 *
	 */
	@Column(id = "bug_version", datatype = "string256")
	private java.lang.String bugVersion;

	/**
	 *
	 */
	@Column(id = "content", datatype = "text")
	private java.lang.String content;

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
	 * 设置
	 */
	public void setBugId(java.lang.Integer bugId) {
		this.bugId = bugId;
	}

	/**
	 * 获取
	 */
	public java.lang.Integer getBugId() {
		return bugId;
	}

	/**
	 * 设置
	 */
	public void setUserId(java.lang.String userId) {
		this.userId = userId;
	}

	/**
	 * 获取
	 */
	public java.lang.String getUserId() {
		return userId;
	}

	/**
	 * 设置
	 */
	public void setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
	}

	/**
	 * 获取
	 */
	public java.sql.Timestamp getCreateTime() {
		return createTime;
	}

	/**
	 * 设置-1为评论类型、其它对应bug相应状态变更操作
	 */
	public void setType(java.lang.Integer type) {
		this.type = type;
	}

	/**
	 * 获取-1为评论类型、其它对应bug相应状态变更操作
	 */
	public java.lang.Integer getType() {
		return type;
	}

	/**
	 * 设置
	 */
	public void setBugVersion(java.lang.String bugVersion) {
		this.bugVersion = bugVersion;
	}

	/**
	 * 获取
	 */
	public java.lang.String getBugVersion() {
		return bugVersion;
	}

	/**
	 * 设置
	 */
	public void setContent(java.lang.String content) {
		this.content = content;
	}

	/**
	 * 获取
	 */
	public java.lang.String getContent() {
		return content;
	}
}
