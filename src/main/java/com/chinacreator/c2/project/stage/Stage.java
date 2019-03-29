package com.chinacreator.c2.project.stage;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 任务阶段
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.project.stage.stage", table = "c2_comunity_stage",
    ds = "dev-center", cache = false)
public class Stage {
  /**
   *
   */
  @Column(id = "id", type = ColumnType.increment, datatype = "int")
  private java.lang.Integer id;

  /**
   *项目id
   */
  @Column(id = "project_id", datatype = "int")
  private java.lang.Integer projectId;

  /**
   *阶段名称
   */
  @Column(id = "name_", datatype = "string256")
  private java.lang.String name;

  /**
   *排序号
   */
  @Column(id = "order_no", datatype = "int")
  private java.lang.Integer orderNo;

  /**
   *1为默认阶段 2为已完成 
   */
  @Column(id = "state", datatype = "int")
  private java.lang.Integer state;

  /**
   *里程碑id
   */
  @Column(id = "milestone_id", datatype = "int")
  private java.lang.Integer milestoneId;


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
   * 设置阶段名称
   */
  public void setName(java.lang.String name) {
    this.name = name;
  }

  /**
   * 获取阶段名称
   */
  public java.lang.String getName() {
    return name;
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
   * 设置1为默认阶段 2为已完成 
   */
  public void setState(java.lang.Integer state) {
    this.state = state;
  }

  /**
   * 获取1为默认阶段 2为已完成 
   */
  public java.lang.Integer getState() {
    return state;
  }

  /**
   * 设置里程碑id
   */
  public void setMilestoneId(java.lang.Integer milestoneId) {
    this.milestoneId = milestoneId;
  }

  /**
   * 获取里程碑id
   */
  public java.lang.Integer getMilestoneId() {
    return milestoneId;
  }
}
