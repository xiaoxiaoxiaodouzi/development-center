package com.chinacreator.c2.project.teamLog;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.chinacreator.c2.config.ConfigManager;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.project.info.MembersMgt;
import com.chinacreator.c2.uop.OrgUserConstant;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.Organization;
import com.chinacreator.c2.uop.User;
import com.chinacreator.c2.workbench.WhiteName;
import com.chinacreator.platform.mvc.template.model.Config;
import com.google.common.collect.Lists;

public class ProjectLogService {

  @SuppressWarnings("unchecked")
  public List<Object> projectLogs(Map<String, Object> search) {
    Map<String, Object> missLogCondition = new HashMap<String, Object>();
    Map<String, Object> weekMap = (Map<String, Object>) search.get("week");
    Boolean task = (Boolean) search.get("task");
    //Boolean lack = (Boolean) search.get("lack");
    if (weekMap == null) {
      weekMap = new HashMap<String, Object>();
      search.put("week", weekMap);
    }
    JSONArray usersArray = (JSONArray) search.get("users");
    if (usersArray == null || usersArray.size() == 0)
      return null;
    List<Map<String, Object>> members = new ArrayList<Map<String, Object>>();
    for (Object object : usersArray) {
      Map<String, Object> member = new HashMap<String, Object>();
      Map<String, Object> user = (Map<String, Object>) object;
      member.put("userName", user.get(OrgUserConstant.USER_NAME));
      if (user.get(OrgUserConstant.RZDATE) != null)
        member.put("inDate", new Date(Long.parseLong(user.get(OrgUserConstant.RZDATE).toString())));
      if (user.get(OrgUserConstant.LZDATE) != null)
        member
            .put("outDate", new Date(Long.parseLong(user.get(OrgUserConstant.LZDATE).toString())));
      members.add(member);
    }
    missLogCondition.put("members", members);

    if (weekMap != null && weekMap.get("et") != null) {
      Date et = new Date(Long.parseLong(weekMap.get("et").toString()));
      weekMap.put("et", et);
      missLogCondition.put("end", et);
    }
    if (weekMap != null && weekMap.get("st") != null) {
      Date st = new Date(Long.parseLong(weekMap.get("st").toString()));
      weekMap.put("st", st);
      missLogCondition.put("start", st);
    }
    Dao<Object> baseDao = DaoFactory.create(Object.class);

    List<Object> list = null;
    if (task) {
      list = baseDao.getSession().selectList("teamLogEstimate", search);
    }
    /*if (lack) {
      List<Object> missLogs =
          baseDao.getSession().selectList("teamGroupEstimateLack", missLogCondition);
      if (list == null)
        list = missLogs;
      else
        list.addAll(missLogs);
      Collections.sort(list, new Comparator<Object>() {
        public int compare(Object arg0, Object arg1) {
          Map<String, Object> A = (Map<String, Object>) arg0;
          Map<String, Object> B = (Map<String, Object>) arg1;
          Date At = (Date) A.get("workDate");
          Date Bt = (Date) B.get("workDate");
          return At.compareTo(Bt);
        }
      });
    }*/

    return list;
  }
  
  /**
   * 项目成员
   * @param projectId
   * @param startDate
   * @param endDate
   * @return
   */
  @SuppressWarnings("unchecked")
  public Map<String,Object> getProjectUsers(String projectId, Date startDate, Date endDate) {
    Map<String,Object> results = new HashMap<String,Object>();
    List<Map<String, Object>> users = Lists.newArrayList();
    MembersMgt memberService = new MembersMgt();
    List<Map<String, Object>> lists = memberService.getMembers(Integer.parseInt(projectId));
    for(Map<String, Object> map:lists){
      Map<String, Object> user = (Map<String, Object>) map.get("userDTO");
      user.put("userName", user.get("user_name"));
      user.put("userId",user.get("user_id"));
      users.add(user);
    }
    List<User> userList = filterByWhiteList(users);
    
    for (int i = 0; i < userList.size(); i++) {
      User u = userList.get(i);
      if (u.get(OrgUserConstant.RZDATE) != null) {// 过滤入职时间在查询结束时间后用户
        Long inDateMill = Long.parseLong(u.get(OrgUserConstant.RZDATE).toString());
        Date inDate = new Date(inDateMill);
        if (inDate.after(endDate)) {
          userList.remove(i);
          i--;
        }
      }
      if (Integer.parseInt(u.get(OrgUserConstant.ISVALID).toString()) != 1) {// 1为有效
        if (u.get(OrgUserConstant.LZDATE) != null) {// 过滤离职时间在查询起始时间前用户
          Long leaveDateMill = Long.parseLong(u.get(OrgUserConstant.LZDATE).toString());
          Date leaveDate = new Date(leaveDateMill);
          if (leaveDate.before(startDate)) {
            userList.remove(i);
            i--;
          }
        } else {
          userList.remove(i);
          i--;
        }
      }
      String userName = u.getEmail().substring(0, u.getEmail().indexOf("@"));
      u.put("userRealname", u.getName());
      u.put("userName", userName);
    }
    results.put("users", userList);
    List<Organization> depts = getDepts(userList);
    results.put("depts", depts);

    return results;
  }

  //获取用户所在的部门信息  
  private List<Organization> getDepts(List<User> userList) {
    List<String> orgs = Lists.newArrayList();
    for(User u:userList){
      List<String> orgIds = (List<String>) u.get("orgIds");
      orgs.addAll(orgIds);
    }
    //查询父机构4mxeMpibqTqGaW6kHlHPB8A下的所有子机构 排除在外的orgIds
    List<Organization> childrenOrgs = OrgUserService.queryChildOrgs(ConfigManager.getInstance().getConfig("c2_org_pid"), true);
    Map<String,String> orgMap = new HashMap<String,String>();
    for(Organization o:childrenOrgs){
    	orgMap.put(o.getId(), o.getId());
    }
    for(int i=0;i<orgs.size();i++){
    	if(!orgMap.containsKey(orgs.get(i))){
    		orgs.remove(i);
    		i--;
    	}
    }
    return OrgUserService.getOrgInfoByIds(orgs);
  }

  private List<User> filterByWhiteList(List<Map<String, Object>> users) {
    List<String> userName = new ArrayList<String>();
    List<String> userIds = new ArrayList<String>();
    for(Map<String, Object> u:users){
        userName.add(u.get("userName").toString());
        userIds.add(u.get("userId").toString());
    }
    Dao<WhiteName> whiteNameDao = DaoFactory.create(WhiteName.class); 
    List<String> whiteList = whiteNameDao.getSession().selectList("com.chinacreator.c2.workbench.WhiteNameMapper.selectWhiteNameListInUsers", userName);
    
    Iterator<Map<String, Object>> it = users.iterator();
    while(it.hasNext()){
      Map<String, Object> uu = it.next();
        String name = uu.get("userName").toString();
        if(whiteList.contains(name)){
            it.remove() ;
        }
    }
    
    //有效的用户
    userName = new ArrayList<String>();
    for(Map<String, Object> u:users){
      userName.add(u.get("userName").toString());
    }
    
    List<User> list = OrgUserService.queryUserByNames(userName);
    //因为现在的用户id对应不上 所以查不到任何信息
    	//List<User> list = OrgUserService.queryUserByids(userIds);
    return list;
  }

}
