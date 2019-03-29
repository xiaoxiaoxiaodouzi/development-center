

package com.chinacreator.c2.team;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 团队任务快照
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.team.TeamTaskSnapshot",
    table = "c2_comunity_team_task_snapshot", ds = "dev-center", cache = false)
public class TeamTaskSnapshot {
  /**
  *快照id
  */
  @Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
  private java.lang.Integer id;

  /**
  *周报id
  */
  @Column(id = "report_id", datatype = "mediumint")
  private java.lang.Integer reportId;

  /**
  *关联的任务
  */
  @Column(id = "task_id", datatype = "mediumint")
  private java.lang.Integer taskId;

  /**
  *
  */
  @Column(id = "left_", datatype = "smalldouble")
  private java.lang.Double left;

  /**
  *预计工时
  */
  @Column(id = "estimate", datatype = "smalldouble")
  private java.lang.Double estimate;

  /**
  *已消耗工时
  */
  @Column(id = "consumed", datatype = "smalldouble")
  private java.lang.Double consumed;

  /**
  *截止时间
  */
  @Column(id = "deadline", datatype = "date")
  private java.sql.Date deadline;

  /**
  *实际开始时间
  */
  @Column(id = "est_start_date", datatype = "date")
  private java.sql.Date estStartDate;

  /**
  *负责人
  */
  @Column(id = "assigned_to", datatype = "string32")
  private java.lang.String assignedTo;

  /**
  *取快照时的状态
  */
  @Column(id = "is_done", datatype = "boolean")
  private java.lang.Boolean isDone;

  /**
  *延期原因
  */
  @Column(id = "reason", datatype = "string64")
  private java.lang.String reason;

  /**
  *处理方式
  */
  @Column(id = "handle", datatype = "string64")
  private java.lang.String handle;

  /**
  *本周消耗工时
  */
  @Column(id = "worked", datatype = "smalldouble")
  private java.lang.Double worked;

  /**
  *
  */
  @Column(id = "name_", datatype = "string256")
  private java.lang.String name;

  /**
  *任务状态，1：正常任务，2：跨周任务，3：逾期任务
  */
  @Column(id = "status_", datatype = "char1")
  private java.lang.String status;

  /**
  *
  */
  @Column(id = "belong_to_next_week", datatype = "boolean")
  private java.lang.Boolean belongToNextWeek;

  /**
  *需求ID
  */
  @Column(id = "story_id", datatype = "mediumint")
  private java.lang.Integer storyId;

  private String userRealname;
  
  private String userIcon;
  
  private String projectName;
  
  private String projectId;
  
  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getUserRealname() {
    return userRealname;
  }

  public void setUserRealname(String userRealname) {
    this.userRealname = userRealname;
  }

  public String getUserIcon() {
    return userIcon;
  }

  public void setUserIcon(String userIcon) {
    this.userIcon = userIcon;
  }

  /**
  * 设置快照id
  */
  public void setId(java.lang.Integer id) {
    this.id = id;
  }

  /**
  * 获取快照id
  */
  public java.lang.Integer getId() {
    return id;
  }

  /**
  * 设置周报id
  */
  public void setReportId(java.lang.Integer reportId) {
    this.reportId = reportId;
  }

  /**
  * 获取周报id
  */
  public java.lang.Integer getReportId() {
    return reportId;
  }

  /**
  * 设置关联的任务
  */
  public void setTaskId(java.lang.Integer taskId) {
    this.taskId = taskId;
  }

  /**
  * 获取关联的任务
  */
  public java.lang.Integer getTaskId() {
    return taskId;
  }

  /**
  * 设置
  */
  public void setLeft(java.lang.Double left) {
    this.left = left;
  }

  /**
  * 获取
  */
  public java.lang.Double getLeft() {
    return left;
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
  * 设置截止时间
  */
  public void setDeadline(java.sql.Date deadline) {
    this.deadline = deadline;
  }

  /**
  * 获取截止时间
  */
  public java.sql.Date getDeadline() {
    return deadline;
  }

  /**
  * 设置实际开始时间
  */
  public void setEstStartDate(java.sql.Date estStartDate) {
    this.estStartDate = estStartDate;
  }

  /**
  * 获取实际开始时间
  */
  public java.sql.Date getEstStartDate() {
    return estStartDate;
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
  * 设置取快照时的状态
  */
  public void setIsDone(java.lang.Boolean isDone) {
    this.isDone = isDone;
  }

  /**
  * 获取取快照时的状态
  */
  public java.lang.Boolean isIsDone() {
    return isDone;
  }

  /**
  * 设置延期原因
  */
  public void setReason(java.lang.String reason) {
    this.reason = reason;
  }

  /**
  * 获取延期原因
  */
  public java.lang.String getReason() {
    return reason;
  }

  /**
  * 设置处理方式
  */
  public void setHandle(java.lang.String handle) {
    this.handle = handle;
  }

  /**
  * 获取处理方式
  */
  public java.lang.String getHandle() {
    return handle;
  }

  /**
  * 设置本周消耗工时
  */
  public void setWorked(java.lang.Double worked) {
    this.worked = worked;
  }

  /**
  * 获取本周消耗工时
  */
  public java.lang.Double getWorked() {
    return worked;
  }

  /**
  * 设置
  */
  public void setName(java.lang.String name) {
    this.name = name;
  }

  /**
  * 获取
  */
  public java.lang.String getName() {
    return name;
  }

  /**
  * 设置任务状态，1：正常任务，2：跨周任务，3：逾期任务
  */
  public void setStatus(java.lang.String status) {
    this.status = status;
  }

  /**
  * 获取任务状态，1：正常任务，2：跨周任务，3：逾期任务
  */
  public java.lang.String getStatus() {
    return status;
  }

  /**
  * 设置
  */
  public void setBelongToNextWeek(java.lang.Boolean belongToNextWeek) {
    this.belongToNextWeek = belongToNextWeek;
  }

  /**
  * 获取
  */
  public java.lang.Boolean isBelongToNextWeek() {
    return belongToNextWeek;
  }

  /**
  * 设置需求ID
  */
  public void setStoryId(java.lang.Integer storyId) {
    this.storyId = storyId;
  }

  /**
  * 获取需求ID
  */
  public java.lang.Integer getStoryId() {
    return storyId;
  }
}
