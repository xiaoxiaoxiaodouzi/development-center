package com.chinacreator.c2.share;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 共享文档
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.share.shareDoc", table = "c2_comunity_share_doc", ds = "dev-center")
public class ShareDoc implements Serializable {
	private static final long serialVersionUID = 1610965403353088L;
	/**
	 *主键
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *文档名称
	 */
	@Column(id = "name", datatype = "string128")
	private java.lang.String name;

	/**
	 *文档描述
	 */
	@Column(id = "desc_", datatype = "string1024")
	private java.lang.String desc;

	/**
	 *访问次数
	 */
	@Column(id = "visit_count", datatype = "int")
	private java.lang.Integer visitCount;

	/**
	 *是否发布
	 */
	@Column(id = "public_static", datatype = "char1")
	private java.lang.String publicStatic;

	/**
	 *内容
	 */
	@Column(id = "content", datatype = "text")
	private java.lang.String content;

	/**
	 *作者
	 */
	@Column(id = "author_id", datatype = "string64")
	private java.lang.String authorId;

	/**
	 *创建时间
	 */
	@Column(id = "createtime", datatype = "timestamp")
	private java.sql.Timestamp createtime;

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
	 * 设置文档名称
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * 获取文档名称
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * 设置文档描述
	 */
	public void setDesc(java.lang.String desc) {
		this.desc = desc;
	}

	/**
	 * 获取文档描述
	 */
	public java.lang.String getDesc() {
		return desc;
	}

	/**
	 * 设置访问次数
	 */
	public void setVisitCount(java.lang.Integer visitCount) {
		this.visitCount = visitCount;
	}

	/**
	 * 获取访问次数
	 */
	public java.lang.Integer getVisitCount() {
		return visitCount;
	}

	/**
	 * 设置是否发布
	 */
	public void setPublicStatic(java.lang.String publicStatic) {
		this.publicStatic = publicStatic;
	}

	/**
	 * 获取是否发布
	 */
	public java.lang.String getPublicStatic() {
		return publicStatic;
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

	/**
	 * 设置作者
	 */
	public void setAuthorId(java.lang.String authorId) {
		this.authorId = authorId;
	}

	/**
	 * 获取作者
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
}
