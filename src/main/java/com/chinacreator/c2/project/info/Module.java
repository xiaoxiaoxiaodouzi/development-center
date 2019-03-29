package com.chinacreator.c2.project.info;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.chinacreator.c2.annotation.Children;
import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 模块
 * 
 * @author
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.info.Module", table = "zt_module", ds = "", cache = false, logicDelete = true)
public class Module {
	/**
	 * id
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "int")
	private java.lang.Integer id;
	/** 
	*/
	private static final long serialVersionUID = 1L;
	/**
	 * 项目ID
	 */
	@Column(id = "project_id", association = true)
	private Project projectId;
	/**
	 * 名称
	 */
	@Column(id = "name_", datatype = "string64")
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
	@Column(id = "c2_logicdelete_flag", datatype = "boolean")
	private java.lang.Boolean c2LogicdeleteFlag;
	/**
	 * 开始时间
	 */
	@Column(id = "startDate", datatype = "timestamp")
	private java.sql.Timestamp startDate;
	/**
	 * 预计完成时间
	 */
	@Column(id = "endDate", datatype = "timestamp")
	private java.sql.Timestamp endDate;
	/**
	 * 负责人
	 */
	@Column(id = "assigned_to", datatype = "string256")
	private java.lang.String assignedTo;
	/**
	 * 模块
	 */
	@Children(id = "modules", fk = "id:parent")
	private Collection<Module> modules;

	private String completes;

	private List<Map<String, Object>> storyList;
	private Map<String, Object> assigned;

	private Integer taskNum;

	private Integer storyNum;
	private Integer bugNum;
	private Integer closeNum;

	public Integer getCloseNum() {
		return closeNum;
	}

	public void setCloseNum(Integer closeNum) {
		this.closeNum = closeNum;
	}

	public Integer getBugNum() {
		return bugNum;
	}

	public void setBugNum(Integer bugNum) {
		this.bugNum = bugNum;
	}

	private List<Integer> storyIds;

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

	/*
	 * 设置项目ID
	 */
	public void setProjectId(Project projectId) {
		this.projectId = projectId;
	}

	/*
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
	public void setC2LogicdeleteFlag(java.lang.Boolean c2LogicdeleteFlag) {
		this.c2LogicdeleteFlag = c2LogicdeleteFlag;
	}

	/**
	 * 获取逻辑删除
	 */
	public java.lang.Boolean isC2LogicdeleteFlag() {
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
	 * 设置模块
	 */
	public void setModules(Collection<Module> modules) {
		this.modules = modules;
	}

	/**
	 * 获取模块
	 */
	public Collection<Module> getModules() {
		return modules;
	}

	/*
	 * 
	 * 获取完成度
	 */
	public void setCompletes(String completes) {
		this.completes = completes;
	}

	public String getCompletes() {
		return completes;
	}

	public void setStoryList(List<Map<String, Object>> storyList) {
		this.storyList = storyList;
	}

	public List<Map<String, Object>> getStoryList() {
		return storyList;
	}

	public void setAssigned(Map<String, Object> assigned) {
		this.assigned = assigned;
	}

	public Map<String, Object> getAssigned() {
		return assigned;
	}

	public Integer getTaskNum() {
		return taskNum;
	}

	public void setTaskNum(Integer taskNum) {
		this.taskNum = taskNum;
	}

	public Integer getStoryNum() {
		return storyNum;
	}

	public void setStoryNum(Integer storyNum) {
		this.storyNum = storyNum;
	}

	public List<Integer> getStoryIds() {
		return storyIds;
	}

	public void setStoryIds(List<Integer> storyIds) {
		this.storyIds = storyIds;
	}

}
