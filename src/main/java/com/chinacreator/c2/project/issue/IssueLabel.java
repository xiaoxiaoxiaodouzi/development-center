package com.chinacreator.c2.project.issue;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 问题标签关联
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.issue.IssueLabel", table = "c2_comunity_issue_label", ds = "dev-center")
public class IssueLabel implements Serializable {
	private static final long serialVersionUID = 1018187683004416L;
	/**
	 *序号
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *问题ID
	 */
	@Column(id = "issue_id", datatype = "mediumint")
	private java.lang.Integer issueId;

	/**
	 *标签ID
	 */
	@Column(id = "label_id", datatype = "mediumint")
	private java.lang.Integer labelId;

	/**
	 * 设置序号
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取序号
	 */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * 设置问题ID
	 */
	public void setIssueId(java.lang.Integer issueId) {
		this.issueId = issueId;
	}

	/**
	 * 获取问题ID
	 */
	public java.lang.Integer getIssueId() {
		return issueId;
	}

	/**
	 * 设置标签ID
	 */
	public void setLabelId(java.lang.Integer labelId) {
		this.labelId = labelId;
	}

	/**
	 * 获取标签ID
	 */
	public java.lang.Integer getLabelId() {
		return labelId;
	}
}
