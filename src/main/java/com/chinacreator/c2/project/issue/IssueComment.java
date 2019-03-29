package com.chinacreator.c2.project.issue;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 问题评论
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.issue.IssueComment", table = "c2_comunity_issue_comments", ds = "dev-center")
public class IssueComment implements Serializable {
	private static final long serialVersionUID = 1018187615846400L;
	/**
	 *回复ID
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *问题ID
	 */
	@Column(id = "issue_id", datatype = "mediumint")
	private java.lang.Integer issueId;

	/**
	 *作者ID
	 */
	@Column(id = "author_id", datatype = "string64")
	private java.lang.String authorId;

	/**
	 *创建时间
	 */
	@Column(id = "createtime", datatype = "timestamp")
	private java.sql.Timestamp createtime;

	/**
	 *内容
	 */
	@Column(id = "content", datatype = "text")
	private java.lang.String content;

	/**
	 * 设置回复ID
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取回复ID
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
	 * 设置作者ID
	 */
	public void setAuthorId(java.lang.String authorId) {
		this.authorId = authorId;
	}

	/**
	 * 获取作者ID
	 */
	public java.lang.String getAuthorId() {
		return authorId;
	}

	/**
	 * 设置创建时间
	 */
	public void setCreatetime(java.sql.Timestamp createtime) {
		this.createtime = createtime;
	}

	/**
	 * 获取创建时间
	 */
	public java.sql.Timestamp getCreatetime() {
		return createtime;
	}

	/**
	 * 设置内容
	 */
	public void setContent(java.lang.String content) {
		this.content = content;
	}

	/**
	 * 获取内容
	 */
	public java.lang.String getContent() {
		return content;
	}
}
