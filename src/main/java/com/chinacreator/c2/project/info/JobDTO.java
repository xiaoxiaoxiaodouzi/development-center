package com.chinacreator.c2.project.info;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 岗位
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.info.JobDTO", table = "td_sm_job", ds = "dev-center")
public class JobDTO implements Serializable {
	private static final long serialVersionUID = 2196027387396096L;
	/**
	 *岗位ID
	 */
	@Column(id = "job_id", type = ColumnType.uuid, datatype = "string64")
	private java.lang.String jobId;

	/**
	 *岗位名称
	 */
	@Column(id = "job_name", datatype = "string128")
	private java.lang.String jobName;

	/**
	 *岗位描述
	 */
	@Column(id = "job_desc", datatype = "string256")
	private java.lang.String jobDesc;

	/**
	 *岗位范围(0:通用岗位,1:机构特有)
	 */
	@Column(id = "job_scope", datatype = "boolean")
	private java.lang.Boolean jobScope;

	/**
	 *岗位职责
	 */
	@Column(id = "job_function", datatype = "string256")
	private java.lang.String jobFunction;

	/**
	 *岗位编制人数
	 */
	@Column(id = "job_amount", datatype = "string128")
	private java.lang.String jobAmount;

	/**
	 *岗位编号
	 */
	@Column(id = "job_number", datatype = "string128")
	private java.lang.String jobNumber;

	/**
	 *任职条件
	 */
	@Column(id = "job_condition", datatype = "string256")
	private java.lang.String jobCondition;

	/**
	 *岗位级别
	 */
	@Column(id = "job_rank", datatype = "string128")
	private java.lang.String jobRank;

	/**
	 *岗位授予人ID
	 */
	@Column(id = "owner_id", datatype = "string64")
	private java.lang.String ownerId;

	/**
	 * 设置岗位ID
	 */
	public void setJobId(java.lang.String jobId) {
		this.jobId = jobId;
	}

	/**
	 * 获取岗位ID
	 */
	public java.lang.String getJobId() {
		return jobId;
	}

	/**
	 * 设置岗位名称
	 */
	public void setJobName(java.lang.String jobName) {
		this.jobName = jobName;
	}

	/**
	 * 获取岗位名称
	 */
	public java.lang.String getJobName() {
		return jobName;
	}

	/**
	 * 设置岗位描述
	 */
	public void setJobDesc(java.lang.String jobDesc) {
		this.jobDesc = jobDesc;
	}

	/**
	 * 获取岗位描述
	 */
	public java.lang.String getJobDesc() {
		return jobDesc;
	}

	/**
	 * 设置岗位范围(0:通用岗位,1:机构特有)
	 */
	public void setJobScope(java.lang.Boolean jobScope) {
		this.jobScope = jobScope;
	}

	/**
	 * 获取岗位范围(0:通用岗位,1:机构特有)
	 */
	public java.lang.Boolean isJobScope() {
		return jobScope;
	}

	/**
	 * 设置岗位职责
	 */
	public void setJobFunction(java.lang.String jobFunction) {
		this.jobFunction = jobFunction;
	}

	/**
	 * 获取岗位职责
	 */
	public java.lang.String getJobFunction() {
		return jobFunction;
	}

	/**
	 * 设置岗位编制人数
	 */
	public void setJobAmount(java.lang.String jobAmount) {
		this.jobAmount = jobAmount;
	}

	/**
	 * 获取岗位编制人数
	 */
	public java.lang.String getJobAmount() {
		return jobAmount;
	}

	/**
	 * 设置岗位编号
	 */
	public void setJobNumber(java.lang.String jobNumber) {
		this.jobNumber = jobNumber;
	}

	/**
	 * 获取岗位编号
	 */
	public java.lang.String getJobNumber() {
		return jobNumber;
	}

	/**
	 * 设置任职条件
	 */
	public void setJobCondition(java.lang.String jobCondition) {
		this.jobCondition = jobCondition;
	}

	/**
	 * 获取任职条件
	 */
	public java.lang.String getJobCondition() {
		return jobCondition;
	}

	/**
	 * 设置岗位级别
	 */
	public void setJobRank(java.lang.String jobRank) {
		this.jobRank = jobRank;
	}

	/**
	 * 获取岗位级别
	 */
	public java.lang.String getJobRank() {
		return jobRank;
	}

	/**
	 * 设置岗位授予人ID
	 */
	public void setOwnerId(java.lang.String ownerId) {
		this.ownerId = ownerId;
	}

	/**
	 * 获取岗位授予人ID
	 */
	public java.lang.String getOwnerId() {
		return ownerId;
	}
}
