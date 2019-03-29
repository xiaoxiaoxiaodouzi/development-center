package com.chinacreator.c2.project.plan;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 实例步骤结果
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.plan.StepResult", table = "c2_community_instance_step_result", ds = "dev-center")
public class StepResult implements Serializable {
	private static final long serialVersionUID = 2076730227752960L;
	/**
	 *
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "int")
	private java.lang.Integer id;

	/**
	 *实例步骤id
	 */
	@Column(id = "step_id", datatype = "int")
	private java.lang.Integer stepId;

	/**
	 *实例id
	 */
	@Column(id = "instance_id", datatype = "int")
	private java.lang.Integer instanceId;

	/**
	 *实际结果
	 */
	@Column(id = "result", datatype = "string256")
	private java.lang.String result;

	/**
	 *创建人
	 */
	@Column(id = "user_name", datatype = "string64")
	private java.lang.String userName;

	/**
	 *创建时间
	 */
	@Column(id = "ctime", datatype = "timestamp")
	private java.sql.Timestamp ctime;

	/**
	 *备注
	 */
	@Column(id = "remark", datatype = "string256")
	private java.lang.String remark;

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
	 * 设置实例步骤id
	 */
	public void setStepId(java.lang.Integer stepId) {
		this.stepId = stepId;
	}

	/**
	 * 获取实例步骤id
	 */
	public java.lang.Integer getStepId() {
		return stepId;
	}

	/**
	 * 设置实例id
	 */
	public void setInstanceId(java.lang.Integer instanceId) {
		this.instanceId = instanceId;
	}

	/**
	 * 获取实例id
	 */
	public java.lang.Integer getInstanceId() {
		return instanceId;
	}

	/**
	 * 设置实际结果
	 */
	public void setResult(java.lang.String result) {
		this.result = result;
	}

	/**
	 * 获取实际结果
	 */
	public java.lang.String getResult() {
		return result;
	}

	/**
	 * 设置创建人
	 */
	public void setUserName(java.lang.String userName) {
		this.userName = userName;
	}

	/**
	 * 获取创建人
	 */
	public java.lang.String getUserName() {
		return userName;
	}

	/**
	 * 设置创建时间
	 */
	public void setCtime(java.sql.Timestamp ctime) {
		this.ctime = ctime;
	}

	/**
	 * 获取创建时间
	 */
	public java.sql.Timestamp getCtime() {
		return ctime;
	}

	/**
	 * 设置备注
	 */
	public void setRemark(java.lang.String remark) {
		this.remark = remark;
	}

	/**
	 * 获取备注
	 */
	public java.lang.String getRemark() {
		return remark;
	}
}
