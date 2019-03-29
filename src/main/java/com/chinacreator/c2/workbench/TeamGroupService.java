package com.chinacreator.c2.workbench;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.chinacreator.asp.comp.sys.oauth2.common.CredentialStore;
import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Order;
import com.chinacreator.c2.dao.mybatis.enhance.Sortable;
import com.chinacreator.c2.project.info.Member;
import com.chinacreator.c2.project.task.WeekDay;
import com.chinacreator.c2.sysmgr.User;
import com.chinacreator.c2.uop.OrgUserConstant;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.Organization;
import com.chinacreator.c2.web.ds.impl.DynamicTreeNode;

public class TeamGroupService {
	
	/*
	 * 团队任务列表
	 */
	public List<Object> teamTaskList(Map<String,Object> search){
		
		@SuppressWarnings("unchecked")
		Map<String,Object> weekMap = (Map<String, Object>) search.get("week");
		if (weekMap==null) {
			weekMap=new HashMap<String, Object>();
			search.put("week", weekMap);
		}
		JSONArray usersArray = (JSONArray) search.get("users");
		if(usersArray==null||usersArray.size()==0){
			return null;
		}
		JSONArray projectsArray = (JSONArray) search.get("projects");
		if(projectsArray==null||projectsArray.size()==0)search.remove("projects");
		if(weekMap!=null&&weekMap.get("et")!=null){
			Date et = new Date(Long.parseLong(weekMap.get("et").toString()));
			weekMap.put("et", et);
		}
		if(weekMap!=null&&weekMap.get("st")!=null){
			Date et = new Date(Long.parseLong(weekMap.get("st").toString()));
			weekMap.put("st", et);
		}
		Dao<Object> baseDao = DaoFactory.create(Object.class);
		List<Object> list = baseDao.getSession().selectList("projectTaskList", search) ;
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, List<Object>> teamTaskListForApp(Map<String,Object> search){
		Map<String,List<Object>> resultMap = new HashMap<String,List<Object>>() ;
		List<Object> tasks = teamTaskList(search);
		if(tasks==null || tasks.size()==0) return resultMap ;
		for(Object object :tasks){
			Map<String, Object> task = (Map<String, Object>) object;
			if(resultMap.containsKey(task.get("assignedTo"))){
				resultMap.get(task.get("assignedTo")).add(task) ;
			}else{
				List<Object> taskArr = new ArrayList<Object>() ;
				taskArr.add(object) ;
				resultMap.put((String) task.get("assignedTo"), taskArr) ;
			}
		}
		return resultMap;
	}
	
	public List<Object> teamProjects(Map<String,Object> search){
		JSONArray usersArray = (JSONArray) search.get("users");
		if(usersArray==null||usersArray.size()==0){
			return null;
		}
		Dao<Object> baseDao = DaoFactory.create(Object.class);
		List<Object> list = baseDao.getSession().selectList("teamGroupProjects", search) ;
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Object> teamLogsForReport(Map<String,Object> search){
        Dao<Object> baseDao = DaoFactory.create(Object.class);
        Map<String,Object> resultMap = new HashMap<String, Object>() ;
        
        Map<String,Object> weekMap = (Map<String, Object>) search.get("week");
		if (weekMap==null) {
			weekMap=new HashMap<String, Object>();
			search.put("week", weekMap);
		}
		if(weekMap!=null&&weekMap.get("et")!=null&&((Map) weekMap.get("et")).size()!=0){
			Map<String,Object> endDate = (Map<String, Object>) weekMap.get("et");
			Calendar endCal = Calendar.getInstance() ;
			endCal.setWeekDate(Integer.parseInt(endDate.get("year").toString()), Integer.parseInt(endDate.get("week").toString())+1, Calendar.SUNDAY) ;
			weekMap.put("et",  new Date(endCal.getTimeInMillis()));
		}
		if(weekMap!=null&&weekMap.get("st")!=null&&((Map) weekMap.get("st")).size()!=0){
			Map<String,Object> startDate = (Map<String, Object>) weekMap.get("st");
			
			Calendar startCal = Calendar.getInstance() ;
			startCal.setWeekDate(Integer.parseInt(startDate.get("year").toString()), Integer.parseInt(startDate.get("week").toString()), Calendar.MONDAY) ;
			weekMap.put("st", new Date(startCal.getTimeInMillis()));
		}
		List<Map<String,Object>> logs= null ;
		List<Object> list = null;
	    list = baseDao.getSession().selectList("teamLogEstimateForReport", search) ;
	   
	    for(Object obj :list){
	        User user = new User() ;
	    	Map<String,Object> task = (Map<String, Object>) obj ;
	    	String assignedTo = task.get("account").toString();
	    	if(resultMap.containsKey(assignedTo)){
				logs = (List<Map<String,Object>>) ((Map<String,Object>)resultMap.get(assignedTo)).get("logs") ;
				Integer consumed=Integer.parseInt(((Map<String,Object>)resultMap.get(assignedTo)).get("consumed").toString());
		        consumed=consumed+Integer.parseInt(task.get("consumed").toString());
		        ((Map<String,Object>)resultMap.get(assignedTo)).put("consumed", consumed);
			}else{
				//初始化
				Map<String,Object> subResultMap = new HashMap<String,Object>() ;
				user.put(OrgUserConstant.USER_NAME, task.get("account").toString());
				user.setName(task.get("user_realname").toString()) ;
				user.put("remark1", "1");
				user.put("userRealname", task.get("user_realname").toString());
				subResultMap.put("userDTO", user) ;
				logs = new ArrayList<Map<String,Object>>() ;
				subResultMap.put("logs", logs) ;
				subResultMap.put("consumed", Integer.parseInt(task.get("consumed").toString())) ;
				resultMap.put(task.get("account").toString(), subResultMap) ;
			}
	    	logs.add(task) ;
	    	((Map<String,Object>)resultMap.get(assignedTo)).put("logs", logs) ;
	    }
		
		return resultMap.values();
	}
	public List<Map<String,Object>> teamLogs(Map<String,Object> search){
		Map<String,Object> missLogCondition = new HashMap<String, Object>();
		Map<String,Object> weekMap = (Map<String, Object>) search.get("week");
		Boolean task = (Boolean) search.get("task");
		Boolean lack = (Boolean) search.get("lack");
		Boolean overtime = (Boolean) search.get("overtime");//加班工时
		Boolean overdue = (Boolean) search.get("overdue");//逾期工时
		if (weekMap==null) {
			weekMap=new HashMap<String, Object>();
			search.put("week", weekMap);
		}
		JSONArray usersArray = (JSONArray) search.get("users");
		if(usersArray==null||usersArray.size()==0) return null;
		List<Map<String,Object>> members = new ArrayList<Map<String,Object>>();
		for (Object object : usersArray) {
			Map<String,Object> member = new HashMap<String, Object>();
			Map<String,Object> user = (Map<String,Object>) object;
			member.put("userName", user.get(OrgUserConstant.USER_NAME));
			if(user.get(OrgUserConstant.RZDATE)!=null)member.put("inDate", new Date(Long.parseLong(user.get(OrgUserConstant.RZDATE).toString())));
			if(user.get(OrgUserConstant.LZDATE)!=null)member.put("outDate",new Date(Long.parseLong(user.get(OrgUserConstant.LZDATE).toString())));
			members.add(member);
		}
		missLogCondition.put("members",members);
		
		if(weekMap!=null&&weekMap.get("et")!=null){
			Date et = new Date(Long.parseLong(weekMap.get("et").toString()));
			weekMap.put("et", et);
			missLogCondition.put("end",et);
		}
		if(weekMap!=null&&weekMap.get("st")!=null){
			Date st = new Date(Long.parseLong(weekMap.get("st").toString()));
			weekMap.put("st", st);
			missLogCondition.put("start",st);
		}
		Dao<Object> baseDao = DaoFactory.create(Object.class);
		
		List<Map<String,Object>> list = null;
		if(task){
			list = baseDao.getSession().selectList("teamLogEstimate", search) ;
		}
		if(lack){
			List<Map<String,Object>> missLogs = baseDao.getSession().selectList("teamGroupEstimateLack", missLogCondition);
			if(list==null)list = missLogs;
			else list.addAll(missLogs);
			Collections.sort(list,new Comparator(){ public int compare(Object arg0, Object arg1){
				Map<String,Object> A = (Map<String, Object>) arg0;
				Map<String,Object> B = (Map<String, Object>) arg1;
				Date At = (Date) A.get("workDate");
				Date Bt = (Date) B.get("workDate");
				return At.compareTo(Bt);
			}});
		}
		if(overtime){
			List<Map<String,Object>> taskList = baseDao.getSession().selectList("teamLogEstimate", search) ;//超时任务日志
			List<Map<String,Object>> overLogs = baseDao.getSession().selectList("teamGroupEstimateOvertime", missLogCondition);//超时工时
			list = changeOvertimeLogs(taskList,overLogs);
			Collections.sort(list,new Comparator(){ public int compare(Object arg0, Object arg1){
				Map<String,Object> A = (Map<String, Object>) arg0;
				Map<String,Object> B = (Map<String, Object>) arg1;
				Date At = (Date) A.get("workDate");
				Date Bt = (Date) B.get("workDate");
				return At.compareTo(Bt);
			}});
		}
		if(overdue!=null && overdue){
		  list = baseDao.getSession().selectList("teamGroupEstimateOverdue", search) ;
		  for(Map<String,Object> o:list){
		    int delay = getTimeDistance((Date)o.get("workDate"),new Date(((Timestamp)o.get("recordDate")).getTime()));
		    o.put("overDay", delay);
		  }
		}
		return list;
	}
	
	private int getTimeDistance(Date beginDate , Date endDate ) {
      Calendar beginCalendar = Calendar.getInstance();
      beginCalendar.setTime(beginDate);
      Calendar endCalendar = Calendar.getInstance();
      endCalendar.setTime(endDate);
      long beginTime = beginCalendar.getTime().getTime();
      long endTime = endCalendar.getTime().getTime();
      int betweenDays = (int)((endTime - beginTime) / (1000 * 60 * 60 *24));//先算出两时间的毫秒数之差大于一天的天数
      
      endCalendar.add(Calendar.DAY_OF_MONTH, -betweenDays);//使endCalendar减去这些天数，将问题转换为两时间的毫秒数之差不足一天的情况
      endCalendar.add(Calendar.DAY_OF_MONTH, -1);//再使endCalendar减去1天
      if(beginCalendar.get(Calendar.DAY_OF_MONTH)==endCalendar.get(Calendar.DAY_OF_MONTH))//比较两日期的DAY_OF_MONTH是否相等
          return betweenDays + 1; //相等说明确实跨天了
      else
          return betweenDays + 0; //不相等说明确实未跨天
  }

	//拼装加班工时日志信息
	private List<Map<String,Object>> changeOvertimeLogs(List<Map<String,Object>> taskList,List<Map<String,Object>> overLogs) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> tasks = addWorkNull(taskList, overLogs);
		for(Map<String,Object> o:overLogs){//拼接加班工时
			String key = o.get("account")+"_"+o.get("workDate");
			int count = 0;
			for(Map<String,Object> t:tasks){
				String tempKey = t.get("account")+"_"+t.get("workDate");
				if(key.equals(tempKey)){
					count ++;
					t.put("overestimate", o.get("overestimate"));
					t.put("GZSC", o.get("GZSC"));
					t.put("count", o.get("count"));
					t.put("flag", count==1?true:false);
					list.add(t);
				}
			}
		}
		return list;
	}

	private List<Map<String, Object>> addWorkNull(List<Map<String, Object>> taskList,List<Map<String, Object>> overLogs) {
		List<Map<String,Object>> tasks = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> overs = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> t:taskList){//去除非加班工时
			boolean flag = false;
			for(Map<String,Object> o:overLogs){
				if(t.get("workDate").equals(o.get("workDate"))){
					flag = true;
					break;
				}
			}
			if(flag){
				tasks.add(t);
			}else{
				Dao<WeekDay> dao = DaoFactory.create(WeekDay.class);
				WeekDay w = new WeekDay();
				w.setWorkdate((Date) t.get("workDate"));
				w = dao.selectOne(w);
				if(w==null){//工作日内无记录表示加班
					tasks.add(t);
					overs.add(t);
				}
			}
		}
		
		//未有工作量记录的工作时长添加至overLogs
		Map<String,List<Map<String,Object>>> map = new HashMap<String,List<Map<String,Object>>>();
		String tempKey = null;
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> o1:overs){
			String key = o1.get("account")+"_"+o1.get("workDate");
			if(tempKey == null||!tempKey.equals(key)){
				list = new ArrayList<Map<String,Object>>();
				map.put(key, list);
			}
			tempKey = key;
			list.add(o1);
		}
		
		for(String k:map.keySet()){
			List<Map<String,Object>> l = map.get(k);
			int count = 0;double GZSC = 0;
			for(Map<String,Object> o1:l){
				count++;
				GZSC += Double.parseDouble(o1.get("consumed").toString());
			}
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("account", l.get(0).get("account"));
			m.put("workDate", l.get(0).get("workDate"));
			m.put("count", count);
			m.put("GZSC", GZSC);
			m.put("overestimate", GZSC);
			overLogs.add(m);
		}
		
		return tasks;
	}

	public List<WhiteName> whiteNameList(){
		Dao<WhiteName> dao = DaoFactory.create(WhiteName.class);
		return dao.selectAll(new Sortable(new Order("orgId")));
	}
	
	/**
	 * 判断是否日志管理员角色
	 * @return boolean
	 */
	public boolean logAdminJob(){
		Object[] roles = (Object[])CredentialStore.getCurrCredential().getUserInfo().get("roles");
		for(Object s:roles){
			if(s.toString().equals(OrgUserConstant.LOGADMIN)){
				return true;
			}
		}
		return false;
	}
	
	public boolean betweenDate(Date d,Date start,Date end){
		return d.after(end)&d.before(start);
	}
	
	public List<Map<String,Object>> getTeamGroupUsers(String orgId,Date startDate,Date endDate){
		List<Map<String,Object>> allUser = new com.chinacreator.c2.dept.UserService().getOrgUsersFilterByWhiteList(orgId);
		if(allUser.size()==0) return new ArrayList<Map<String,Object>>();
		Map<String, Map<String,Object>> iconMap = DaoFactory.create(Member.class).getSession().selectMap("wrapIcon2UserDTO", allUser, "userName");
		
		if(startDate!=null && endDate!=null){
	
			for (int i = 0 ; i < allUser.size() ; i++) {
				Map<String,Object> u = allUser.get(i);
				if(u.get(OrgUserConstant.RZDATE)!=null){//过滤入职时间在查询结束时间后用户
					Long inDateMill = Long.parseLong(u.get(OrgUserConstant.RZDATE).toString());
					Date inDate = new Date(inDateMill);
					if(inDate.after(endDate)){
						allUser.remove(i);
						i--;
					}
				}
				if(Integer.parseInt(u.get(OrgUserConstant.ISVALID).toString())!=1){//1为有效
					if(u.get(OrgUserConstant.LZDATE)!=null){//过滤离职时间在查询起始时间前用户
						Long leaveDateMill = Long.parseLong(u.get(OrgUserConstant.LZDATE).toString());
						Date leaveDate = new Date(leaveDateMill);
						if(leaveDate.before(startDate)){
							allUser.remove(i);
							i--;
						}
					}else{
						allUser.remove(i);
						i--;
					} 
				}
				if(u.get(OrgUserConstant.USER_NAME)==null || !iconMap.containsKey(u.get(OrgUserConstant.USER_NAME))) continue ;
				u.put("remark1", iconMap.get(u.get(OrgUserConstant.USER_NAME)).get("icon")==null?"":iconMap.get(u.get(OrgUserConstant.USER_NAME)).get("icon").toString());
			}
		}
		
		
		return allUser;
	}
	
	//白名单列表
	public List<Map<String,String>> getWhiteList(){
		WebOperationContext context = (WebOperationContext) OperationContextHolder.getContext();
		String userId = context.getUser().getId();
		Dao<WhiteName> dao = DaoFactory.create(WhiteName.class);
		
		boolean isLogAdmin = logAdminJob();
		List<String> whiteUsernames = new ArrayList<String>();
		List<WhiteName> deptWhiteNameList = new ArrayList<WhiteName>();
		
		if(isLogAdmin){//日志管理员查询所有白名单
			List<WhiteName> list = dao.selectAll(new Sortable(new Order("orgId")));
			for(WhiteName w:list){
				com.chinacreator.c2.uop.User u = OrgUserService.queryUserByEmail(w.getUserName()+"@chinacreator.com");
				if(u!=null){
					deptWhiteNameList.add(w);
				}
			}
		}else{//查询本事业部白名单
			List<DynamicTreeNode> orgUsers = OrgUserService.queryOrgUsersByUserName(userId);
			List<String> departUser = new ArrayList<String>();
			for (DynamicTreeNode n : orgUsers) {
				if(n.get("userName")!=null)departUser.add(n.get("userName").toString());
			}
			//查询白名单表中userName相同的名字表
			deptWhiteNameList = dao.getSession().selectList("com.chinacreator.c2.workbench.WhiteNameMapper.selectWhiteNameByNameList", departUser);
		}
		
		for (WhiteName whiteName : deptWhiteNameList) {
			whiteUsernames.add(whiteName.getUserName());
		}
		
		List<Map<String,String>> whiteUsers = DaoFactory.create(Member.class).getSession().selectList("queryUsersByUsernames", whiteUsernames);
		for(Map<String,String> map:whiteUsers){
		    List<Organization> orgs = OrgUserService.queryOrgByEmail(map.get("userName")+"@chinacreator.com");
			if(orgs!=null&&orgs.size()>0){
			  String name = "";
			  for(Organization o:orgs){
			    name += o.getName()+",";
			  }
			  map.put("orgShowName", name.substring(0, name.length()-1));
			}
			
		}
		return whiteUsers;
	}
	
	//白名单管理树
	public List<DynamicTreeNode> whiteNameManageTree() {
		Dao<WhiteName> dao = DaoFactory.create(WhiteName.class);
		List<DynamicTreeNode> tree = OrgUserService.queryOrgUsers();
		List<WhiteName> whiteNameList = dao.selectAll();
		
		
		for(DynamicTreeNode node : tree) {
			if("org".equals(node.get("type"))){
				node.put("nocheck", true);
				continue ;
			}
			for(WhiteName w : whiteNameList) {
				if(null != node.get("userName") && w.getUserName().equals((String)node.get("userName"))) {
					node.put("checked", true);
					break;
				}
			}
		}
		
		return tree;
	}
}
