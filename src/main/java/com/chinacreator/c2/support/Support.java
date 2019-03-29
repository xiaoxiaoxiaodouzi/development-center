package com.chinacreator.c2.support;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 任务支持单
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.support.support", table = "c2_community_support", ds = "dev-center")
public class Support implements Serializable {
	private static final long serialVersionUID = 1587143890452480L;
	/**
	 *主键
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "int")
	private java.lang.Integer id;

	/**
	 *标题
	 */
	@Column(id = "title", datatype = "string256")
	private java.lang.String title;

	/**
	 *申请人
	 */
	@Column(id = "user", datatype = "string256")
	private java.lang.String user;

	/**
	 *申请机构外键
	 */
	@Column(id = "org_id", datatype = "string256")
	private java.lang.String orgId;

	/**
	 *验收负责人
	 */
	@Column(id = "check_user", datatype = "string256")
	private java.lang.String checkUser;

	/**
	 *要求开始时间
	 */
	@Column(id = "ask_start_time", datatype = "timestamp")
	private java.sql.Timestamp askStartTime;

	/**
	 *要求结束时间
	 */
	@Column(id = "ask_end_time", datatype = "timestamp")
	private java.sql.Timestamp askEndTime;

	/**
	 *提交申请时间
	 */
	@Column(id = "creat_time", datatype = "timestamp")
	private java.sql.Timestamp creatTime;

	/**
	 *项目外键
	 */
	@Column(id = "project_id", datatype = "int")
	private java.lang.Integer projectId;

	/**
	 *执行机构外键
	 */
	@Column(id = "do_org_id", datatype = "string256")
	private java.lang.String doOrgId;

	/**
	 *执行负责人
	 */
	@Column(id = "manage_user", datatype = "string256")
	private java.lang.String manageUser;

	/**
	 *处理人
	 */
	@Column(id = "do_user", datatype = "string256")
	private java.lang.String doUser;

	/**
	 *接受申请时间
	 */
	@Column(id = "accept_time", datatype = "timestamp")
	private java.sql.Timestamp acceptTime;

	/**
	 *计划开始时间
	 */
	@Column(id = "plan_start_time", datatype = "timestamp")
	private java.sql.Timestamp planStartTime;

	/**
	 *计划结束时间
	 */
	@Column(id = "plan_end_time", datatype = "timestamp")
	private java.sql.Timestamp planEndTime;

	/**
	 *任务描述
	 */
	@Column(id = "description", datatype = "text")
	private java.lang.String description;

	/**
	 *执行情况
	 */
	@Column(id = "info", datatype = "text")
	private java.lang.String info;

	/**
	 *承包工作量
	 */
	@Column(id = "plan_workload", datatype = "int")
	private java.lang.Integer planWorkload;

	/**
	 *风险工作量
	 */
	@Column(id = "risk_workload", datatype = "int")
	private java.lang.Integer riskWorkload;

	/**
	 *实际工作量
	 */
	@Column(id = "actual_workload", datatype = "double")
	private java.lang.Double actualWorkload;

	/**
	 *结算工作量
	 */
	@Column(id = "account_workload", datatype = "double")
	private java.lang.Double accountWorkload;

	/**
	 *实际开始时间
	 */
	@Column(id = "actual_start_time", datatype = "timestamp")
	private java.sql.Timestamp actualStartTime;

	/**
	 *实际结束时间
	 */
	@Column(id = "actual_end_time", datatype = "timestamp")
	private java.sql.Timestamp actualEndTime;

	/**
	 *是否执行成功
	 */
	@Column(id = "success", datatype = "tinyint")
	private java.lang.Integer success;

	/**
	 *状态：0New；1Accept；2Done；3Close
	 */
	@Column(id = "state", datatype = "int")
	private java.lang.Integer state;

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
	 * 设置标题
	 */
	public void setTitle(java.lang.String title) {
		this.title = title;
	}

	/**
	 * 获取标题
	 */
	public java.lang.String getTitle() {
		return title;
	}

	/**
	 * 设置申请人
	 */
	public void setUser(java.lang.String user) {
		this.user = user;
	}

	/**
	 * 获取申请人
	 */
	public java.lang.String getUser() {
		return user;
	}

	/**
	 * 设置申请机构外键
	 */
	public void setOrgId(java.lang.String orgId) {
		this.orgId = orgId;
	}

	/**
	 * 获取申请机构外键
	 */
	public java.lang.String getOrgId() {
		return orgId;
	}

	/**
	 * 设置验收负责人
	 */
	public void setCheckUser(java.lang.String checkUser) {
		this.checkUser = checkUser;
	}

	/**
	 * 获取验收负责人
	 */
	public java.lang.String getCheckUser() {
		return checkUser;
	}

	/**
	 * 设置要求开始时间
	 */
	public void setAskStartTime(java.sql.Timestamp askStartTime) {
		this.askStartTime = askStartTime;
	}

	/**
	 * 获取要求开始时间
	 */
	public java.sql.Timestamp getAskStartTime() {
		return askStartTime;
	}

	/**
	 * 设置要求结束时间
	 */
	public void setAskEndTime(java.sql.Timestamp askEndTime) {
		this.askEndTime = askEndTime;
	}

	/**
	 * 获取要求结束时间
	 */
	public java.sql.Timestamp getAskEndTime() {
		return askEndTime;
	}

	/**
	 * 设置提交申请时间
	 */
	public void setCreatTime(java.sql.Timestamp creatTime) {
		this.creatTime = creatTime;
	}

	/**
	 * 获取提交申请时间
	 */
	public java.sql.Timestamp getCreatTime() {
		return creatTime;
	}

	/**
	 * 设置项目外键
	 */
	public void setProjectId(java.lang.Integer projectId) {
		this.projectId = projectId;
	}

	/**
	 * 获取项目外键
	 */
	public java.lang.Integer getProjectId() {
		return projectId;
	}

	/**
	 * 设置执行机构外键
	 */
	public void setDoOrgId(java.lang.String doOrgId) {
		this.doOrgId = doOrgId;
	}

	/**
	 * 获取执行机构外键
	 */
	public java.lang.String getDoOrgId() {
		return doOrgId;
	}

	/**
	 * 设置执行负责人
	 */
	public void setManageUser(java.lang.String manageUser) {
		this.manageUser = manageUser;
	}

	/**
	 * 获取执行负责人
	 */
	public java.lang.String getManageUser() {
		return manageUser;
	}

	/**
	 * 设置处理人
	 */
	public void setDoUser(java.lang.String doUser) {
		this.doUser = doUser;
	}

	/**
	 * 获取处理人
	 */
	public java.lang.String getDoUser() {
		return doUser;
	}

	/**
	 * 设置接受申请时间
	 */
	public void setAcceptTime(java.sql.Timestamp acceptTime) {
		this.acceptTime = acceptTime;
	}

	/**
	 * 获取接受申请时间
	 */
	public java.sql.Timestamp getAcceptTime() {
		return acceptTime;
	}

	/**
	 * 设置计划开始时间
	 */
	public void setPlanStartTime(java.sql.Timestamp planStartTime) {
		this.planStartTime = planStartTime;
	}

	/**
	 * 获取计划开始时间
	 */
	public java.sql.Timestamp getPlanStartTime() {
		return planStartTime;
	}

	/**
	 * 设置计划结束时间
	 */
	public void setPlanEndTime(java.sql.Timestamp planEndTime) {
		this.planEndTime = planEndTime;
	}

	/**
	 * 获取计划结束时间
	 */
	public java.sql.Timestamp getPlanEndTime() {
		return planEndTime;
	}

	/**
	 * 设置任务描述
	 */
	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	/**
	 * 获取任务描述
	 */
	public java.lang.String getDescription() {
		return description;
	}

	/**
	 * 设置执行情况
	 */
	public void setInfo(java.lang.String info) {
		this.info = info;
	}

	/**
	 * 获取执行情况
	 */
	public java.lang.String getInfo() {
		return info;
	}

	/**
	 * 设置承包工作量
	 */
	public void setPlanWorkload(java.lang.Integer planWorkload) {
		this.planWorkload = planWorkload;
	}

	/**
	 * 获取承包工作量
	 */
	public java.lang.Integer getPlanWorkload() {
		return planWorkload;
	}

	/**
	 * 设置风险工作量
	 */
	public void setRiskWorkload(java.lang.Integer riskWorkload) {
		this.riskWorkload = riskWorkload;
	}

	/**
	 * 获取风险工作量
	 */
	public java.lang.Integer getRiskWorkload() {
		return riskWorkload;
	}

	/**
	 * 设置实际工作量
	 */
	public void setActualWorkload(java.lang.Double actualWorkload) {
		this.actualWorkload = actualWorkload;
	}

	/**
	 * 获取实际工作量
	 */
	public java.lang.Double getActualWorkload() {
		return actualWorkload;
	}

	/**
	 * 设置结算工作量
	 */
	public void setAccountWorkload(java.lang.Double accountWorkload) {
		this.accountWorkload = accountWorkload;
	}

	/**
	 * 获取结算工作量
	 */
	public java.lang.Double getAccountWorkload() {
		return accountWorkload;
	}

	/**
	 * 设置实际开始时间
	 */
	public void setActualStartTime(java.sql.Timestamp actualStartTime) {
		this.actualStartTime = actualStartTime;
	}

	/**
	 * 获取实际开始时间
	 */
	public java.sql.Timestamp getActualStartTime() {
		return actualStartTime;
	}

	/**
	 * 设置实际结束时间
	 */
	public void setActualEndTime(java.sql.Timestamp actualEndTime) {
		this.actualEndTime = actualEndTime;
	}

	/**
	 * 获取实际结束时间
	 */
	public java.sql.Timestamp getActualEndTime() {
		return actualEndTime;
	}

	/**
	 * 设置是否执行成功
	 */
	public void setSuccess(java.lang.Integer success) {
		this.success = success;
	}

	/**
	 * 获取是否执行成功
	 */
	public java.lang.Integer getSuccess() {
		return success;
	}

	/**
	 * 设置状态：0New；1Accept；2Done；3Close
	 */
	public void setState(java.lang.Integer state) {
		this.state = state;
	}

	/**
	 * 获取状态：0New；1Accept；2Done；3Close
	 */
	public java.lang.Integer getState() {
		return state;
	}
}
