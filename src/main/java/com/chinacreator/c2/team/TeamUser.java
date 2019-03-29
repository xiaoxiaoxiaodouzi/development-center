

package com.chinacreator.c2.team;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 团队成员
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.team.TeamUser", table = "c2_comunity_teamuser",
    ds = "dev-center", cache = false)
public class TeamUser {
  /**
  *
  */
  @Column(id = "id", type = ColumnType.uuid, datatype = "int")
  private java.lang.Integer id;

  /**
  *团队id
  */
  @Column(id = "team_id", datatype = "int")
  private java.lang.Integer teamId;

  /**
  *成员名称
  */
  @Column(id = "user_name", datatype = "string256")
  private java.lang.String userName;

  /**
  *导入时间
  */
  @Column(id = "import_time", datatype = "timestamp")
  private java.sql.Timestamp importTime;


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
  * 设置成员名称
  */
  public void setUserName(java.lang.String userName) {
    this.userName = userName;
  }

  /**
  * 获取成员名称
  */
  public java.lang.String getUserName() {
    return userName;
  }

  /**
  * 设置导入时间
  */
  public void setImportTime(java.sql.Timestamp importTime) {
    this.importTime = importTime;
  }

  /**
  * 获取导入时间
  */
  public java.sql.Timestamp getImportTime() {
    return importTime;
  }
}
