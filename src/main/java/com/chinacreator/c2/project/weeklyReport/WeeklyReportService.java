package com.chinacreator.c2.project.weeklyReport;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.dao.mybatis.enhance.Pageable;
import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;
import com.chinacreator.c2.project.info.Member;
import com.chinacreator.c2.project.info.Module;
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.project.task.Estimate;
import com.chinacreator.c2.project.task.Task;
import com.chinacreator.c2.project.weeklyReport.exception.WeekReportPersistenceException;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;
import com.google.common.collect.Lists;

public class WeeklyReportService {
	
	private static WeekReportPersisTool persisTool = WeekReportPersisTool.getINSTANCE() ;
	
	public List<Map<String,Object>> getWeekReportSubmit2(Integer projectId){
		return DaoFactory.create(WeeklyReportSubmit2.class).getSession().selectList("selectWRSubmitByProjectId", projectId) ;
	}
	
	/**
	 * 周报详情，包含上周计划
	 * @param reportId
	 * @return
	 */
	public Map<String,Object> getWeekReportInfo(Integer reportId){
		Map<String,Object> resultMap = new HashMap<String,Object>() ;
		WeeklyReport weeklyReport = DaoFactory.create(WeeklyReport.class).selectByID(reportId);
		resultMap.put("weeklyReport", weeklyReport);
		WeeklyReport param = new WeeklyReport();
		param.setProjectId(weeklyReport.getProjectId());
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, weeklyReport.getYear()); 
		cal.set(Calendar.WEEK_OF_YEAR, weeklyReport.getWeek()); 
		cal.set(Calendar.DAY_OF_WEEK, 7); // 1表示周日，2表示周一，7表示周六
		cal.setTimeInMillis(cal.getTimeInMillis()-7*24*60*60*1000);
		param.setYear(cal.get(Calendar.YEAR));//获得当前的年
		param.setWeek(cal.get(Calendar.WEEK_OF_YEAR));//获得当前日期属于今年的第几周
        
		WeeklyReport week = DaoFactory.create(WeeklyReport.class).selectOne(param);
		resultMap.put("preSummary", week!=null?week.getNextSummary():null);
		return resultMap;
	}
	
	/**
	 *  查询周报任务快照
	 *  
	 * @param start
	 * @param end
	 * @param reportId
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> getWeeklyReportTaskSnapshots(Map<String,Object> condition) {
		
		Map<String,Object> resultMap = new HashMap<String,Object>() ;
		Set<String> delayUserName = new HashSet<String>() ;
		Set<String> crossWeekUserName = new HashSet<String>() ;
		Set<String> normalUserName = new HashSet<String>() ;
		
		/*
		 * 遍历时间范围内的所有任务
		 * (1)计算当前项目工时总数和其他项目工时总数
		 * (2)按逾期、跨周、正常顺序对任务排序
		 * */
		if(condition.get("moduleId")==null){//该模块下的所有子模块
			condition.put("moduleId", 0);
		}
		getCasecadeModuleIds(condition);
		List<TaskSnapshotEx> taskSnapshots = DaoFactory.create(TaskSnapshotEx.class).getSession().selectList("selectWeeklyReportTaskSnapshots", condition) ;
		for(TaskSnapshotEx taskSnapshot : taskSnapshots){
			List<TaskSnapshotEx> taskSnapShotList = null ;
			String assignedToRealName = taskSnapshot.getUserRealname() ;
			//判断是否已存在该用户,没有则初始化之
			if(resultMap.containsKey(assignedToRealName)){
				taskSnapShotList = (List<TaskSnapshotEx>) ((Map<String,Object>)resultMap.get(assignedToRealName)).get("taskSnapShotList") ;
			}else{
				Map<String,Object> subResultMap = new HashMap<String,Object>() ;
				subResultMap.put("currentProjectEstimateCount", 0.0) ;
				subResultMap.put("otherProjectEstimateCount", 0.0) ;
				Map<String,Object> user = new HashMap<String,Object>() ;
				user.put("userName",taskSnapshot.getAssignedTo()) ;
				user.put("userRealname",assignedToRealName) ;
				user.put("remark4",taskSnapshot.getUserIcon()) ;
				subResultMap.put("userDTO", user) ;
				taskSnapShotList = new ArrayList<TaskSnapshotEx>() ;
				subResultMap.put("taskSnapShotList", taskSnapShotList) ;
				resultMap.put(assignedToRealName, subResultMap) ;
			}
			//截止日期在本周的任务若没完成则作逾期处理(1:正常;2:跨周;3:逾期)
			if(taskSnapshot.getStatus().equals("3")){ 
				delayUserName.add(assignedToRealName) ;
			}else if(taskSnapshot.getStatus().equals("2")){
				crossWeekUserName.add(assignedToRealName) ;
			}else{
				normalUserName.add(assignedToRealName) ;
			}
			
			//统计项目公示消耗总数,包括本项目和其他项目
			if(taskSnapshot.isCrossProject()){
				Double estimatCount = ((Double)((Map<String,Object>)resultMap.get(assignedToRealName)).get("otherProjectEstimateCount")) ;
				estimatCount += taskSnapshot.getWorked() ;
				((Map<String,Object>)resultMap.get(assignedToRealName)).put("otherProjectEstimateCount", estimatCount) ;
			}else{
				Double estimatCount = ((Double)((Map<String,Object>)resultMap.get(assignedToRealName)).get("currentProjectEstimateCount")) ;
				estimatCount += taskSnapshot.getWorked() ;
				((Map<String,Object>)resultMap.get(assignedToRealName)).put("currentProjectEstimateCount", estimatCount) ;
			}
			taskSnapShotList.add(taskSnapshot) ;
			((Map<String,Object>)resultMap.get(assignedToRealName)).put("taskSnapShotList", taskSnapShotList) ;
		}
		return sortByDelay(delayUserName,crossWeekUserName,normalUserName,resultMap);
	}
	
	//获取某模块的所有子模块id
	private void getCasecadeModuleIds(Map<String, Object> condition) {
		List<Integer> moduleIds = Lists.newArrayList();
		int moduleId = Integer.parseInt(condition.get("moduleId").toString());
		moduleIds.add(moduleId);
		
		//int projectId = Integer.parseInt(condition.get("projectId").toString());
		Dao<Module> modDao = DaoFactory.create(Module.class);
		List<Module> modList = modDao.getSession().selectList("getWeekReportModules", condition);
		//List<Module> modList = modDao.select(module);
		
		for (Module mods : modList) {
			//循环遍历找出所有的子数据
			if(mods.getParent().equals(moduleId)){
				moduleIds.add(mods.getId());
				getTreeNodes(mods.getId(), modList, moduleIds );
			}
		}
		condition.put("moduleIds", moduleIds);
	}
	
	public void getTreeNodes(Integer pid,List<Module> list,List<Integer> childList){
		if(list.size()>0){
			for (Module mod : list) {
				if(mod.getParent().equals(pid)){
					childList.add(mod.getId());
					getTreeNodes(mod.getId(), list,childList);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<Object> getWeeklyReportTasksOfNextWeek(Map<String,Object> startParam,Map<String,Object> endParam,Integer projectId,Integer reportId){
		Calendar startCal = Calendar.getInstance() ;
		Calendar endCal = Calendar.getInstance() ;
		startCal.setWeekDate(Integer.parseInt(startParam.get("year").toString()), Integer.parseInt(startParam.get("week").toString()), Calendar.MONDAY) ;
		endCal.setWeekDate(Integer.parseInt(endParam.get("year").toString()), Integer.parseInt(endParam.get("week").toString())+1, Calendar.SUNDAY) ;
		Date start = new Date(startCal.getTimeInMillis()) ;
		Date end = new Date(endCal.getTimeInMillis()) ;
		
		return (Collection<Object>) getWeeklyReportTasksOfNextWeek(start,end,projectId,reportId) ;
	}
	
	@SuppressWarnings("unchecked")
	public Object getWeeklyReportTasksOfNextWeek(Date start,Date end,Integer projectId,Integer reportId){
		Map<String,Object> condition = new HashMap<String,Object>() ;
		condition.put("start", start);
		condition.put("end", end);
		condition.put("projectId", projectId)  ;
		
		List<Map<String,Object>> taskSnapShotList = null ;
		Map<String,Object> resultMap = new HashMap<String, Object>() ;
		
		/*
		 * 遍历时间范围内的所有任务
		 * 此处传入的projectId只是为了查出项目里面所有的成员,而不是限定为改项目的任务
		 * */
		List<Object> objs = DaoFactory.create(Task.class).getSession().selectList("selectNextWeekTasksWithProjectId", condition) ;
		for(Object obj : objs){
			Map<String,Object> task = (Map<String, Object>) obj ;
			
			//无项目任务或非本项目任务不需要出现再下周计划中
			if(task.get("projectId")==null || Integer.parseInt(task.get("projectId").toString())!=projectId){
				continue ;
			}
			String assignedTo = task.get("assignedTo").toString() ;
			//判断是否已存在该用户,没有则初始化之
			if(resultMap.containsKey(assignedTo)){
				taskSnapShotList = (List<Map<String,Object>>) ((Map<String,Object>)resultMap.get(assignedTo)).get("taskSnapShotList") ;
			}else{
				Map<String,Object> subResultMap = new HashMap<String,Object>() ;
				subResultMap.put("currentProjectEstimateCount", 0.0) ;
				subResultMap.put("otherProjectEstimateCount", 0.0) ;
				Map<String,Object> user = new HashMap<String,Object>() ;
				user.put("userName",assignedTo) ;
				user.put("userRealname",task.get("assignedToRealName").toString()) ;
				user.put("remark1","1") ;
				subResultMap.put("userDTO", user) ;
				taskSnapShotList = new ArrayList<Map<String,Object>>() ;
				subResultMap.put("taskSnapShotList", taskSnapShotList) ;
				resultMap.put(assignedTo, subResultMap) ;
			}
			task.put("belongToNextWeek", true) ;
			task.put("crossProject", false) ;
			task.put("status", "1") ;
			task.put("worked",0.0) ;
			task.put("reportId",reportId) ;
			
			taskSnapShotList.add(task) ;
			((Map<String,Object>)resultMap.get(assignedTo)).put("taskSnapShotList", taskSnapShotList) ;
		}
		return resultMap.values();
	}
	
	/**
	 *  查询周报任务
	 * 
	 * @param start
	 * @param end
	 * @param projectId
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> getWeeklyReportTasks(Map<String,Object> startParam,Map<String,Object> endParam,Integer projectId,Integer reportId) throws IllegalAccessException, InvocationTargetException{
		Map<String,Object> condition = new HashMap<String,Object>() ;
		
		Calendar startCal = Calendar.getInstance() ;
		Calendar endCal = Calendar.getInstance() ;
		startCal.setWeekDate(Integer.parseInt(startParam.get("year").toString()), Integer.parseInt(startParam.get("week").toString()), Calendar.MONDAY) ;
		endCal.setWeekDate(Integer.parseInt(endParam.get("year").toString()), Integer.parseInt(endParam.get("week").toString())+1, Calendar.SUNDAY) ;
		Date start = new Date(startCal.getTimeInMillis()) ;
		Date end = new Date(endCal.getTimeInMillis()) ;
		
		condition.put("start", start);
		condition.put("end", end);
		condition.put("projectId", projectId)  ;
		Map<Integer,Double> estResult = new HashMap<Integer,Double>() ;
		//首先查start-end时间范围内的所有任务及工时总数
		for(Object est:DaoFactory.create(Estimate.class).getSession().selectList("selectRangeEstimateInfos", condition)){
			Map<String,Object> estMap = (Map<String,Object>)est ;
			estResult.put((Integer)estMap.get("taskId"), (Double)estMap.get("sum")) ;
		}
		Map<String,Object> resultMap = new HashMap<String, Object>() ;
		Set<String> delayUserName = new HashSet<String>() ;
		Set<String> crossWeekUserName = new HashSet<String>() ;
		Set<String> normalUserName = new HashSet<String>() ;
		
		/*
		 * 遍历时间范围内的所有任务
		 * 此处传入的projectId只是为了查出项目里面所有的成员,而不是限定为改项目的任务
		 * (1)计算当前项目工时总数和其他项目工时总数
		 * (2)按逾期、跨周、正常顺序对任务排序
		 * */
		List<Object> objs = DaoFactory.create(Task.class).getSession().selectList("selectRangeTasksWithProjectId", condition) ;
		for(Object obj : objs){
			Map<String,Object> task = (Map<String, Object>) obj ;
			
			List<Map<String,Object>> taskSnapShotList = null ;
			
			String assignedTo = task.get("assignedTo").toString() ;
			//判断是否已存在该用户,没有则初始化之
			if(resultMap.containsKey(assignedTo)){
				taskSnapShotList = (List<Map<String,Object>>) ((Map<String,Object>)resultMap.get(assignedTo)).get("taskSnapShotList") ;
			}else{
				Map<String,Object> subResultMap = new HashMap<String,Object>() ;
				subResultMap.put("currentProjectEstimateCount", 0.0) ;
				subResultMap.put("otherProjectEstimateCount", 0.0) ;
				Map<String,Object> user = new HashMap<String,Object>() ;
				user.put("userName",assignedTo) ;
				user.put("userRealname",task.get("assignedToRealName").toString()) ;
				subResultMap.put("userDTO", user) ;
				taskSnapShotList = new ArrayList<Map<String,Object>>() ;
				subResultMap.put("taskSnapShotList", taskSnapShotList) ;
				resultMap.put(assignedTo, subResultMap) ;
			}
			//将用户任务工时相加,最终求出当前、其他项目任务工时总数
			if(estResult.containsKey(task.get("taskId"))){
				task.put("worked", estResult.get(task.get("taskId"))) ;
				//统计项目公示消耗总数,包括本项目和其他项目
				String estimateKey = task.get("projectId")==projectId?"currentProjectEstimateCount":"otherProjectEstimateCount" ;
				Double estimatCount = ((Double)((Map<String,Object>)resultMap.get(assignedTo)).get(estimateKey)) ;
				estimatCount += (Double)estResult.get(task.get("taskId")) ;
				((Map<String,Object>)resultMap.get(assignedTo)).put(estimateKey, estimatCount) ;
			}else{
				task.put("worked", 0) ;
			}
			
			task.put("belongToNextWeek",false) ;
			task.put("reportId",reportId) ;
			
			//区分当前项目任务及其他项目任务
			if(!task.get("projectId").equals(projectId)){
				task.put("crossProject", true) ;
				task.put("status", "1") ;
			}else{
				//截止日期在本周的任务若没完成则作逾期处理(1:正常;2:跨周;3:逾期)
				Date deadline=(Date) task.get("deadline"); 
				Boolean isDone = (Boolean) task.get("isDone");
				//截止日期为空时默认状态为正常
				if(deadline==null || isDone){
					normalUserName.add(assignedTo) ;
					task.put("status", "1") ;
				}else if(deadline.before(end) && !isDone){//已经到了截止日期还没有完成的
					delayUserName.add(assignedTo) ;
					task.put("status", "3") ;
				}else if(deadline.after(end) && !isDone){
					crossWeekUserName.add(assignedTo) ;
					task.put("status", "2") ;
				}
				task.put("crossProject", false) ;
			}
			
			taskSnapShotList.add(task) ;
			((Map<String,Object>)resultMap.get(assignedTo)).put("taskSnapShotList", taskSnapShotList) ;
		}
		
		Map<String,Object> result = new HashMap<String, Object>() ;
		result.put("currentWeekTasks", sortByDelay(delayUserName,crossWeekUserName,normalUserName,resultMap)) ;
		result.put("nextWeekTasks", 
				getWeeklyReportTasksOfNextWeek(
						calendarAdd(start,GregorianCalendar.WEEK_OF_YEAR,1),
						calendarAdd(end,GregorianCalendar.WEEK_OF_YEAR,1),
						projectId,reportId)) ;
		return result ;
	} 
	
	@SuppressWarnings("deprecation")
	private Date calendarAdd(Date date,int field,int addNum){
		GregorianCalendar nextDate = new GregorianCalendar(date.getYear()+1900,date.getMonth(),date.getDate()) ;
		nextDate.add(field, addNum) ;
		return new Date(nextDate.getTimeInMillis());
	}
	
	/**
	 *  任务快照排序
	 * 
	 * @param delayUserName
	 * @param crossWeekUserName
	 * @param normalUserName
	 * @param resultMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String,Object>> sortByDelay(Set<String> delayUserName, Set<String> crossWeekUserName, Set<String> normalUserName, Map<String, Object> resultMap) {
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>() ;
		//成员数量固定,所以此处时间复杂度为N,可以接受
		for(String userName:delayUserName){
			Map<String,Object> map = (Map<String, Object>) resultMap.get(userName) ;
			((Map<String,Object>)map.get("userDTO")).put("remark1","3") ;
			resultList.add(map) ;
			resultMap.remove(userName) ;
		}
		for(String userName:crossWeekUserName){
			if(resultMap.containsKey(userName)){
				Map<String,Object> map = (Map<String, Object>) resultMap.get(userName) ;
				((Map<String,Object>)map.get("userDTO")).put("remark1","2") ;
				resultList.add(map) ;
				resultMap.remove(userName) ;
			}
		}
		for(String userName:normalUserName){
			if(resultMap.containsKey(userName)){
				Map<String,Object> map = (Map<String, Object>) resultMap.get(userName) ;
				((Map<String,Object>)map.get("userDTO")).put("remark1","1") ;
				resultList.add(map) ;
			}
		}
		return resultList;
	}
	
	public Map<String,Object> getSingleWeeklyReportTask(Integer taskId,Integer reportId,Date start,Date end){
		Map<String, Object> condition = new HashMap<String,Object>();
		condition.put("taskId", taskId) ;
		condition.put("start", start) ;
		condition.put("end", end) ;
		condition.put("reportId", reportId) ;
		
		List<Map<String,Object>> results = DaoFactory.create(Task.class).getSession().selectList("selectSingleWeeklyReportTask", condition) ;
		if(results.size()!=0){
			Map<String, Object> result = (Map<String, Object>) results.get(0) ;
			result.put("belongToNextWeek", true) ;
			result.put("crossProject", false) ;
			result.put("status", "1") ;
			result.put("worked",0.0) ;
			result.put("reportId",reportId) ;
			return result ;
		}
		return null;
	}

	public List<String> getUserNamesByProjectId(Integer projectId){
		return DaoFactory.create(Member.class).getSession().selectList("selectUserNamesByProjectId", projectId) ;
	}
	
	public List<TaskSnapshot> getTaskSnapshots(TaskSnapshot snapshot){
		return DaoFactory.create(TaskSnapshot.class).select(snapshot) ;
	}
	
	/**
	 *  生成缺失的周报(若有缺失),返回周报列表
	 * 
	 * @param reportArr 
	 */
	public Page<Map<String,Object>> createWeeklyReportIfLack(Integer projectId,Pageable pageable){
		try {
			List<WeeklyReport> lackWeeklyReport = new ArrayList<WeeklyReport>() ;
			Calendar currentDate = Calendar.getInstance();
			Calendar recentWrDate = Calendar.getInstance() ;
			
			WeeklyReport recentWeeklyReport = DaoFactory.create(WeeklyReport.class).getSession()
			.selectOne("com.chinacreator.c2.project.weeklyReport.WeeklyReportMapper.selectMaxWeekNum", projectId);
			
			int week=0;
			int year=0;
			
			if(recentWeeklyReport==null){//新项目
				Project project = new Project() ;
				project.setId(projectId) ;
				project = DaoFactory.create(Project.class).selectByID(project);
				recentWrDate.setTimeInMillis(project.getCreateDate().getTime());
				week = recentWrDate.get(Calendar.WEEK_OF_YEAR) ;
				year = recentWrDate.get(Calendar.YEAR) ;
				WeeklyReport weeklyReport = new WeeklyReport();
				weeklyReport.setYear(year) ;
				weeklyReport.setWeek(week) ;
				weeklyReport.setProjectId(projectId) ;
				weeklyReport.setStatus("缺失") ;
				lackWeeklyReport.add(weeklyReport) ;
			}else{
				week = recentWeeklyReport.getWeek() ;
				year = recentWeeklyReport.getYear() ;
			}
			
			recentWrDate.setWeekDate(year, week, Calendar.FRIDAY) ;
			if(currentDate.after(recentWrDate)){
				do{
					week++ ;
					recentWrDate.setWeekDate(year, week, Calendar.FRIDAY) ;
					if(recentWrDate.get(Calendar.YEAR)>year){//跨年了,重置周数
						year = recentWrDate.get(Calendar.YEAR) ;
						week = 1 ;
						recentWrDate.setWeekDate(year, week, Calendar.FRIDAY) ;
					}
					WeeklyReport weeklyReport = new WeeklyReport();
					weeklyReport.setYear(year) ;
					weeklyReport.setWeek(week) ;
					weeklyReport.setProjectId(projectId) ;
					weeklyReport.setStatus("缺失") ;
					lackWeeklyReport.add(weeklyReport) ;
				}while(recentWrDate.compareTo(currentDate)<0) ;
			}
			
			List<Map<String,Object>> authorInfos = getWeeklyReportProjectAuthorInfos(null,null,projectId) ; 
			String userProjectRelationship = null;

			if(lackWeeklyReport!=null && lackWeeklyReport.size()!=0){
				persisTool.createLackWeeklyReport(lackWeeklyReport) ;
			}
			Map<String,Object> condition = new HashMap<String,Object>() ;
			WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
			User user = OrgUserService.getUserInfoById(context.getUser().getId());
			String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
			Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
			condition.put("currentUserId", uu.get("user_id").toString());
			condition.put("projectId", projectId) ;
			if(authorInfos.size()!=0)userProjectRelationship = authorInfos.get(0).get("userProjectRelationship").toString();
			condition.put("userProjectRelationship", userProjectRelationship) ;
			RowBounds4Page page = new RowBounds4Page(pageable, null, null, true) ;
			List<Map<String,Object>> list = DaoFactory.create(WeeklyReport.class).getSession()
					.selectList("com.chinacreator.c2.project.weeklyReport.WeeklyReportMapper.selectWeeklyReport", condition, page) ;
			
			for (Map<String, Object> weeklyReport : list) {
				if(weeklyReport.containsKey("year") && weeklyReport.containsKey("week")){
					Calendar startCal = Calendar.getInstance() ;
					Calendar endCal = Calendar.getInstance() ;
					startCal.setWeekDate(Integer.parseInt(weeklyReport.get("year").toString()), Integer.parseInt(weeklyReport.get("week").toString()), Calendar.MONDAY) ;
					endCal.setWeekDate(Integer.parseInt(weeklyReport.get("year").toString()), Integer.parseInt(weeklyReport.get("week").toString())+1, Calendar.SUNDAY) ;
					weeklyReport.put("startDate", addZero(startCal.get(Calendar.MONTH)+1,2)+"/"+addZero(startCal.get(Calendar.DATE),2)) ;
					weeklyReport.put("endDate", addZero(endCal.get(Calendar.MONTH)+1,2)+"/"+addZero(endCal.get(Calendar.DATE),2)) ;
					weeklyReport.put("week", addZero(Integer.parseInt(weeklyReport.get("week").toString()),2)) ;
				}
			}
			
			return new Page<Map<String,Object>>(pageable.getPageIndex(), 
													 pageable.getPageSize(), 
													 page.getTotalSize(), 
													 list) ;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WeekReportPersistenceException("缺失周报生成过程中出现异常..."+e.getCause().getMessage()) ;
		}
	}
	
	/**
	 *  提交周报信息
	 * 
	 * @param report
	 * @param tasks
	 */
	public void submitWeeklyReport(WeeklyReport report,List<TaskSnapshot> tasks){
		try {
			persisTool.submitWeeklyReport(report, tasks) ;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WeekReportPersistenceException("周报提交过程中出现异常..."+e.getCause().getMessage()) ;
		}
	}
	
	/**
	 *  获取周报列表
	 * 
	 * @param pageable 分页对象
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public Page<Map<String,Object>> getWeeklyReportList(Pageable pageable) throws IllegalAccessException, InvocationTargetException{

		RowBounds4Page page = new RowBounds4Page(pageable, null, null, true) ;
		Map<String,Object> condition = new HashMap<String,Object>() ;
		//查询所有有权限看到的项目周报,包括提交给及参与的项目
		//项目成员无法查看设置了“管理员可见”的周报
		condition.put("statusFilter", "缺失") ;
		List<Map<String, Object>> infos = getWeeklyReportProjectAuthorInfos(null,null,null);
		if(infos.size()==0) return null ;
		condition.put("projectAuthorInfos", infos) ;
		
		List<Map<String,Object>> list = DaoFactory.create(WeeklyReport.class).getSession()
				.selectList("com.chinacreator.c2.project.weeklyReport.WeeklyReportMapper.selectWeeklyReport", condition, page) ;
		
		for (Map<String, Object> weeklyReport : list) {
			if(weeklyReport.containsKey("year") && weeklyReport.containsKey("week")){
				Calendar startCal = Calendar.getInstance() ;
				Calendar endCal = Calendar.getInstance() ;
				startCal.setWeekDate(Integer.parseInt(weeklyReport.get("year").toString()), Integer.parseInt(weeklyReport.get("week").toString()), Calendar.MONDAY) ;
				endCal.setWeekDate(Integer.parseInt(weeklyReport.get("year").toString()), Integer.parseInt(weeklyReport.get("week").toString())+1, Calendar.SUNDAY) ;
				weeklyReport.put("startDate", addZero(startCal.get(Calendar.MONTH)+1,2)+"/"+addZero(startCal.get(Calendar.DATE),2)) ;
				weeklyReport.put("endDate", addZero(endCal.get(Calendar.MONTH)+1,2)+"/"+addZero(endCal.get(Calendar.DATE),2)) ;
				weeklyReport.put("week", addZero(Integer.parseInt(weeklyReport.get("week").toString()),2)) ;
			}
		}
		return new Page<Map<String,Object>>(pageable.getPageIndex(), 
												 pageable.getPageSize(), 
												 page.getTotalSize(), 
												 list) ;
	}
	
	private String addZero(int num, int len) {  
		 StringBuilder s = new StringBuilder();  
		 s.append(num);  
		 while (s.length() < len) {       
		   s.insert(0, "0");           
		 }  
		 return s.toString();  
	}  
	
	
	/**
	 *  通过项目id获取周报列表
	 * 
	 * @param projectId 项目id
	 * @param pageable 分页对象
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public Page<Map<String,Object>> getWeeklyReportListByProjectId(Integer projectId,Pageable pageable,String statusFilter) throws IllegalAccessException, InvocationTargetException{
		
		List<Map<String, Object>> projectAuthorInfos = getWeeklyReportProjectAuthorInfos(null,null,projectId);
		
		Map<String,Object> condition = new HashMap<String,Object>() ;
		
		RowBounds4Page page = new RowBounds4Page(pageable, null, null, true) ;
		condition.put("projectId", projectId) ;
		condition.put("projectAuthorInfos", projectAuthorInfos) ;
		condition.put("statusFilter", statusFilter) ;
		
		List<Map<String,Object>> list = DaoFactory.create(WeeklyReport.class).getSession()
				.selectList("com.chinacreator.c2.project.weeklyReport.WeeklyReportMapper.selectWeeklyReport", condition, page) ;
		return new Page<Map<String,Object>>(pageable.getPageIndex(), 
												 pageable.getPageSize(), 
												 page.getTotalSize(), 
												 list) ;
	}
	
	/**
	 *  获取项目任务周报最大周数,用于判断是否需要生成周报,提高点效率 
	 * 
	 * @param projectId
	 * @return
	 */
	public WeeklyReport getMaxWeekNumOfProject(Integer projectId){
		return DaoFactory.create(WeeklyReport.class).getSession()
				.selectOne("com.chinacreator.c2.project.weeklyReport.WeeklyReportMapper.selectMaxWeekNum", projectId) ;
	}
	
	public void setWeekReportSend2(List<WeeklyReportSubmit2> weeklyReportSubmit2,Integer projectId){
		try {
			persisTool.addWeekReportSend2(weeklyReportSubmit2,projectId) ;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WeekReportPersistenceException("编辑周报提交给过程中出现异常..."+e.getCause().getMessage()) ;
		}
	}
	
	/**
	 * 查询当前用户可访问项目周报的信息(发送给、管理者、成员)
	 * 
	 * @param week 周数 
	 * @param projectId 项目id
	 * @return
	 */
	public List<Map<String,Object>> getWeeklyReportProjectAuthorInfos(Integer week,Integer year,Integer projectId){
		Map<String,Object> condition = new HashMap<String,Object>() ;
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
		condition.put("currentUserId", uu.get("user_id").toString());
		condition.put("week", week);
		condition.put("year", year);
		condition.put("projectId", projectId) ;
		return DaoFactory.create(WeeklyReport.class).getSession()
				.selectList("com.chinacreator.c2.project.weeklyReport.WeeklyReportMapper.getCurrtentMemberWRProjectInfos", 
						condition);
	}
	
}
