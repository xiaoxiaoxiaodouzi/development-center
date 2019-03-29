package com.chinacreator.c2.team.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Conditions;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.dao.mybatis.enhance.Pageable;
import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;
import com.chinacreator.c2.dao.mybatis.enhance.Rule;
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.team.Team;
import com.chinacreator.c2.team.TeamPreference;
import com.chinacreator.c2.team.TeamUser;
import com.chinacreator.c2.team.TeamWeekReportSubmit;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.Organization;
import com.chinacreator.c2.uop.User;
import com.google.common.collect.Lists;

@Service
public class TeamService {

  @Autowired
  private TeamWeekReportService wrService;
  /**
   * 添加团队
   * @param entity
   * @return
   */
  public Team insert(Team entity) {
    DaoFactory.create(Team.class).insert(entity);
    if(CollectionUtils.isNotEmpty(entity.getTeamUser())){
      List<TeamUser> list = Lists.newArrayList();
      for(TeamUser tu:entity.getTeamUser()){
        tu.setTeamId(entity.getId());
        list.add(tu);
      }
      DaoFactory.create(TeamUser.class).insertBatch(list);
    }
    return entity;
  }

  /**
   * 查询团队
   * @param owner
   * @return
   */
  public List<Map<String,Object>> getListByPage(String owner) {
    return DaoFactory.create(Team.class).getSession().selectList("getTeamList", owner);
  }

  /**
   * 获取成员信息
   * @param orgId
   * @param name
   * @return
   */
  public List<Map<String, String>> getMembers(String orgId, String name) {
    List<String> userNameList = new ArrayList<String>();

    if ((null != name && !name.trim().equals(""))||(null != orgId && !orgId.trim().equals(""))) {
      List<User> list = OrgUserService.queryUserByUserCondition(orgId, name);
      if (list.size() > 0) {
        for (User userDTO : list) {
          if (userDTO.getEmail() != null)
            userNameList.add(userDTO.getEmail().substring(0, userDTO.getEmail().indexOf("@")));
        }
      }
    }
    
    List<Map<String,String>> list = new ArrayList<Map<String,String>>();
    if(userNameList.size()>0){
      // 根据远程获取的用户数据和机构数据再到本地拼装图标
      List<Map<String, String>> developUsers = DaoFactory.create(Object.class).getSession()
          .selectList("queryUsersByUsernames", userNameList);
      for (Map<String, String> map : developUsers) {
        User u = OrgUserService.queryUserByEmail(map.get("userName") + "@chinacreator.com");
        if (u != null) {
          map.put("remark1", map.get("icon"));
          List<Organization> org = OrgUserService.queryOrgs(u.getId());
          if (org != null && org.size() > 0) {
            map.put("orgId", org.get(0).getId());
            map.put("orgShowName", org.get(0).getName());
          }
          list.add(map);
        }

      }
    }
    
    return list;
  }

  /**
   * 获取某团队成员包含创建者
   * @param teamId
   * @return
   */
  public List<Map<String, String>> getTeamUser(Integer teamId) {
    List<Map<String,String>> list = new ArrayList<Map<String,String>>();
    
    List<String> userNameList = DaoFactory.create(TeamUser.class).getSession().selectList("getTeamUsers", teamId);
    
    if(userNameList.size()>0){
      List<Map<String,String>> developUsers = DaoFactory.create(Object.class).getSession().selectList("queryUsersByUsernames", userNameList);
      for (Map<String,String> map:developUsers) {
        User u = OrgUserService.queryUserByEmail(map.get("userName") + "@chinacreator.com");
        if (u != null) {
          map.put("remark1", map.get("icon"));
          List<Organization> org = OrgUserService.queryOrgs(u.getId());
          if (org != null && org.size() > 0) {
            map.put("orgId", org.get(0).getId());
            map.put("orgShowName", org.get(0).getName());
          }
          list.add(map);
        }
        
      }
    }
    return list;
  }

  /**
   * 删除团队下的某些成员
   * @param teamId
   * @param userNames
   * @return
   */
  public void deleteTeamUser(Integer teamId, List<String> userNames) {
    Dao<TeamUser> dao = DaoFactory.create(TeamUser.class);
    Conditions con = new Conditions("AND").append(new Rule("teamId","eq",teamId)).append(new Rule("userName", "in", userNames));
    List<TeamUser> list = dao.selectByCondition(con, null);
    dao.deleteBatch(list);
  }
  
  /**
   * 添加团队成员
   * @param teamId
   * @param userNames
   */
  public void addTeamUser(Integer teamId, List<String> userNames) {
    List<TeamUser> list = Lists.newArrayList();
    for(String name:userNames){
      TeamUser tu = new TeamUser();
      tu.setTeamId(teamId);
      tu.setUserName(name);
      list.add(tu);
    }
    DaoFactory.create(TeamUser.class).insertBatch(list);
  }

  /**
   * 获取团队周报提交者
   * @param teamId
   * @return
   */
  public List<Map<String, Object>> getWeekReportSubmit2(Integer teamId) {
    return DaoFactory.create(TeamWeekReportSubmit.class).getSession().selectList("selectWRSubmitByTeamId", teamId);
  }

  /**
   * 获取周报偏好设置
   * @param teamId
   * @return
   */
  public Map<String, Object> getTeamPreference(Integer teamId) {
    Map<String, Object> result = new HashMap<String, Object>();
    TeamPreference entity = new TeamPreference();
    entity.setTeamId(teamId);
    for (TeamPreference pre : DaoFactory.create(TeamPreference.class).select(entity)) {
      result.put(pre.getPreferName(), pre);
    }
    return result;
  }

  /**
   * 设置周报提交者
   * 先删除后插入
   * @param submits
   */
  @Transactional
  public void setSubmit2(int teamId,List<TeamWeekReportSubmit> submits) {
    Dao<TeamWeekReportSubmit> dao = DaoFactory.create(TeamWeekReportSubmit.class);
    TeamWeekReportSubmit sub = new TeamWeekReportSubmit();
    sub.setTeamId(teamId);
    List<TeamWeekReportSubmit> list = dao.select(sub);
    dao.deleteBatch(list);
    dao.insertBatch(submits);
  }

  /**
   * 分页获取团队数据 包含团队成员
   * @param owner
   * @param pageIndex
   * @return
   */
  public Page<Map<String, Object>> getTeamByPage(String owner, int pageIndex) {
    Pageable pageable = new Pageable(pageIndex);
    RowBounds4Page page = new RowBounds4Page(pageable, null, null, true) ;
    List<Map<String,Object>> list = DaoFactory.create(Team.class).getSession()
            .selectList("getTeamList", owner, page) ;
    return new Page<Map<String, Object>>(pageable.getPageIndex(), pageable.getPageSize(), page.getTotalSize(), list);
  }

  /**
   * 删除某团队以及成员数据
   * @param teamId
   */
  @Transactional
  public void deleteTeam(Integer teamId) {
    Dao<TeamUser> dao = DaoFactory.create(TeamUser.class);
    TeamUser uu = new TeamUser();
    uu.setTeamId(teamId);
    List<TeamUser> users = dao.select(uu);
    dao.deleteBatch(users);
    DaoFactory.create(Team.class).delete(teamId);
  }
  
  /**
   * 获取有权限的团队列表
   * 提交给的团队 
   * @param owner
   * @return
   */
  public List<Map<String,Object>> getAuthList(String owner) {
    List<Map<String,Object>> teams = wrService.getWeeklyReportAuthorInfos(owner, null);
    List<Map<String,Object>> list = getListByPage(owner);
    for(Map<String,Object> t:teams){
      if(t.get("userTeamRelationship").equals("submit2")){
        boolean flag = true;
        for(Map<String,Object> l:list){
          if(l.get("id").equals(t.get("id"))){
            flag = false;
          }
        }
        if(flag){
          Map<String,Object> team = new HashMap<String,Object>();
          team.put("id",t.get("id"));
          team.put("name",t.get("name"));
          list.add(team);
        }
      }
    }
    return list;
  }

  /**
   * 获取团队下的所有成员参与的项目
   * @param belongToNextWeek 
   * @param teamId
   * @return
   */
  public List<Project> getTeamProject(Integer reportId, boolean belongToNextWeek) {
    Map<String,Object> map = new HashMap<String,Object>();
    map.put("reportId", reportId);
    map.put("belongToNextWeek", belongToNextWeek);
    return DaoFactory.create(Team.class).getSession().selectList("getTeamProject", map);
  }

  /**
   * 团队转让
   * 更新所有者且删除该成员数据
   * @param teamId
   * @param owner
   */
  @Transactional
  public void transformOwner(Integer teamId, String owner) {
    TeamUser teamUser = new TeamUser();
    teamUser.setTeamId(teamId);
    teamUser.setUserName(owner);
    teamUser = DaoFactory.create(TeamUser.class).selectOne(teamUser);
    DaoFactory.create(TeamUser.class).delete(teamUser);
    Team team = DaoFactory.create(Team.class).selectByID(teamId);
    team.setOwner(owner);
    DaoFactory.create(Team.class).update(team);
  }

  /**
   * 更新团队信息
   * @param teamId
   * @param entity
   * @return
   */
  public Team update(int teamId, Team entity) {
    DaoFactory.create(Team.class).update(entity);
    return entity;
  }

  /**
   * 
   * @param teamId
   * @return
   */
  public Team getTeamById(Integer teamId) {
    return DaoFactory.create(Team.class).selectByID(teamId);
  }
}
