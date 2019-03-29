

package com.chinacreator.c2.team;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 团队周报提交给
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.team.TeamWeekReportSubmit",
    table = "c2_comunity_team_weekreport_submit2", ds = "dev-center", cache = false)
public class TeamWeekReportSubmit {
  /**
  *
  */
  @Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
  private java.lang.Integer id;

  /**
  *
  */
  @Column(id = "team_id", datatype = "mediumint")
  private java.lang.Integer teamId;

  /**
  *
  */
  @Column(id = "submit_to", datatype = "string32")
  private java.lang.String submitTo;


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
  * 设置
  */
  public void setTeamId(java.lang.Integer teamId) {
    this.teamId = teamId;
  }

  /**
  * 获取
  */
  public java.lang.Integer getTeamId() {
    return teamId;
  }

  /**
  * 设置
  */
  public void setSubmitTo(java.lang.String submitTo) {
    this.submitTo = submitTo;
  }

  /**
  * 获取
  */
  public java.lang.String getSubmitTo() {
    return submitTo;
  }
}
