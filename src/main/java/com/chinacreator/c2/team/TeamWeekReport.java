

package com.chinacreator.c2.team;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 团队周报
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.team.TeamWeekReport",
    table = "c2_comunity_team_weekreport", ds = "dev-center", cache = false)
public class TeamWeekReport {
  /**
  *周报id
  */
  @Column(id = "id", type = ColumnType.uuid, datatype = "mediumint")
  private java.lang.Integer id;

  /**
  *团队id
  */
  @Column(id = "team_id", datatype = "mediumint")
  private java.lang.Integer teamId;

  /**
  *年份
  */
  @Column(id = "year", datatype = "mediumint")
  private java.lang.Integer year;

  /**
  *周编号
  */
  @Column(id = "week", datatype = "mediumint")
  private java.lang.Integer week;

  /**
  *本周总结
  */
  @Column(id = "summary", datatype = "text")
  private java.lang.String summary;

  /**
  *统计信息
  */
  @Column(id = "statistics", association = true)
  private TeamWeekreportStatistics statistics;

  /**
  *总体状态
  */
  @Column(id = "status_", datatype = "string64")
  private java.lang.String status;

  /**
  *下周总结
  */
  @Column(id = "next_summary", datatype = "text")
  private java.lang.String nextSummary;

  /**
  *可能存在得问题
  */
  @Column(id = "exist_problems", datatype = "text")
  private java.lang.String existProblems;

  /**
  *可见范围
  */
  @Column(id = "visible_range", datatype = "string20")
  private java.lang.String visibleRange;


  /**
  * 设置周报id
  */
  public void setId(java.lang.Integer id) {
    this.id = id;
  }

  /**
  * 获取周报id
  */
  public java.lang.Integer getId() {
    return id;
  }

  /**
  * 设置团队id
  */
  public void setTeamId(java.lang.Integer teamId) {
    this.teamId = teamId;
  }

  /**
  * 获取团队id
  */
  public java.lang.Integer getTeamId() {
    return teamId;
  }

  /**
  * 设置年份
  */
  public void setYear(java.lang.Integer year) {
    this.year = year;
  }

  /**
  * 获取年份
  */
  public java.lang.Integer getYear() {
    return year;
  }

  /**
  * 设置周编号
  */
  public void setWeek(java.lang.Integer week) {
    this.week = week;
  }

  /**
  * 获取周编号
  */
  public java.lang.Integer getWeek() {
    return week;
  }

  /**
  * 设置本周总结
  */
  public void setSummary(java.lang.String summary) {
    this.summary = summary;
  }

  /**
  * 获取本周总结
  */
  public java.lang.String getSummary() {
    return summary;
  }

  /**
  * 设置统计信息
  */
  public void setStatistics(TeamWeekreportStatistics statistics) {
    this.statistics = statistics;
  }

  /**
  * 获取统计信息
  */
  public TeamWeekreportStatistics getStatistics() {
    return statistics;
  }

  /**
  * 设置总体状态
  */
  public void setStatus(java.lang.String status) {
    this.status = status;
  }

  /**
  * 获取总体状态
  */
  public java.lang.String getStatus() {
    return status;
  }

  /**
  * 设置下周总结
  */
  public void setNextSummary(java.lang.String nextSummary) {
    this.nextSummary = nextSummary;
  }

  /**
  * 获取下周总结
  */
  public java.lang.String getNextSummary() {
    return nextSummary;
  }

  /**
  * 设置可能存在得问题
  */
  public void setExistProblems(java.lang.String existProblems) {
    this.existProblems = existProblems;
  }

  /**
  * 获取可能存在得问题
  */
  public java.lang.String getExistProblems() {
    return existProblems;
  }

  /**
  * 设置可见范围
  */
  public void setVisibleRange(java.lang.String visibleRange) {
    this.visibleRange = visibleRange;
  }

  /**
  * 获取可见范围
  */
  public java.lang.String getVisibleRange() {
    return visibleRange;
  }
}
