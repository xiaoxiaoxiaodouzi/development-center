package com.chinacreator.c2.project.info;

import java.util.List;
import java.util.Map;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;
import com.chinacreator.c2.project.task.Task;

/**
 * 项目计划
 * 
 * @author
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.info.ProjectPlan", table = "zt_project_plan", ds = "", cache = false, logicDelete = true)
public class ProjectPlan {
	/**
	 * id
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "int")
	private java.lang.Integer id;
	private Map<String, Object> assigned;
	private List<Task> taskList;
	private String completes;
	private Map<String, Object> taskNum;
	/**
	 * 项目ID
	 */
	@Column(id = "project_id", association = true)
	private Project projectId;
	/**
	 * 名称
	 */
	@Column(id = "name", datatype = "string64")
	private java.lang.String name;
	/**
	 * 上级模块
	 */
	@Column(id = "parent", datatype = "int")
	private java.lang.Integer parent;
	/**
	 * 排序
	 */
	@Column(id = "order_", datatype = "smallint")
	private java.lang.Integer order;
	/**
	 * 逻辑删除
	 */
	@Column(id = "c2_logicdelete_flag", datatype = "char1")
	private java.lang.String c2LogicdeleteFlag;
	/**
	 * 开始时间
	 */
	@Column(id = "start_date", datatype = "timestamp")
	private java.sql.Timestamp startDate;
	/**
	 * 预计完成时间
	 */
	@Column(id = "end_date", datatype = "timestamp")
	private java.sql.Timestamp endDate;
	/**
	 * 负责人
	 */
	@Column(id = "assigned_to", datatype = "string256")
	private java.lang.String assignedTo;
	/**
	 * 描述
	 */
	@Column(id = "description", datatype = "string256")
	private java.lang.String description;
	/**
	 * 排序号
	 */
	@Column(id = "sn", datatype = "bigint")
	private java.math.BigInteger sn;
	/**
	 * 重要程度('0','1','2','重要','一般','可拖延')
	 */
	@Column(id = "level", datatype = "char1")
	private java.lang.String level;

	/**
	 * 设置id
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取id
	 */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * 设置项目ID
	 */
	public void setProjectId(Project projectId) {
		this.projectId = projectId;
	}

	/**
	 * 获取项目ID
	 */
	public Project getProjectId() {
		return projectId;
	}

	/**
	 * 设置名称
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * 获取名称
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * 设置上级模块
	 */
	public void setParent(java.lang.Integer parent) {
		this.parent = parent;
	}

	/**
	 * 获取上级模块
	 */
	public java.lang.Integer getParent() {
		return parent;
	}

	/**
	 * 设置排序
	 */
	public void setOrder(java.lang.Integer order) {
		this.order = order;
	}

	/**
	 * 获取排序
	 */
	public java.lang.Integer getOrder() {
		return order;
	}

	/**
	 * 设置逻辑删除
	 */
	public void setC2LogicdeleteFlag(java.lang.String c2LogicdeleteFlag) {
		this.c2LogicdeleteFlag = c2LogicdeleteFlag;
	}

	/**
	 * 获取逻辑删除
	 */
	public java.lang.String getC2LogicdeleteFlag() {
		return c2LogicdeleteFlag;
	}

	/**
	 * 设置开始时间
	 */
	public void setStartDate(java.sql.Timestamp startDate) {
		this.startDate = startDate;
	}

	/**
	 * 获取开始时间
	 */
	public java.sql.Timestamp getStartDate() {
		return startDate;
	}

	/**
	 * 设置预计完成时间
	 */
	public void setEndDate(java.sql.Timestamp endDate) {
		this.endDate = endDate;
	}

	/**
	 * 获取预计完成时间
	 */
	public java.sql.Timestamp getEndDate() {
		return endDate;
	}

	/**
	 * 设置负责人
	 */
	public void setAssignedTo(java.lang.String assignedTo) {
		this.assignedTo = assignedTo;
	}

	/**
	 * 获取负责人
	 */
	public java.lang.String getAssignedTo() {
		return assignedTo;
	}

	/**
	 * 设置描述
	 */
	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	/**
	 * 获取描述
	 */
	public java.lang.String getDescription() {
		return description;
	}

	/**
	 * 设置排序号
	 */
	public void setSn(java.math.BigInteger sn) {
		this.sn = sn;
	}

	/**
	 * 获取排序号
	 */
	public java.math.BigInteger getSn() {
		return sn;
	}

	/**
	 * 设置重要程度('0','1','2','重要','一般','可拖延')
	 */
	public void setLevel(java.lang.String level) {
		this.level = level;
	}

	/**
	 * 获取重要程度('0','1','2','重要','一般','可拖延')
	 */
	public java.lang.String getLevel() {
		return level;
	}

	public Map<String, Object> getAssigned() {
		return assigned;
	}

	public void setAssigned(Map<String, Object> assigned) {
		this.assigned = assigned;
	}

	public void setTaskList(List<Task> taskList) {
		this.taskList = taskList;
	}

	public List<Task> getTaskList() {
		return taskList;
	}

	public void setTaskNum(Map<String, Object> taskNum) {
		this.taskNum = taskNum;
	}

	public Map<String, Object> getTaskNum() {
		return this.taskNum;
	}

	public void setCompletes(String completes) {
		this.completes = completes;
	}

	public String getCompletes() {
		return this.completes;
	}
}
