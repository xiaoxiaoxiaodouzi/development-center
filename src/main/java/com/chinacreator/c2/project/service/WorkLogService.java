package com.chinacreator.c2.project.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.project.info.UserPreferences;
import com.chinacreator.c2.project.info.UserPreferencesService;
import com.chinacreator.c2.project.task.WeekDay;
import com.chinacreator.c2.report.constant.PreferenceConstants;
import com.chinacreator.c2.report.workLog.WorkLog;
import com.chinacreator.c2.uop.OrgUserConstant;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;
import com.chinacreator.c2.web.ds.TreeNode;
import com.chinacreator.c2.web.ds.impl.DynamicTreeNode;
import com.chinacreator.c2.workbench.TeamGroupService;
import com.google.common.base.Preconditions;

public class WorkLogService {
	
	
	private static UserPreferencesService preferenceService = new UserPreferencesService();
	
	/**
	 *  查询缺失工时信息
	 * 
	 * @param conditions={
	 * 						start:起始时间,
	 * 						end:截止时间,
	 * 						deptIds:部门id
	 * }
	 * @return 
	 */
	public List<List<Map<String,Object>>> getEstimateInfosOfLack(Date start,Date end,List<String> deptIds){
		Preconditions.checkArgument(start!=null || end!=null, "时间段不能为空...") ;
		
		Dao<WorkLog> dao = DaoFactory.create(WorkLog.class) ;
		Map<String,Object> conditions = new HashMap<String,Object>() ;
		conditions.put("start", start) ;
		conditions.put("end", end) ;
		
		List<List<Map<String,Object>>> lackList = new ArrayList<List<Map<String,Object>>>() ;
		
		//List<Map<String,Object>> exWhiteList = userService.getOrgUsersFilterByWhiteList(deptIds.get(0));
		
		List<Map<String,Object>> allUsers = new TeamGroupService().getTeamGroupUsers(deptIds.get(0),start,end);
		
		List<Map<String,Object>> members = new ArrayList<Map<String,Object>>();
        for (Map<String,Object> object : allUsers) {
            Map<String,Object> member = new HashMap<String, Object>();
            Map<String,Object> user = (Map<String,Object>) object;
            member.put("userName", user.get(OrgUserConstant.USER_NAME));
            if(user.get(OrgUserConstant.RZDATE)!=null)member.put("inDate", new Date(Long.parseLong(user.get(OrgUserConstant.RZDATE).toString())));
            if(user.get(OrgUserConstant.LZDATE)!=null)member.put("outDate",new Date(Long.parseLong(user.get(OrgUserConstant.LZDATE).toString())));
            members.add(member);
        }
        conditions.put("members",members);
        
        List<Map<String,Object>> result = dao.getSession().selectList("selectEstimateInfosOfLack", conditions);
		if(result.size()==0) return lackList ;
		
		String userRealNameKey = null ;
		List<Map<String,Object>> lackInfo = null ;
		//遍历结果集给成员分组
		for(Map<String,Object> temp : result){
			if(temp.containsKey("userRealName")){
				if(userRealNameKey == null || !temp.get("userRealName").equals(userRealNameKey)){
					userRealNameKey = (String) temp.get("userRealName") ;
					lackInfo = new ArrayList<Map<String,Object>>() ;
					lackList.add(lackInfo) ;
				}
				lackInfo.add(temp) ;
			}
		}
		
		//保存偏好
		updateWorkLogDeptPreference(JSON.toJSONString(deptIds)) ;
		
		return lackList;
	}

	public static List<Map<String,Object>> getMembersByDeptIds(List<String> deptIds) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<User> users = OrgUserService.queryUserByOrg(deptIds.toArray(new String[0]));
		List<String> userNames = new ArrayList<String>();
		for(User u:users){
			userNames.add(u.getEmail().substring(0,u.getEmail().indexOf("@")));
		}
		return list;
	}
	
	public List<Map<String,Object>> getMembersByDeptId(String deptId) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<User> users = OrgUserService.queryUserByOrg(deptId);
		List<String> userNames = new ArrayList<String>();
		for(User u:users){
			userNames.add(u.getEmail().substring(0,u.getEmail().indexOf("@")));
		}
		return list;
	}
	
	
	public List<Map<String,Object>> getAllMemberOfProjects(List<Integer> projects){
		return DaoFactory.create(WorkLog.class).getSession().selectList("com.chinacreator.c2.report.workLog.WorkLogMapper.selectAllMemberOfProjects",projects) ;
	}
	
	public List<Map<String,Object>> getWeekDayEvents(Map<String,Object> conditions){
		Dao<WeekDay> dao = DaoFactory.create(WeekDay.class) ;
		return dao.getSession().selectList("selectWeekDayEvents", conditions) ;
	}
	
	public boolean generateDefaultWeekDays(List<WeekDay> weekDays){
		Dao<WeekDay> dao = DaoFactory.create(WeekDay.class) ;
		for(WeekDay day:weekDays){
			WeekDay current = new WeekDay() ;
			current.setWorkdate(day.getWorkdate()) ;
			List<WeekDay> list = dao.select(current) ;
			if(list.size()==0){
				dao.insert(day) ;
			}else{
				current = list.get(0) ;
				current.setEstimate(day.getEstimate()) ;
				dao.update(current) ;
			}
		}
		return true;
	}
	
	public void updateWeekDayEstimate(WeekDay weekDay){
		DaoFactory.create(WeekDay.class).getSession().update("updateEstimateByWorkDate", weekDay) ;
	}
	
	public void addWeekDayEstimate(WeekDay weekDay){
		Dao<WeekDay> dao = DaoFactory.create(WeekDay.class) ;
		WeekDay temp = new WeekDay() ;
		temp.setWorkdate(weekDay.getWorkdate()) ;
		if(dao.select(temp).size()==0){
			dao.insert(weekDay) ;
		}else{
			dao.update(weekDay) ;
		}
	}
	
	public void synchronizeWorkLog(Date start,Date end,List<String> deptIds){
		Dao<WorkLog> dao = DaoFactory.create(WorkLog.class) ;
		HashMap<String, Object> conditions = new HashMap<String,Object>();
		conditions.put("start", start) ;
		conditions.put("end", end) ;
		
		conditions.put("memberInfos", getMembersByDeptIds(deptIds)) ;
		
		dao.getSession().delete("deleteBeforeSyn", conditions) ;

		List<WorkLog> list = dao.getSession().selectList("selectWorkLog4Syn", conditions) ;
		if(list.size()!=0){
			dao.insertBatch(list) ;
		}
	}
	
	public void updateWorkLogDeptPreference(String deptArrJsonStr){
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
		UserPreferences preference = new UserPreferences() ;
		preference.setUserId(uu.get("user_id").toString()) ;
		preference.setInfoName(PreferenceConstants.WORKLOG_DEPT) ;
		preference.setInfoContent(deptArrJsonStr) ;
		preferenceService.update(preference) ;
	}
	
	public List<DynamicTreeNode> getDepartMentTree(String orgId){
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
		UserPreferences deptPreference = preferenceService.queryByPK(uu.get("user_id").toString(), PreferenceConstants.WORKLOG_DEPT);
		List<String> deptIds = new ArrayList<String>() ;
		if(StringUtils.isNotBlank(deptPreference.getInfoContent()))
			deptIds = JSON.parseArray(deptPreference.getInfoContent(), String.class) ;
		if(deptIds == null) deptIds = new ArrayList<String>() ;
		List<DynamicTreeNode> orgs = OrgUserService.queryChildOrgs(null, -1);
		for (DynamicTreeNode dynamicTreeNode : orgs) {
			if(dynamicTreeNode.getPid()==null){
				dynamicTreeNode.put("pId", "0") ;
			}else{
				dynamicTreeNode.put("pId", dynamicTreeNode.getPid()) ;
			}
			dynamicTreeNode.put("checked", deptIds.contains(dynamicTreeNode.getId())) ;
		}
		return orgs;
	}
	
	class WorkLogDeptTreeNode implements TreeNode {
		
		private String pId,id,name ;
		
		private boolean checked,isParent,open,chkDisabled ;

		public WorkLogDeptTreeNode(String id, String name) {
			this.id = id ;
			this.name = name ;
		}

		public boolean isChkDisabled() {
			return chkDisabled;
		}

		public void setChkDisabled(boolean chkDisabled) {
			this.chkDisabled = chkDisabled;
		}

		public String getPId() {
			return pId;
		}

		public void setPId(String pId) {
			this.pId = pId;
		}

		public void setParent(boolean isParent) {
			this.isParent = isParent;
		}

		public void setId(String id) {
			this.id = id;
		}

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		public boolean isOpen() {
			return open;
		}

		public void setOpen(boolean open) {
			this.open = open;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setIsParent(boolean isParent) {
			this.isParent = isParent;
		}

		@Override
		public String getPid() {
			return null;
		}

		@Override
		public String getId() {
			return this.id;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public boolean getIsParent() {
			return this.isParent;
		}
		
	}


}
