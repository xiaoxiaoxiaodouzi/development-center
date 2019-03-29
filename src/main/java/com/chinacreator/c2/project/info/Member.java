package com.chinacreator.c2.project.info;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 项目成员
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.info.Member", table = "c2_projectuser", ds = "dev-center")
public class Member implements Serializable {
	private static final long serialVersionUID = 1241993097428992L;
	/**
	 *序号
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *项目id
	 */
	@Column(id = "project_id", datatype = "mediumint")
	private java.lang.Integer projectId;

	/**
	 *用户id
	 */
	@Column(id = "user_id", datatype = "string64")
	private java.lang.String userId;

	/**
	 *岗位id
	 */
	@Column(id = "job_id", datatype = "string64")
	private java.lang.String jobId;

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
	 * 设置项目id
	 */
	public void setProjectId(java.lang.Integer projectId) {
		this.projectId = projectId;
	}

	/**
	 * 获取项目id
	 */
	public java.lang.Integer getProjectId() {
		return projectId;
	}

	/**
	 * 设置用户id
	 */
	public void setUserId(java.lang.String userId) {
		this.userId = userId;
	}

	/**
	 * 获取用户id
	 */
	public java.lang.String getUserId() {
		return userId;
	}

	/**
	 * 设置岗位id
	 */
	public void setJobId(java.lang.String jobId) {
		this.jobId = jobId;
	}

	/**
	 * 获取岗位id
	 */
	public java.lang.String getJobId() {
		return jobId;
	}
}
