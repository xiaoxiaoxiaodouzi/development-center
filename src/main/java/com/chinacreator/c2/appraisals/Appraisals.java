
package com.chinacreator.c2.appraisals;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 考核表
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.appraisals.Appraisals", table = "c2_appraisals", ds = "dev-center")
public class Appraisals implements Serializable {
	private static final long serialVersionUID = 3026948309237760L;
	/**
	*主键
	*/
	@Column(id = "id_", type = ColumnType.uuid, datatype = "string64")
	private java.lang.String id;

	/**
	*被考核用户id
	*/
	@Column(id = "user_id", datatype = "string64")
	private java.lang.String userId;

	/**
	*被考核用户姓名
	*/
	@Column(id = "user_realname", datatype = "string64")
	private java.lang.String userRealname;

	/**
	*考核年份
	*/
	@Column(id = "year", datatype = "int")
	private java.lang.Integer year;

	/**
	*考核月份
	*/
	@Column(id = "month", datatype = "int")
	private java.lang.Integer month;

	/**
	*考核分数
	*/
	@Column(id = "score", datatype = "int")
	private java.lang.Integer score;

	/**
	*考核评级
	*/
	@Column(id = "level", datatype = "string64")
	private java.lang.String level;

	/**
	*考核人ID
	*/
	@Column(id = "assessor_id", datatype = "string64")
	private java.lang.String assessorId;

	/**
	*考核人姓名
	*/
	@Column(id = "assessor_realname", datatype = "string64")
	private java.lang.String assessorRealname;

	/**
	*复核人id
	*/
	@Column(id = "reviewer_id", datatype = "string64")
	private java.lang.String reviewerId;

	/**
	*复核人姓名
	*/
	@Column(id = "reviewer_realname", datatype = "string64")
	private java.lang.String reviewerRealname;

	/**
	*版本
	*/
	@Column(id = "version", datatype = "string64")
	private java.lang.String version;

	/**
	*状态
	*/
	@Column(id = "status", datatype = "string64")
	private java.lang.String status;

	/**
	*详情
	*/
	@Column(id = "detail", datatype = "string64")
	private java.lang.String detail;

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
	*考核期
	*/
	@Column(id = "date", datatype = "date")
	private java.sql.Date date;
	
	private boolean assessable,reviewable;
	
	public boolean isAssessable() {
		return assessable;
	}

	public void setAssessable(boolean assessable) {
		this.assessable = assessable;
	}

	public boolean isReviewable() {
		return reviewable;
	}

	public void setReviewable(boolean reviewable) {
		this.reviewable = reviewable;
	}

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
	* 设置被考核用户id
	*/
	public void setUserId(java.lang.String userId) {
		this.userId = userId;
	}

	/**
	* 获取被考核用户id
	*/
	public java.lang.String getUserId() {
		return userId;
	}

	/**
	* 设置被考核用户姓名
	*/
	public void setUserRealname(java.lang.String userRealname) {
		this.userRealname = userRealname;
	}

	/**
	* 获取被考核用户姓名
	*/
	public java.lang.String getUserRealname() {
		return userRealname;
	}

	/**
	* 设置考核年份
	*/
	public void setYear(java.lang.Integer year) {
		this.year = year;
	}

	/**
	* 获取考核年份
	*/
	public java.lang.Integer getYear() {
		return year;
	}

	/**
	* 设置考核月份
	*/
	public void setMonth(java.lang.Integer month) {
		this.month = month;
	}

	/**
	* 获取考核月份
	*/
	public java.lang.Integer getMonth() {
		return month;
	}

	/**
	* 设置考核分数
	*/
	public void setScore(java.lang.Integer score) {
		this.score = score;
	}

	/**
	* 获取考核分数
	*/
	public java.lang.Integer getScore() {
		return score;
	}

	/**
	* 设置考核评级
	*/
	public void setLevel(java.lang.String level) {
		this.level = level;
	}

	/**
	* 获取考核评级
	*/
	public java.lang.String getLevel() {
		return level;
	}

	/**
	* 设置考核人ID
	*/
	public void setAssessorId(java.lang.String assessorId) {
		this.assessorId = assessorId;
	}

	/**
	* 获取考核人ID
	*/
	public java.lang.String getAssessorId() {
		return assessorId;
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
	* 设置版本
	*/
	public void setVersion(java.lang.String version) {
		this.version = version;
	}

	/**
	* 获取版本
	*/
	public java.lang.String getVersion() {
		return version;
	}

	/**
	* 设置状态
	*/
	public void setStatus(java.lang.String status) {
		this.status = status;
	}

	/**
	* 获取状态
	*/
	public java.lang.String getStatus() {
		return status;
	}

	/**
	* 设置详情
	*/
	public void setDetail(java.lang.String detail) {
		this.detail = detail;
	}

	/**
	* 获取详情
	*/
	public java.lang.String getDetail() {
		return detail;
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

	/**
	* 设置考核期
	*/
	public void setDate(java.sql.Date date) {
		this.date = date;
	}

	/**
	* 获取考核期
	*/
	public java.sql.Date getDate() {
		return date;
	}
}
