package com.chinacreator.c2.project.story;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 需求评论
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.story.StoryComment", table = "c2_comunity_story_comments", ds = "dev-center", cache = false)
public class StoryComment {
	/**
	 *
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *
	 */
	@Column(id = "story_id", datatype = "mediumint")
	private java.lang.Integer storyId;

	/**
	 *
	 */
	@Column(id = "user_id", datatype = "string256")
	private java.lang.String userId;

	/**
	 *
	 */
	@Column(id = "create_time", datatype = "timestamp")
	private java.sql.Timestamp createTime;

	/**
	 *
	 */
	@Column(id = "type_", datatype = "string256")
	private java.lang.String type;

	/**
	 *
	 */
	@Column(id = "content", datatype = "text")
	private java.lang.String content;

	/**
	 *
	 */
	@Column(id = "attr_val", datatype = "text")
	private java.lang.String attrVal;

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
	public void setStoryId(java.lang.Integer storyId) {
		this.storyId = storyId;
	}

	/**
	 * 获取
	 */
	public java.lang.Integer getStoryId() {
		return storyId;
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
	 * 设置
	 */
	public void setType(java.lang.String type) {
		this.type = type;
	}

	/**
	 * 获取
	 */
	public java.lang.String getType() {
		return type;
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

	/**
	 * 设置
	 */
	public void setAttrVal(java.lang.String attrVal) {
		this.attrVal = attrVal;
	}

	/**
	 * 获取
	 */
	public java.lang.String getAttrVal() {
		return attrVal;
	}
}
