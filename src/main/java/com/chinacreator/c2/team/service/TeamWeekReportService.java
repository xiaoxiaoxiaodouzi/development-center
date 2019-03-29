package com.chinacreator.c2.team.service;


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
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.dao.mybatis.enhance.Pageable;
import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.project.task.Estimate;
import com.chinacreator.c2.project.task.Task;
import com.chinacreator.c2.project.weeklyReport.WeeklyReport;
import com.chinacreator.c2.project.weeklyReport.exception.WeekReportPersistenceException;
import com.chinacreator.c2.team.Team;
import com.chinacreator.c2.team.TeamTaskSnapshot;
import com.chinacreator.c2.team.TeamWeekReport;
import com.chinacreator.c2.team.TeamWeekreportStatistics;
import com.chinacreator.c2.uop.OrgUserConstant;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;

@Service
public class TeamWeekReportService {

  /**
   * 生成团队周报列表（若有缺失）
   * @param page
   * @param owner
   * @param teamId
   * @return
   */
  public Page<Map<String,Object>> getListByPage(int pageIndex, String owner, int teamId) {
    try {
      List<TeamWeekReport> lackWeeklyReport = new ArrayList<TeamWeekReport>();
      Calendar currentDate = Calendar.getInstance();
      Calendar recentWrDate = Calendar.getInstance();

      TeamWeekReport recentWeeklyReport = DaoFactory.create(TeamWeekReport.class).getSession()
          .selectOne("com.chinacreator.c2.team.TeamWeekReportMapper.selectMaxWeekNum",teamId);

      int week = 0;
      int year = 0;

      if (recentWeeklyReport == null) {// 新项目
        Team team = new Team();
        team.setId(teamId);
        team = DaoFactory.create(Team.class).selectByID(team);
        recentWrDate.setTimeInMillis(team.getCreateTime().getTime());
        week = recentWrDate.get(Calendar.WEEK_OF_YEAR);
        year = recentWrDate.get(Calendar.YEAR);
        TeamWeekReport weeklyReport = new TeamWeekReport();
        weeklyReport.setYear(year);
        weeklyReport.setWeek(week);
        weeklyReport.setTeamId(teamId);
        weeklyReport.setStatus("缺失");
        lackWeeklyReport.add(weeklyReport);
      } else {
        week = recentWeeklyReport.getWeek();
        year = recentWeeklyReport.getYear();
      }

      recentWrDate.setWeekDate(year, week, Calendar.FRIDAY);
      if (currentDate.after(recentWrDate)) {
        do {
          week++;
          recentWrDate.setWeekDate(year, week, Calendar.FRIDAY);
          if (recentWrDate.get(Calendar.YEAR) > year) {// 跨年了,重置周数
            year = recentWrDate.get(Calendar.YEAR);
            week = 1;
            recentWrDate.setWeekDate(year, week, Calendar.FRIDAY);
          }
          TeamWeekReport weeklyReport = new TeamWeekReport();
          weeklyReport.setYear(year);
          weeklyReport.setWeek(week);
          weeklyReport.setTeamId(teamId);
          weeklyReport.setStatus("缺失");
          lackWeeklyReport.add(weeklyReport);
        } while (recentWrDate.compareTo(currentDate) < 0);
      }

      if(lackWeeklyReport!=null && lackWeeklyReport.size()!=0){
        createLackWeeklyReport(lackWeeklyReport) ;
      }
      
      String userTeamRelationship = null;

      Map<String,Object> condition = new HashMap<String,Object>() ;
      List<Map<String,Object>> authorInfos = getWeeklyReportAuthorInfos(owner,teamId) ; 
      condition.put("teamId", teamId) ;
      condition.put("currentUserName", owner);
      if(authorInfos.size()!=0)userTeamRelationship = authorInfos.get(0).get("userTeamRelationship").toString();
      condition.put("userTeamRelationship", userTeamRelationship) ;
      Pageable pageable = new Pageable(pageIndex);
      RowBounds4Page page = new RowBounds4Page(pageable, null, null, true) ;
      List<Map<String,Object>> list = DaoFactory.create(WeeklyReport.class).getSession()
              .selectList("com.chinacreator.c2.team.TeamWeekReportMapper.selectWeeklyReport", condition, page) ;
      

      for (Map<String, Object> weeklyReport : list) {
        if (weeklyReport.containsKey("year") && weeklyReport.containsKey("week")) {
          Calendar startCal = Calendar.getInstance();
          Calendar endCal = Calendar.getInstance();
          startCal.setWeekDate(Integer.parseInt(weeklyReport.get("year").toString()),
              Integer.parseInt(weeklyReport.get("week").toString()), Calendar.MONDAY);
          endCal.setWeekDate(Integer.parseInt(weeklyReport.get("year").toString()),
              Integer.parseInt(weeklyReport.get("week").toString()) + 1, Calendar.SUNDAY);
          weeklyReport.put("startDate", addZero(startCal.get(Calendar.MONTH) + 1, 2) + "/"
              + addZero(startCal.get(Calendar.DATE), 2));
          weeklyReport.put("endDate", addZero(endCal.get(Calendar.MONTH) + 1, 2) + "/"
              + addZero(endCal.get(Calendar.DATE), 2));
          weeklyReport.put("week",
              addZero(Integer.parseInt(weeklyReport.get("week").toString()), 2));
        }
      }

      return new Page<Map<String, Object>>(pageable.getPageIndex(), pageable.getPageSize(),
          page.getTotalSize(), list);
    } catch (Exception e) {
      e.printStackTrace();
      throw new WeekReportPersistenceException("缺失周报生成过程中出现异常..." + e.getCause().getMessage());
    }

  }

  public List<Map<String, Object>> getWeeklyReportAuthorInfos(String currentUserName, Integer teamId) {
    Map<String,Object> condition = new HashMap<String,Object>() ;
    condition.put("currentUserName", currentUserName);
    condition.put("teamId", teamId) ;
    return DaoFactory.create(TeamWeekReport.class).getSession()
            .selectList("com.chinacreator.c2.team.TeamWeekReportMapper.getCurrtentMemberWRTeamInfos", 
                    condition);
  }

  /**
   * 创建缺失周报
   * @param lackWeeklyReport
   */
  private void createLackWeeklyReport(List<TeamWeekReport> lackWeeklyReport) {
    DaoFactory.create(TeamWeekReport.class).insertBatch(lackWeeklyReport) ;
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
   * 获取单个周报详情
   * @param reportId
   * @return
   */
  public Map<String, Object> getWeekReportInfo(String reportId) {
    Map<String,Object> resultMap = new HashMap<String,Object>() ;
    TeamWeekReport weeklyReport = DaoFactory.create(TeamWeekReport.class).selectByID(reportId);
    resultMap.put("weeklyReport", weeklyReport);
    TeamWeekReport param = new TeamWeekReport();
    param.setTeamId(weeklyReport.getTeamId());
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, weeklyReport.getYear()); 
    cal.set(Calendar.WEEK_OF_YEAR, weeklyReport.getWeek()); 
    cal.set(Calendar.DAY_OF_WEEK, 7); // 1表示周日，2表示周一，7表示周六
    cal.setTimeInMillis(cal.getTimeInMillis()-7*24*60*60*1000);
    param.setYear(cal.get(Calendar.YEAR));//获得当前的年
    param.setWeek(cal.get(Calendar.WEEK_OF_YEAR));//获得当前日期属于今年的第几周
    
    TeamWeekReport week = DaoFactory.create(TeamWeekReport.class).selectOne(param);
    resultMap.put("preSummary", week!=null?week.getNextSummary():null);
    return resultMap;
  }

  /**
   * 查询周报任务快照
   * @param reportId
   * @param belongToNextWeek
   * @param teamId
   * @return
   */
  public List<Map<String, Object>> getWeekReportTaskSnapshots(String reportId, boolean belongToNextWeek,
      String teamId) {
    Map<String,Object> resultMap = new HashMap<String,Object>() ;
    Set<String> delayUserName = new HashSet<String>() ;
    Set<String> crossWeekUserName = new HashSet<String>() ;
    Set<String> normalUserName = new HashSet<String>() ;
    
    /*
     * 遍历时间范围内的所有任务
     * (1)计算当前项目工时总数和其他项目工时总数
     * (2)按逾期、跨周、正常顺序对任务排序
     * */
    Map<String,Object> condition = new HashMap<String,Object>();
    condition.put("reportId", reportId);
    condition.put("belongToNextWeek", belongToNextWeek);
    condition.put("teamId", teamId);
    List<TeamTaskSnapshot> taskSnapshots = DaoFactory.create(TeamTaskSnapshot.class).getSession().selectList("selectTeamWeeklyReportTaskSnapshots", condition) ;
    for(TeamTaskSnapshot taskSnapshot : taskSnapshots){
        List<TeamTaskSnapshot> taskSnapShotList = null ;
        String assignedToRealName = taskSnapshot.getUserRealname() ;
        //判断是否已存在该用户,没有则初始化之
        if(resultMap.containsKey(assignedToRealName)){
            taskSnapShotList = (List<TeamTaskSnapshot>) ((Map<String,Object>)resultMap.get(assignedToRealName)).get("taskSnapShotList") ;
        }else{
            Map<String,Object> subResultMap = new HashMap<String,Object>() ;
            Map<String,Object> user = new HashMap<String,Object>() ;
            user.put("userName",taskSnapshot.getAssignedTo()) ;
            user.put("userRealname",assignedToRealName) ;
            user.put("remark4",taskSnapshot.getUserIcon()) ;
            subResultMap.put("userDTO", user) ;
            taskSnapShotList = new ArrayList<TeamTaskSnapshot>() ;
            subResultMap.put("taskSnapShotList", taskSnapShotList) ;
            resultMap.put(assignedToRealName, subResultMap) ;
        }
        //截止日期在本周的任务若没完成则作逾期处理(1:正常;2:跨周;3:逾期)
        if(taskSnapshot.equals("3")){ 
            delayUserName.add(assignedToRealName) ;
        }else if(taskSnapshot.getStatus().equals("2")){
            crossWeekUserName.add(assignedToRealName) ;
        }else{
            normalUserName.add(assignedToRealName) ;
        }
        
        taskSnapShotList.add(taskSnapshot) ;
        ((Map<String,Object>)resultMap.get(assignedToRealName)).put("taskSnapShotList", taskSnapShotList) ;
    }
    return sortByDelay(delayUserName,crossWeekUserName,normalUserName,resultMap);
  }

  private List<Map<String, Object>> sortByDelay(Set<String> delayUserName,
      Set<String> crossWeekUserName, Set<String> normalUserName, Map<String, Object> resultMap) {
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

  /**
   * 获取下周任务
   * @param reportId
   * @param start
   * @param end
   * @param teamId
   * @return
   */
  public Collection<Object> getNextWeekTask(String reportId, String startParam, String endParam,
      int teamId) {
    Calendar startCal = Calendar.getInstance() ;
    Calendar endCal = Calendar.getInstance() ;
    startCal.setWeekDate(Integer.parseInt(startParam.split("-")[0]), Integer.parseInt(startParam.split("-")[1]), Calendar.MONDAY) ;
    endCal.setWeekDate(Integer.parseInt(endParam.split("-")[0]), Integer.parseInt(endParam.split("-")[1])+1, Calendar.SUNDAY) ;
    Date start = new Date(startCal.getTimeInMillis()) ;
    Date end = new Date(endCal.getTimeInMillis()) ;
    
    return (Collection<Object>) getWeeklyReportTasksOfNextWeek(start,end,teamId,reportId) ;
  }

  private Collection<Object> getWeeklyReportTasksOfNextWeek(Date start, Date end, int teamId,
      String reportId) {
    Map<String,Object> condition = new HashMap<String,Object>() ;
    condition.put("start", start);
    condition.put("end", end);
    condition.put("teamId", teamId)  ;
    
    List<Map<String,Object>> taskSnapShotList = null ;
    Map<String,Object> resultMap = new HashMap<String, Object>() ;
    
    /*
     * 遍历时间范围内的所有任务
     * 此处传入的projectId只是为了查出项目里面所有的成员,而不是限定为改项目的任务
     * */
    List<Object> objs = DaoFactory.create(Task.class).getSession().selectList("selectNextWeekTasksWithTeamId", condition) ;
    for(Object obj : objs){
        Map<String,Object> task = (Map<String, Object>) obj ;
        
        //无项目任务或非本项目任务不需要出现再下周计划中
        if(task.get("projectId")==null){
            continue ;
        }
        String assignedTo = task.get("assignedTo").toString() ;
        //判断是否已存在该用户,没有则初始化之
        if(resultMap.containsKey(assignedTo)){
            taskSnapShotList = (List<Map<String,Object>>) ((Map<String,Object>)resultMap.get(assignedTo)).get("taskSnapShotList") ;
        }else{
            Map<String,Object> subResultMap = new HashMap<String,Object>() ;
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
        task.put("status", "1") ;
        task.put("worked",0.0) ;
        task.put("reportId",reportId) ;
        
        taskSnapShotList.add(task) ;
        ((Map<String,Object>)resultMap.get(assignedTo)).put("taskSnapShotList", taskSnapShotList) ;
    }
    return resultMap.values();
  }

  /**
   * 获取周报任务
   * @param reportId
   * @param start
   * @param end
   * @param teamId
   * @return
   */
  public Map<String,Object> getWeeklyReportTasks(String reportId, String startParam,
      String endParam, int teamId) {
    Map<String,Object> condition = new HashMap<String,Object>() ;
    
    Calendar startCal = Calendar.getInstance() ;
    Calendar endCal = Calendar.getInstance() ;
    startCal.setWeekDate(Integer.parseInt(startParam.split("-")[0]), Integer.parseInt(startParam.split("-")[1]), Calendar.MONDAY) ;
    endCal.setWeekDate(Integer.parseInt(endParam.split("-")[0]), Integer.parseInt(endParam.split("-")[1])+1, Calendar.SUNDAY) ;
    Date start = new Date(startCal.getTimeInMillis()) ;
    Date end = new Date(endCal.getTimeInMillis()) ;
    
    condition.put("start", start);
    condition.put("end", end);
    condition.put("teamId", teamId)  ;
    Map<Integer,Double> estResult = new HashMap<Integer,Double>() ;
    //首先查start-end时间范围内的所有任务及工时总数
    for(Object est:DaoFactory.create(Estimate.class).getSession().selectList("selectTeamRangeEstimateInfos", condition)){
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
    List<Object> objs = DaoFactory.create(TeamTaskSnapshot.class).getSession().selectList("selectRangeTasksWithTeamId", condition) ;
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
            /*//统计项目公示消耗总数,包括本项目和其他项目
            String estimateKey = "currentProjectEstimateCount";
            Double estimatCount = ((Double)((Map<String,Object>)resultMap.get(assignedTo)).get(estimateKey)) ;
            estimatCount += (Double)estResult.get(task.get("taskId")) ;
            ((Map<String,Object>)resultMap.get(assignedTo)).put(estimateKey, estimatCount) ;*/
        }else{
            task.put("worked", 0) ;
        }
        
        task.put("belongToNextWeek",false) ;
        task.put("reportId",reportId) ;
        
        //区分当前项目任务及其他项目任务
        /*if(!task.get("projectId").equals(teamId)){
            task.put("crossProject", true) ;
            task.put("status", "1") ;
        }else{*/
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
            //task.put("crossProject", false) ;
        //}
        
        taskSnapShotList.add(task) ;
        ((Map<String,Object>)resultMap.get(assignedTo)).put("taskSnapShotList", taskSnapShotList) ;
    }
    
    Map<String,Object> result = new HashMap<String, Object>() ;
    result.put("currentWeekTasks", sortByDelay(delayUserName,crossWeekUserName,normalUserName,resultMap)) ;
    result.put("nextWeekTasks", 
            getWeeklyReportTasksOfNextWeek(
                    calendarAdd(start,GregorianCalendar.WEEK_OF_YEAR,1),
                    calendarAdd(end,GregorianCalendar.WEEK_OF_YEAR,1),
                    teamId,reportId)) ;
    return result ;
  }

  private Date calendarAdd(Date date,int field,int addNum) {
    GregorianCalendar nextDate = new GregorianCalendar(date.getYear()+1900,date.getMonth(),date.getDate()) ;
    nextDate.add(field, addNum) ;
    return new Date(nextDate.getTimeInMillis());
  }

  /**
   * 创建团队周报
   * @param teamId
   * @param params
   */
  public void addTeamWeekReport(int teamId, Map<String, Object> params) {
    TeamWeekReport report = JSONObject.toJavaObject((JSONObject) params.get("report"), TeamWeekReport.class);
    List<TeamTaskSnapshot> tasks = JSONArray.parseArray(((JSONArray)params.get("tasks")).toJSONString(), TeamTaskSnapshot.class);
    TeamWeekreportStatistics statistics=new TeamWeekreportStatistics();
    
    double estimate=0;
    double consumed=0;
    int total=0;
    int completed=0;
    int canceled=0;
    double worked=0;
    int delay = 0 ;
    int crossWeek = 0 ;
    
    Set<String> members=new HashSet<String>();
    
    for(TeamTaskSnapshot snapshot:tasks){
        if(snapshot.isBelongToNextWeek()) continue ;
        total++ ;
        snapshot.setReportId(report.getId()) ;
        members.add(snapshot.getAssignedTo());
        estimate+=(double)snapshot.getEstimate();
        consumed+=(double)snapshot.getConsumed();
        worked+=(double)snapshot.getWorked();
        if(snapshot.isIsDone()){
            completed++;
        }
        //逾期任务
        if(snapshot.getStatus().equals("3")){
            delay++ ;
        }
        //跨周任务
        if(snapshot.getStatus().equals("2")){
            crossWeek++ ;
        }
    }
    
    statistics.setDelayed(delay) ;
    statistics.setCrossWeek(crossWeek) ;
    statistics.setCanceled(canceled);
    statistics.setCompleted(completed);
    statistics.setConsumed(consumed);
    statistics.setWorked(worked);
    statistics.setEstimate(estimate);
    statistics.setMember(members.size());
    statistics.setTotal(total);
    
    Dao<TeamWeekreportStatistics> statisticDao=DaoFactory.create(TeamWeekreportStatistics.class);
    statisticDao.insert(statistics);
    
    report.setStatistics(statistics);
    Dao<TeamWeekReport> reportDao=DaoFactory.create(TeamWeekReport.class);
    reportDao.update(report);
    
    Dao<TeamTaskSnapshot> snapshotDao=DaoFactory.create(TeamTaskSnapshot.class);
    snapshotDao.insertBatch(tasks) ;
  }

  /**
   * 获取周报日志
   * @param reportId
   * @param st
   * @param et
   * @param teamId
   * @return
   */
  public Collection<Object> getWeeklyReportLogs(String reportId, String st, String et, int teamId) {
    Dao<Object> baseDao = DaoFactory.create(Object.class);
    Map<String,Object> resultMap = new HashMap<String, Object>() ;
    Map<String,Object> weekMap = new HashMap<String, Object>() ;
    if(StringUtils.isNotBlank(et)){
        Calendar endCal = Calendar.getInstance() ;
        endCal.setWeekDate(Integer.parseInt(et.split("-")[0]), Integer.parseInt(et.split("-")[1])+1, Calendar.SUNDAY) ;
        weekMap.put("et",  new Date(endCal.getTimeInMillis()));
    }
    if(StringUtils.isNotBlank(st)){
        Calendar startCal = Calendar.getInstance() ;
        startCal.setWeekDate(Integer.parseInt(st.split("-")[0]), Integer.parseInt(st.split("-")[1]), Calendar.MONDAY) ;
        weekMap.put("st", new Date(startCal.getTimeInMillis()));
    }
    List<Map<String,Object>> logs= null ;
    List<Object> list = null;
    weekMap.put("reportId", reportId);
    weekMap.put("teamId", teamId);
    list = baseDao.getSession().selectList("teamLogEstimateForTeam", weekMap) ;
   
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

  /**
   * 下周任务的所属项目
   * @param reportId
   * @param startParam
   * @param endParam
   * @param teamId
   * @return
   */
  public List<Project> getNextWeekProject(String reportId, String startParam, String endParam,
      int teamId) {
    Calendar startCal = Calendar.getInstance() ;
    Calendar endCal = Calendar.getInstance() ;
    startCal.setWeekDate(Integer.parseInt(startParam.split("-")[0]), Integer.parseInt(startParam.split("-")[1]), Calendar.MONDAY) ;
    endCal.setWeekDate(Integer.parseInt(endParam.split("-")[0]), Integer.parseInt(endParam.split("-")[1])+1, Calendar.SUNDAY) ;
    Date start = new Date(startCal.getTimeInMillis()) ;
    Date end = new Date(endCal.getTimeInMillis()) ;
    Map<String,Object> condition = new HashMap<String,Object>() ;
    condition.put("start", start);
    condition.put("end", end);
    condition.put("teamId", teamId)  ;
    
    List<Map<String,Object>> taskSnapShotList = null ;
    Map<String,Object> resultMap = new HashMap<String, Object>() ;
    
    List<Project> objs = DaoFactory.create(TeamWeekReport.class).getSession().selectList("selectNextWeekProjectsWithTeamId", condition) ;
    return objs;
  }
}
