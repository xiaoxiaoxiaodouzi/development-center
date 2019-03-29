package com.chinacreator.c2.project.bug;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * bug标签关联
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.bug.BugLabel", table = "c2_community_label_bug", ds = "dev-center")
public class BugLabel implements Serializable {
	private static final long serialVersionUID = 1359127425728512L;
	/**
	 *
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *标签
	 */
	@Column(id = "label_id", datatype = "mediumint")
	private java.lang.Integer labelId;

	/**
	 *bug
	 */
	@Column(id = "bug_id", datatype = "mediumint")
	private java.lang.Integer bugId;

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
	 * 设置标签
	 */
	public void setLabelId(java.lang.Integer labelId) {
		this.labelId = labelId;
	}

	/**
	 * 获取标签
	 */
	public java.lang.Integer getLabelId() {
		return labelId;
	}

	/**
	 * 设置bug
	 */
	public void setBugId(java.lang.Integer bugId) {
		this.bugId = bugId;
	}

	/**
	 * 获取bug
	 */
	public java.lang.Integer getBugId() {
		return bugId;
	}
}
