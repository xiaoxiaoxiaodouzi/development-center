

package com.chinacreator.c2.team;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 团队周报统计
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.team.TeamWeekreportStatistics",
    table = "c2_comunity_team_weekreport_statistics", ds = "dev-center", cache = false)
public class TeamWeekreportStatistics {
  /**
  *id
  */
  @Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
  private java.lang.Integer id;

  /**
  *预计总工时
  */
  @Column(id = "estimate", datatype = "double")
  private java.lang.Double estimate;

  /**
  *总消耗工时
  */
  @Column(id = "consumed", datatype = "double")
  private java.lang.Double consumed;

  /**
  *任务总数
  */
  @Column(id = "total", datatype = "mediumint")
  private java.lang.Integer total;

  /**
  *完成任务数
  */
  @Column(id = "completed", datatype = "mediumint")
  private java.lang.Integer completed;

  /**
  *取消任务数
  */
  @Column(id = "canceled", datatype = "tinyint")
  private java.lang.Integer canceled;

  /**
  *参与成员
  */
  @Column(id = "member", datatype = "tinyint")
  private java.lang.Integer member;

  /**
  *周消耗工时
  */
  @Column(id = "worked", datatype = "smalldouble")
  private java.lang.Double worked;

  /**
  *
  */
  @Column(id = "cross_week", datatype = "tinyint")
  private java.lang.Integer crossWeek;

  /**
  *
  */
  @Column(id = "delayed_", datatype = "tinyint")
  private java.lang.Integer delayed;


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
  * 设置预计总工时
  */
  public void setEstimate(java.lang.Double estimate) {
    this.estimate = estimate;
  }

  /**
  * 获取预计总工时
  */
  public java.lang.Double getEstimate() {
    return estimate;
  }

  /**
  * 设置总消耗工时
  */
  public void setConsumed(java.lang.Double consumed) {
    this.consumed = consumed;
  }

  /**
  * 获取总消耗工时
  */
  public java.lang.Double getConsumed() {
    return consumed;
  }

  /**
  * 设置任务总数
  */
  public void setTotal(java.lang.Integer total) {
    this.total = total;
  }

  /**
  * 获取任务总数
  */
  public java.lang.Integer getTotal() {
    return total;
  }

  /**
  * 设置完成任务数
  */
  public void setCompleted(java.lang.Integer completed) {
    this.completed = completed;
  }

  /**
  * 获取完成任务数
  */
  public java.lang.Integer getCompleted() {
    return completed;
  }

  /**
  * 设置取消任务数
  */
  public void setCanceled(java.lang.Integer canceled) {
    this.canceled = canceled;
  }

  /**
  * 获取取消任务数
  */
  public java.lang.Integer getCanceled() {
    return canceled;
  }

  /**
  * 设置参与成员
  */
  public void setMember(java.lang.Integer member) {
    this.member = member;
  }

  /**
  * 获取参与成员
  */
  public java.lang.Integer getMember() {
    return member;
  }

  /**
  * 设置周消耗工时
  */
  public void setWorked(java.lang.Double worked) {
    this.worked = worked;
  }

  /**
  * 获取周消耗工时
  */
  public java.lang.Double getWorked() {
    return worked;
  }

  /**
  * 设置
  */
  public void setCrossWeek(java.lang.Integer crossWeek) {
    this.crossWeek = crossWeek;
  }

  /**
  * 获取
  */
  public java.lang.Integer getCrossWeek() {
    return crossWeek;
  }

  /**
  * 设置
  */
  public void setDelayed(java.lang.Integer delayed) {
    this.delayed = delayed;
  }

  /**
  * 获取
  */
  public java.lang.Integer getDelayed() {
    return delayed;
  }
}
