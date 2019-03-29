package com.chinacreator.c2.project.story;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 需求标签关联
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.story.StoryLabel", table = "c2_comunity_label_story", ds = "dev-center", cache = false)
public class StoryLabel {
	/**
	 *
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *
	 */
	@Column(id = "label_id", datatype = "mediumint")
	private java.lang.Integer labelId;

	/**
	 *
	 */
	@Column(id = "story_id", datatype = "mediumint")
	private java.lang.Integer storyId;

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
	public void setLabelId(java.lang.Integer labelId) {
		this.labelId = labelId;
	}

	/**
	 * 获取
	 */
	public java.lang.Integer getLabelId() {
		return labelId;
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
}
