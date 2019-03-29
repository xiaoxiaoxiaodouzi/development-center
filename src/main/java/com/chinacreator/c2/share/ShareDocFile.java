package com.chinacreator.c2.share;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 共享文档附件
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.share.shareDocFile", table = "c2_comunity_share_doc_file", ds = "dev-center")
public class ShareDocFile implements Serializable {
	private static final long serialVersionUID = 1610966617243648L;
	/**
	 *ID
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *文档ID
	 */
	@Column(id = "doc_id", datatype = "mediumint")
	private java.lang.Integer docId;

	/**
	 *附件名称
	 */
	@Column(id = "name", datatype = "string128")
	private java.lang.String name;

	/**
	 *附件路径
	 */
	@Column(id = "path", datatype = "string256")
	private java.lang.String path;

	/**
	 * 设置ID
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取ID
	 */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * 设置文档ID
	 */
	public void setDocId(java.lang.Integer docId) {
		this.docId = docId;
	}

	/**
	 * 获取文档ID
	 */
	public java.lang.Integer getDocId() {
		return docId;
	}

	/**
	 * 设置附件名称
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * 获取附件名称
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * 设置附件路径
	 */
	public void setPath(java.lang.String path) {
		this.path = path;
	}

	/**
	 * 获取附件路径
	 */
	public java.lang.String getPath() {
		return path;
	}
}
