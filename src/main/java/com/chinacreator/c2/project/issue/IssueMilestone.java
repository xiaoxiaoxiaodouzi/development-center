package com.chinacreator.c2.project.issue;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 问题里程碑关联
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.issue.IssueMilestone", table = "C2_COMUNITY_ISSUE_MILESTONE", ds = "dev-center")
public class IssueMilestone implements Serializable {
	private static final long serialVersionUID = 1035157206466560L;
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
	 *里程碑ID
	 */
	@Column(id = "milestone_id", datatype = "mediumint")
	private java.lang.Integer milestoneId;

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
	 * 设置里程碑ID
	 */
	public void setMilestoneId(java.lang.Integer milestoneId) {
		this.milestoneId = milestoneId;
	}

	/**
	 * 获取里程碑ID
	 */
	public java.lang.Integer getMilestoneId() {
		return milestoneId;
	}
}
