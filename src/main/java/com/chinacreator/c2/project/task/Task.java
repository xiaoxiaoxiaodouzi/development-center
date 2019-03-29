

package com.chinacreator.c2.project.task;

import java.util.Collection;
import com.chinacreator.c2.annotation.Children;
import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;
import com.chinacreator.c2.project.info.ProjectPlan;
import com.chinacreator.c2.project.label.LabelTask;

/**
 * 任务
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.task.task", table = "c2_comunity_task",
    ds = "dev-center", cache = false)
public class Task {
  /**
  *任务id
  */
  @Column(id = "id", type = ColumnType.increment, datatype = "int")
  private java.lang.Integer id;

  /**
  *需求id
  */
  @Column(id = "story_id", datatype = "int")
  private java.lang.Integer storyId;

  /**
  *
  */
  @Column(id = "bug_id", datatype = "int")
  private java.lang.Integer bugId;

  /**
  *项目
  */
  @Column(id = "project_id", datatype = "int")
  private java.lang.Integer projectId;

  /**
  *模块
  */
  @Column(id = "module_id", datatype = "int")
  private java.lang.Integer moduleId;

  /**
  *里程碑
  */
  @Column(id = "milestone_id", datatype = "int")
  private java.lang.Integer milestoneId;

  /**
  *任务名称
  */
  @Column(id = "name_", datatype = "string256")
  private java.lang.String name;

  /**
  *任务优先级
  */
  @Column(id = "pri", datatype = "tinyint")
  private java.lang.Integer pri;

  /**
  *预计工时
  */
  @Column(id = "estimate", datatype = "double")
  private java.lang.Double estimate;

  /**
  *已消耗工时
  */
  @Column(id = "consumed", datatype = "double")
  private java.lang.Double consumed;

  /**
  *剩余工时
  */
  @Column(id = "left_", datatype = "double")
  private java.lang.Double left;

  /**
  *截止日期
  */
  @Column(id = "deadline", datatype = "date")
  private java.sql.Date deadline;

  /**
  *任务是否完成
  */
  @Column(id = "is_done", datatype = "boolean")
  private java.lang.Boolean isDone;

  /**
  *任务是否完成
  */
  @Column(id = "closed", datatype = "boolean")
  private java.lang.Boolean closed;

  /**
  *任务描述
  */
  @Column(id = "description", datatype = "text")
  private java.lang.String description;

  /**
  *执行人
  */
  @Column(id = "assigned_to", datatype = "string64")
  private java.lang.String assignedTo;

  /**
  *预计起始时间
  */
  @Column(id = "est_start_date", datatype = "date")
  private java.sql.Date estStartDate;

  /**
  *完成人
  */
  @Column(id = "finished_by", datatype = "string64")
  private java.lang.String finishedBy;

  /**
  *完成时间
  */
  @Column(id = "finished_date", datatype = "date")
  private java.sql.Date finishedDate;

  /**
  *任务创建人
  */
  @Column(id = "creator", datatype = "string64")
  private java.lang.String creator;

  /**
  *创建时间
  */
  @Column(id = "create_date", datatype = "timestamp")
  private java.sql.Timestamp createDate;

  /**
  *
  */
  @Column(id = "task_type", datatype = "int")
  private java.lang.Integer taskType;

  /**
  *
  */
  @Column(id = "project_task_order", datatype = "int")
  private java.lang.Integer projectTaskOrder;

  /**
  *
  */
  @Column(id = "my_task_order", datatype = "int")
  private java.lang.Integer myTaskOrder;

  /**
  *阶段id
  */
  @Column(id = "stage_id", datatype = "int")
  private java.lang.Integer stageId;

  /**
  *任务是否确认
  */
  @Column(id = "is_confirm", datatype = "string5")
  private java.lang.String isConfirm;

  /**
  *任务确认人
  */
  @Column(id = "confirm_user", datatype = "string64")
  private java.lang.String confirmUser;

  /**
  *项目计划
  */
  @Column(id = "project_plan", association = true)
  private ProjectPlan projectPlan;

  /**
  *支持单id
  */
  @Column(id = "support_id", datatype = "int")
  private java.lang.Integer supportId;

  /**
  *标签任务关联
  */
  @Children(id = "labelTask", fk = "id:task_id")
  private Collection<LabelTask> labelTask;


  /**
  * 设置任务id
  */
  public void setId(java.lang.Integer id) {
    this.id = id;
  }

  /**
  * 获取任务id
  */
  public java.lang.Integer getId() {
    return id;
  }

  /**
  * 设置需求id
  */
  public void setStoryId(java.lang.Integer storyId) {
    this.storyId = storyId;
  }

  /**
  * 获取需求id
  */
  public java.lang.Integer getStoryId() {
    return storyId;
  }

  /**
  * 设置
  */
  public void setBugId(java.lang.Integer bugId) {
    this.bugId = bugId;
  }

  /**
  * 获取
  */
  public java.lang.Integer getBugId() {
    return bugId;
  }

  /**
  * 设置项目
  */
  public void setProjectId(java.lang.Integer projectId) {
    this.projectId = projectId;
  }

  /**
  * 获取项目
  */
  public java.lang.Integer getProjectId() {
    return projectId;
  }

  /**
  * 设置模块
  */
  public void setModuleId(java.lang.Integer moduleId) {
    this.moduleId = moduleId;
  }

  /**
  * 获取模块
  */
  public java.lang.Integer getModuleId() {
    return moduleId;
  }

  /**
  * 设置里程碑
  */
  public void setMilestoneId(java.lang.Integer milestoneId) {
    this.milestoneId = milestoneId;
  }

  /**
  * 获取里程碑
  */
  public java.lang.Integer getMilestoneId() {
    return milestoneId;
  }

  /**
  * 设置任务名称
  */
  public void setName(java.lang.String name) {
    this.name = name;
  }

  /**
  * 获取任务名称
  */
  public java.lang.String getName() {
    return name;
  }

  /**
  * 设置任务优先级
  */
  public void setPri(java.lang.Integer pri) {
    this.pri = pri;
  }

  /**
  * 获取任务优先级
  */
  public java.lang.Integer getPri() {
    return pri;
  }

  /**
  * 设置预计工时
  */
  public void setEstimate(java.lang.Double estimate) {
    this.estimate = estimate;
  }

  /**
  * 获取预计工时
  */
  public java.lang.Double getEstimate() {
    return estimate;
  }

  /**
  * 设置已消耗工时
  */
  public void setConsumed(java.lang.Double consumed) {
    this.consumed = consumed;
  }

  /**
  * 获取已消耗工时
  */
  public java.lang.Double getConsumed() {
    return consumed;
  }

  /**
  * 设置剩余工时
  */
  public void setLeft(java.lang.Double left) {
    this.left = left;
  }

  /**
  * 获取剩余工时
  */
  public java.lang.Double getLeft() {
    return left;
  }

  /**
  * 设置截止日期
  */
  public void setDeadline(java.sql.Date deadline) {
    this.deadline = deadline;
  }

  /**
  * 获取截止日期
  */
  public java.sql.Date getDeadline() {
    return deadline;
  }

  /**
  * 设置任务是否完成
  */
  public void setIsDone(java.lang.Boolean isDone) {
    this.isDone = isDone;
  }

  /**
  * 获取任务是否完成
  */
  public java.lang.Boolean isIsDone() {
    return isDone;
  }

  /**
  * 设置任务是否完成
  */
  public void setClosed(java.lang.Boolean closed) {
    this.closed = closed;
  }

  /**
  * 获取任务是否完成
  */
  public java.lang.Boolean isClosed() {
    return closed;
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
  * 设置执行人
  */
  public void setAssignedTo(java.lang.String assignedTo) {
    this.assignedTo = assignedTo;
  }

  /**
  * 获取执行人
  */
  public java.lang.String getAssignedTo() {
    return assignedTo;
  }

  /**
  * 设置预计起始时间
  */
  public void setEstStartDate(java.sql.Date estStartDate) {
    this.estStartDate = estStartDate;
  }

  /**
  * 获取预计起始时间
  */
  public java.sql.Date getEstStartDate() {
    return estStartDate;
  }

  /**
  * 设置完成人
  */
  public void setFinishedBy(java.lang.String finishedBy) {
    this.finishedBy = finishedBy;
  }

  /**
  * 获取完成人
  */
  public java.lang.String getFinishedBy() {
    return finishedBy;
  }

  /**
  * 设置完成时间
  */
  public void setFinishedDate(java.sql.Date finishedDate) {
    this.finishedDate = finishedDate;
  }

  /**
  * 获取完成时间
  */
  public java.sql.Date getFinishedDate() {
    return finishedDate;
  }

  /**
  * 设置任务创建人
  */
  public void setCreator(java.lang.String creator) {
    this.creator = creator;
  }

  /**
  * 获取任务创建人
  */
  public java.lang.String getCreator() {
    return creator;
  }

  /**
  * 设置创建时间
  */
  public void setCreateDate(java.sql.Timestamp createDate) {
    this.createDate = createDate;
  }

  /**
  * 获取创建时间
  */
  public java.sql.Timestamp getCreateDate() {
    return createDate;
  }

  /**
  * 设置
  */
  public void setTaskType(java.lang.Integer taskType) {
    this.taskType = taskType;
  }

  /**
  * 获取
  */
  public java.lang.Integer getTaskType() {
    return taskType;
  }

  /**
  * 设置
  */
  public void setProjectTaskOrder(java.lang.Integer projectTaskOrder) {
    this.projectTaskOrder = projectTaskOrder;
  }

  /**
  * 获取
  */
  public java.lang.Integer getProjectTaskOrder() {
    return projectTaskOrder;
  }

  /**
  * 设置
  */
  public void setMyTaskOrder(java.lang.Integer myTaskOrder) {
    this.myTaskOrder = myTaskOrder;
  }

  /**
  * 获取
  */
  public java.lang.Integer getMyTaskOrder() {
    return myTaskOrder;
  }

  /**
  * 设置阶段id
  */
  public void setStageId(java.lang.Integer stageId) {
    this.stageId = stageId;
  }

  /**
  * 获取阶段id
  */
  public java.lang.Integer getStageId() {
    return stageId;
  }

  /**
  * 设置任务是否确认
  */
  public void setIsConfirm(java.lang.String isConfirm) {
    this.isConfirm = isConfirm;
  }

  /**
  * 获取任务是否确认
  */
  public java.lang.String getIsConfirm() {
    return isConfirm;
  }

  /**
  * 设置任务确认人
  */
  public void setConfirmUser(java.lang.String confirmUser) {
    this.confirmUser = confirmUser;
  }

  /**
  * 获取任务确认人
  */
  public java.lang.String getConfirmUser() {
    return confirmUser;
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

  /**
  * 设置支持单id
  */
  public void setSupportId(java.lang.Integer supportId) {
    this.supportId = supportId;
  }

  /**
  * 获取支持单id
  */
  public java.lang.Integer getSupportId() {
    return supportId;
  }

  /**
  * 设置标签任务关联
  */
  public void setLabelTask(Collection<LabelTask> labelTask) {
    this.labelTask = labelTask;
  }

  /**
  * 获取标签任务关联
  */
  public Collection<LabelTask> getLabelTask() {
    return labelTask;
  }
}
