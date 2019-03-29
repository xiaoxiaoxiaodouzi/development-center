

package com.chinacreator.c2.team;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 团队偏好设置
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.team.TeamPreference",
    table = "c2_comunity_team_preference", ds = "dev-center", cache = false)
public class TeamPreference {
  /**
  *
  */
  @Column(id = "prefer_id", type = ColumnType.increment, datatype = "mediumint")
  private java.lang.Integer preferId;

  /**
  *
  */
  @Column(id = "prefer_name", datatype = "string32")
  private java.lang.String preferName;

  /**
  *
  */
  @Column(id = "prefer_content", datatype = "string256")
  private java.lang.String preferContent;

  /**
  *
  */
  @Column(id = "prefer_desc", datatype = "string256")
  private java.lang.String preferDesc;

  /**
  *
  */
  @Column(id = "team_id", datatype = "mediumint")
  private java.lang.Integer teamId;


  /**
  * 设置
  */
  public void setPreferId(java.lang.Integer preferId) {
    this.preferId = preferId;
  }

  /**
  * 获取
  */
  public java.lang.Integer getPreferId() {
    return preferId;
  }

  /**
  * 设置
  */
  public void setPreferName(java.lang.String preferName) {
    this.preferName = preferName;
  }

  /**
  * 获取
  */
  public java.lang.String getPreferName() {
    return preferName;
  }

  /**
  * 设置
  */
  public void setPreferContent(java.lang.String preferContent) {
    this.preferContent = preferContent;
  }

  /**
  * 获取
  */
  public java.lang.String getPreferContent() {
    return preferContent;
  }

  /**
  * 设置
  */
  public void setPreferDesc(java.lang.String preferDesc) {
    this.preferDesc = preferDesc;
  }

  /**
  * 获取
  */
  public java.lang.String getPreferDesc() {
    return preferDesc;
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
}
