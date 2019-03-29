package com.chinacreator.c2.project.bug;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;
import com.chinacreator.c2.project.info.Module;
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.project.info.ProjectPlan;
import com.chinacreator.c2.project.milestone.Milestone;

/** 
 * bug
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.bug.Bug", table = "c2_community_bug", ds = "", cache = false)
public class Bug {
	/** 
	* id
	*/
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;
	/** 
	* bug编号
	*/
	@Column(id = "no", datatype = "mediumint")
	private java.lang.Integer no;
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
	* 关联里程碑
	*/
	@Column(id = "milestone", association = true)
	private Milestone milestone;
	/** 
	* 标题
	*/
	@Column(id = "title", datatype = "string256")
	private java.lang.String title;
	/** 
	* 版本
	*/
	@Column(id = "bug_version", datatype = "string256")
	private java.lang.String bugVersion;
	/** 
	* 优先级
	*/
	@Column(id = "pri", datatype = "mediumint")
	private java.lang.Integer pri;
	/** 
	* 0关闭、1创建，2打开、3完成、4延后
	*/
	@Column(id = "status_", datatype = "int")
	private java.lang.Integer status;
	/** 
	*/
	@Column(id = "init_assigned_to", datatype = "string64")
	private java.lang.String initAssignedTo;
	/** 
	* 创建时间
	*/
	@Column(id = "create_time", datatype = "timestamp")
	private java.sql.Timestamp createTime;
	/** 
	* 创建人
	*/
	@Column(id = "create_by", datatype = "string64")
	private java.lang.String createBy;
	/** 
	* 最后修改时间
	*/
	@Column(id = "last_update_time", datatype = "timestamp")
	private java.sql.Timestamp lastUpdateTime;
	/** 
	* 问题描述
	*/
	@Column(id = "spec", datatype = "text")
	private java.lang.String spec;
	/** 
	* 排序号
	*/
	@Column(id = "order_no", datatype = "int")
	private java.lang.Integer orderNo;
	/** 
	* 项目计划
	*/
	@Column(id = "projectPlan", association = true)
	private ProjectPlan projectPlan;

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
	* 设置bug编号
	*/
	public void setNo(java.lang.Integer no) {
		this.no = no;
	}

	/** 
	* 获取bug编号
	*/
	public java.lang.Integer getNo() {
		return no;
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
	* 设置关联里程碑
	*/
	public void setMilestone(Milestone milestone) {
		this.milestone = milestone;
	}

	/** 
	* 获取关联里程碑
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
	* 设置版本
	*/
	public void setBugVersion(java.lang.String bugVersion) {
		this.bugVersion = bugVersion;
	}

	/** 
	* 获取版本
	*/
	public java.lang.String getBugVersion() {
		return bugVersion;
	}

	/** 
	* 设置优先级
	*/
	public void setPri(java.lang.Integer pri) {
		this.pri = pri;
	}

	/** 
	* 获取优先级
	*/
	public java.lang.Integer getPri() {
		return pri;
	}

	/** 
	* 设置0关闭、1创建，2打开、3完成、4延后
	*/
	public void setStatus(java.lang.Integer status) {
		this.status = status;
	}

	/** 
	* 获取0关闭、1创建，2打开、3完成、4延后
	*/
	public java.lang.Integer getStatus() {
		return status;
	}

	/** 
	* 设置
	*/
	public void setInitAssignedTo(java.lang.String initAssignedTo) {
		this.initAssignedTo = initAssignedTo;
	}

	/** 
	* 获取
	*/
	public java.lang.String getInitAssignedTo() {
		return initAssignedTo;
	}

	/** 
	* 设置创建时间
	*/
	public void setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
	}

	/** 
	* 获取创建时间
	*/
	public java.sql.Timestamp getCreateTime() {
		return createTime;
	}

	/** 
	* 设置创建人
	*/
	public void setCreateBy(java.lang.String createBy) {
		this.createBy = createBy;
	}

	/** 
	* 获取创建人
	*/
	public java.lang.String getCreateBy() {
		return createBy;
	}

	/** 
	* 设置最后修改时间
	*/
	public void setLastUpdateTime(java.sql.Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	/** 
	* 获取最后修改时间
	*/
	public java.sql.Timestamp getLastUpdateTime() {
		return lastUpdateTime;
	}

	/** 
	* 设置问题描述
	*/
	public void setSpec(java.lang.String spec) {
		this.spec = spec;
	}

	/** 
	* 获取问题描述
	*/
	public java.lang.String getSpec() {
		return spec;
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
	* 设置项目计划
	*/
	public void setProjectPlan(ProjectPlan projectPlan) {
		this.projectPlan = projectPlan;
	}

	/** 
	* 获取项目计划
	*/
	public ProjectPlan getProjectPlan() {
		return projectPlan;
	}
}
