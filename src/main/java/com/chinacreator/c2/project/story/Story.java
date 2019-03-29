package com.chinacreator.c2.project.story;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;
import com.chinacreator.c2.project.info.Module;
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.project.milestone.Milestone;

/** 
 * 需求
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.story.Story", table = "zt_story", ds = "", cache = false)
public class Story {
	/** 
	* id
	*/
	@Column(id = "id", type = ColumnType.increment, datatype = "int")
	private java.lang.Integer id;
	/** 
	* 关联项目
	*/
	@Column(id = "project", association = true)
	private Project project;
	/** 
	* 关联模块
	*/
	@Column(id = "module", association = true)
	private Module module;
	/** 
	* 所属里程碑
	*/
	@Column(id = "milestone", association = true)
	private Milestone milestone;
	/** 
	* 标题
	*/
	@Column(id = "title", datatype = "string256")
	private java.lang.String title;
	/** 
	* 优先级
	*/
	@Column(id = "pri", datatype = "bigdecimal")
	private java.math.BigDecimal pri;
	/** 
	* 正常，关闭，确认
	*/
	@Column(id = "status_", datatype = "char1")
	private java.lang.String status;
	/** 
	* 创建人
	*/
	@Column(id = "openedby", datatype = "string64")
	private java.lang.String openedby;
	/** 
	* 创建时间
	*/
	@Column(id = "openeddate", datatype = "timestamp")
	private java.sql.Timestamp openeddate;
	/** 
	* 需求定义
	*/
	@Column(id = "spec", datatype = "text")
	private java.lang.String spec;
	/** 
	* 负责人
	*/
	@Column(id = "assigned_to", datatype = "string256")
	private java.lang.String assignedTo;
	/** 
	* 排序号
	*/
	@Column(id = "order_no", datatype = "int")
	private java.lang.Integer orderNo;
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
	* 功能，用户体验
	*/
	@Column(id = "type", datatype = "char1")
	private java.lang.String type;
	/** 
	* 作为
	*/
	@Column(id = "mainStory", datatype = "text")
	private java.lang.String mainStory;
	/** 
	* 我想要
	*/
	@Column(id = "storyFunction", datatype = "text")
	private java.lang.String storyFunction;
	/** 
	* 以便于
	*/
	@Column(id = "storyEffect", datatype = "text")
	private java.lang.String storyEffect;
	/** 
	* 提出人
	*/
	@Column(id = "proposer", datatype = "string256")
	private java.lang.String proposer;
	/** 
	* 完成度
	*/
	@Column(id = "completes", datatype = "char1")
	private java.lang.String completes;

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
	* 设置关联项目
	*/
	public void setProject(Project project) {
		this.project = project;
	}

	/** 
	* 获取关联项目
	*/
	public Project getProject() {
		return project;
	}

	/** 
	* 设置关联模块
	*/
	public void setModule(Module module) {
		this.module = module;
	}

	/** 
	* 获取关联模块
	*/
	public Module getModule() {
		return module;
	}

	/** 
	* 设置所属里程碑
	*/
	public void setMilestone(Milestone milestone) {
		this.milestone = milestone;
	}

	/** 
	* 获取所属里程碑
	*/
	public Milestone getMilestone() {
		return milestone;
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
	* 设置优先级
	*/
	public void setPri(java.math.BigDecimal pri) {
		this.pri = pri;
	}

	/** 
	* 获取优先级
	*/
	public java.math.BigDecimal getPri() {
		return pri;
	}

	/** 
	* 设置正常，关闭，确认
	*/
	public void setStatus(java.lang.String status) {
		this.status = status;
	}

	/** 
	* 获取正常，关闭，确认
	*/
	public java.lang.String getStatus() {
		return status;
	}

	/** 
	* 设置创建人
	*/
	public void setOpenedby(java.lang.String openedby) {
		this.openedby = openedby;
	}

	/** 
	* 获取创建人
	*/
	public java.lang.String getOpenedby() {
		return openedby;
	}

	/** 
	* 设置创建时间
	*/
	public void setOpeneddate(java.sql.Timestamp openeddate) {
		this.openeddate = openeddate;
	}

	/** 
	* 获取创建时间
	*/
	public java.sql.Timestamp getOpeneddate() {
		return openeddate;
	}

	/** 
	* 设置需求定义
	*/
	public void setSpec(java.lang.String spec) {
		this.spec = spec;
	}

	/** 
	* 获取需求定义
	*/
	public java.lang.String getSpec() {
		return spec;
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
	* 设置排序号
	*/
	public void setOrderNo(java.lang.Integer orderNo) {
		this.orderNo = orderNo;
	}

	/** 
	* 获取排序号
	*/
	public java.lang.Integer getOrderNo() {
		return orderNo;
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
	* 设置功能，用户体验
	*/
	public void setType(java.lang.String type) {
		this.type = type;
	}

	/** 
	* 获取功能，用户体验
	*/
	public java.lang.String getType() {
		return type;
	}

	/** 
	* 设置作为
	*/
	public void setMainStory(java.lang.String mainStory) {
		this.mainStory = mainStory;
	}

	/** 
	* 获取作为
	*/
	public java.lang.String getMainStory() {
		return mainStory;
	}

	/** 
	* 设置我想要
	*/
	public void setStoryFunction(java.lang.String storyFunction) {
		this.storyFunction = storyFunction;
	}

	/** 
	* 获取我想要
	*/
	public java.lang.String getStoryFunction() {
		return storyFunction;
	}

	/** 
	* 设置以便于
	*/
	public void setStoryEffect(java.lang.String storyEffect) {
		this.storyEffect = storyEffect;
	}

	/** 
	* 获取以便于
	*/
	public java.lang.String getStoryEffect() {
		return storyEffect;
	}

	/** 
	* 设置提出人
	*/
	public void setProposer(java.lang.String proposer) {
		this.proposer = proposer;
	}

	/** 
	* 获取提出人
	*/
	public java.lang.String getProposer() {
		return proposer;
	}

	/** 
	* 设置完成度
	*/
	public void setCompletes(java.lang.String completes) {
		this.completes = completes;
	}

	/** 
	* 获取完成度
	*/
	public java.lang.String getCompletes() {
		return completes;
	}
}
