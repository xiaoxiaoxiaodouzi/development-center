

package com.chinacreator.c2.team;

import java.util.Collection;
import com.chinacreator.c2.annotation.Children;
import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 团队
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.team.Team", table = "c2_comunity_team", ds = "dev-center",
    cache = false)
public class Team {
  /**
  *
  */
  @Column(id = "id", type = ColumnType.increment, datatype = "int")
  private java.lang.Integer id;

  /**
  *团队名称
  */
  @Column(id = "name_", datatype = "string256")
  private java.lang.String name;

  /**
  *团队负责人
  */
  @Column(id = "owner", datatype = "string256")
  private java.lang.String owner;

  /**
  *团队创建时间
  */
  @Column(id = "create_time", datatype = "timestamp")
  private java.sql.Timestamp createTime;

  /**
  *团队成员
  */
  @Children(id = "teamUser", fk = "id:team_id")
  private Collection<TeamUser> teamUser;


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
  * 设置团队名称
  */
  public void setName(java.lang.String name) {
    this.name = name;
  }

  /**
  * 获取团队名称
  */
  public java.lang.String getName() {
    return name;
  }

  /**
  * 设置团队负责人
  */
  public void setOwner(java.lang.String owner) {
    this.owner = owner;
  }

  /**
  * 获取团队负责人
  */
  public java.lang.String getOwner() {
    return owner;
  }

  /**
  * 设置团队创建时间
  */
  public void setCreateTime(java.sql.Timestamp createTime) {
    this.createTime = createTime;
  }

  /**
  * 获取团队创建时间
  */
  public java.sql.Timestamp getCreateTime() {
    return createTime;
  }

  /**
  * 设置团队成员
  */
  public void setTeamUser(Collection<TeamUser> teamUser) {
    this.teamUser = teamUser;
  }

  /**
  * 获取团队成员
  */
  public Collection<TeamUser> getTeamUser() {
    return teamUser;
  }
}
