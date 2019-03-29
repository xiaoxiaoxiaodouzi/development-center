
package com.chinacreator.c2.appraisals;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 考核授权管理
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.appraisals.Assessor", table = "c2_assessor_config", ds = "dev-center")
public class Assessor implements Serializable {
	private static final long serialVersionUID = 3026961383129088L;
	/**
	*主键
	*/
	@Column(id = "id_", type = ColumnType.uuid, datatype = "string64")
	private java.lang.String id;

	/**
	*被考核人id
	*/
	@Column(id = "user_id", datatype = "string64")
	private java.lang.String userId;

	/**
	*考核人id
	*/
	@Column(id = "assessor_id", datatype = "string64")
	private java.lang.String assessorId;

	/**
	*复核人id
	*/
	@Column(id = "reviewer_id", datatype = "string64")
	private java.lang.String reviewerId;

	/**
	*被考核人姓名
	*/
	@Column(id = "user_realname", datatype = "string64")
	private java.lang.String userRealname;

	/**
	*考核人姓名
	*/
	@Column(id = "assessor_realname", datatype = "string64")
	private java.lang.String assessorRealname;

	/**
	*复核人姓名
	*/
	@Column(id = "reviewer_realname", datatype = "string64")
	private java.lang.String reviewerRealname;

	/**
	*被考核人账号
	*/
	@Column(id = "user_name", datatype = "string64")
	private java.lang.String userName;

	/**
	*考核人账号
	*/
	@Column(id = "assessor_name", datatype = "string64")
	private java.lang.String assessorName;

	/**
	*复核人账号
	*/
	@Column(id = "reviewer_name", datatype = "string64")
	private java.lang.String reviewerName;

	/**
	* 设置主键
	*/
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	* 获取主键
	*/
	public java.lang.String getId() {
		return id;
	}

	/**
	* 设置被考核人id
	*/
	public void setUserId(java.lang.String userId) {
		this.userId = userId;
	}

	/**
	* 获取被考核人id
	*/
	public java.lang.String getUserId() {
		return userId;
	}

	/**
	* 设置考核人id
	*/
	public void setAssessorId(java.lang.String assessorId) {
		this.assessorId = assessorId;
	}

	/**
	* 获取考核人id
	*/
	public java.lang.String getAssessorId() {
		return assessorId;
	}

	/**
	* 设置复核人id
	*/
	public void setReviewerId(java.lang.String reviewerId) {
		this.reviewerId = reviewerId;
	}

	/**
	* 获取复核人id
	*/
	public java.lang.String getReviewerId() {
		return reviewerId;
	}

	/**
	* 设置被考核人姓名
	*/
	public void setUserRealname(java.lang.String userRealname) {
		this.userRealname = userRealname;
	}

	/**
	* 获取被考核人姓名
	*/
	public java.lang.String getUserRealname() {
		return userRealname;
	}

	/**
	* 设置考核人姓名
	*/
	public void setAssessorRealname(java.lang.String assessorRealname) {
		this.assessorRealname = assessorRealname;
	}

	/**
	* 获取考核人姓名
	*/
	public java.lang.String getAssessorRealname() {
		return assessorRealname;
	}

	/**
	* 设置复核人姓名
	*/
	public void setReviewerRealname(java.lang.String reviewerRealname) {
		this.reviewerRealname = reviewerRealname;
	}

	/**
	* 获取复核人姓名
	*/
	public java.lang.String getReviewerRealname() {
		return reviewerRealname;
	}

	/**
	* 设置被考核人账号
	*/
	public void setUserName(java.lang.String userName) {
		this.userName = userName;
	}

	/**
	* 获取被考核人账号
	*/
	public java.lang.String getUserName() {
		return userName;
	}

	/**
	* 设置考核人账号
	*/
	public void setAssessorName(java.lang.String assessorName) {
		this.assessorName = assessorName;
	}

	/**
	* 获取考核人账号
	*/
	public java.lang.String getAssessorName() {
		return assessorName;
	}

	/**
	* 设置复核人账号
	*/
	public void setReviewerName(java.lang.String reviewerName) {
		this.reviewerName = reviewerName;
	}

	/**
	* 获取复核人账号
	*/
	public java.lang.String getReviewerName() {
		return reviewerName;
	}
}
