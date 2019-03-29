package com.chinacreator.c2.workbench;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.session.RowBounds;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Pageable;
import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.project.service.ProjectService;
import com.chinacreator.c2.project.stage.Stage;
import com.chinacreator.c2.project.story.Story;
import com.chinacreator.c2.project.story.StoryService;
import com.chinacreator.c2.project.task.Task;
import com.chinacreator.c2.security.shiro.impls.ProjectAuthorizationProvider;
/**
 * 我的任务 看板模式
 * @author Administrator
 *
 */
public class MyTaskBoardService {

  /**
   * 获取我的任务看板列表
   * @param search
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<Map<String,Object>> getTaskStageList(Map<String,Object> search){
    Map<String,Object> weekMap = (Map<String, Object>) search.get("week");
    if (weekMap==null) {
        weekMap=new HashMap<String, Object>();
        search.put("week", weekMap);
    }
    JSONArray projectsArray = (JSONArray) search.get("projects");
    if(projectsArray==null||projectsArray.size()==0)search.remove("projects");
    JSONArray labelsArray = (JSONArray) search.get("labels");
    if(labelsArray==null||labelsArray.size()==0)search.remove("labels");
    if(weekMap!=null&&weekMap.get("et")!=null){
        Date et = new Date(Long.parseLong(weekMap.get("et").toString()));
        weekMap.put("et", et);
    }
    if(weekMap!=null&&weekMap.get("st")!=null){
        Date et = new Date(Long.parseLong(weekMap.get("st").toString()));
        weekMap.put("st", et);
    }
    
    
    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    
    //分页参数
    Pageable pageable = new Pageable(Integer.valueOf(search.get("page").toString()), Integer.valueOf(search.get("rows").toString()));
    
    //增加需求列
    addStoryList(list,search,pageable);
    
    
    String[] names = {"待确认","未开始","进行中","待审核","已完成"};
    for(String name:names){
      Stage stage = new Stage();
      stage.setName(name);
      Map<String,Object> map = new HashMap<String,Object>();
      search.put("stage", stage);
      RowBounds4Page page = new RowBounds4Page(pageable, null, null, true);
      List<Map<String,Object>> taskList = DaoFactory.create(Object.class).getSession().selectList("com.chinacreator.c2.workbench.MyTaskBoardMapper.getTaskListByStage", search,page);
      map.put("stage", stage);
      map.put("taskList", taskList);
      map.put("totalSize", page.getTotalSize());
      list.add(map);
    }
    return list;
  }
  
  /**
   * 获取更多分页任务
   * @param stageName
   * @param search
   * @return
   */
  public List<Map<String,Object>> getMoreTaskStageList(String stageName,Map<String,Object> search){
	  Map<String,Object> weekMap = (Map<String, Object>) search.get("week");
	    if (weekMap==null) {
	        weekMap=new HashMap<String, Object>();
	        search.put("week", weekMap);
	    }
	    JSONArray projectsArray = (JSONArray) search.get("projects");
	    if(projectsArray==null||projectsArray.size()==0)search.remove("projects");
	    JSONArray labelsArray = (JSONArray) search.get("labels");
	    if(labelsArray==null||labelsArray.size()==0)search.remove("labels");
	    if(weekMap!=null&&weekMap.get("et")!=null){
	        Date et = new Date(Long.parseLong(weekMap.get("et").toString()));
	        weekMap.put("et", et);
	    }
	    if(weekMap!=null&&weekMap.get("st")!=null){
	        Date et = new Date(Long.parseLong(weekMap.get("st").toString()));
	        weekMap.put("st", et);
	    }
	    
	  //分页参数
	  	Pageable pageable = new Pageable(Integer.valueOf(search.get("page").toString()), Integer.valueOf(search.get("rows").toString()));
	  	Stage stage = new Stage();
	      stage.setName(stageName);
	      search.put("stage", stage);
	      RowBounds4Page page = new RowBounds4Page(pageable, null, null, true);
	      List<Map<String,Object>> taskList = DaoFactory.create(Object.class).getSession().selectList("com.chinacreator.c2.workbench.MyTaskBoardMapper.getTaskListByStage", search,page);
	    return taskList;
  }
  
  /**
   * 查询部分或者全部已完成任务
   * @param condition
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<Map<String,Object>> getCompleteTaskList(Map<String,Object> condition){
    Map<String,Object> weekMap = (Map<String, Object>) condition.get("week");
    if (weekMap==null) {
        weekMap=new HashMap<String, Object>();
        condition.put("week", weekMap);
    }
    JSONArray projectsArray = (JSONArray) condition.get("projects");
    if(projectsArray==null||projectsArray.size()==0)condition.remove("projects");
    JSONArray labelsArray = (JSONArray) condition.get("labels");
    if(labelsArray==null||labelsArray.size()==0)condition.remove("labels");
    if(weekMap!=null&&weekMap.get("et")!=null){
        Date et = new Date(Long.parseLong(weekMap.get("et").toString()));
        weekMap.put("et", et);
    }
    if(weekMap!=null&&weekMap.get("st")!=null){
        Date et = new Date(Long.parseLong(weekMap.get("st").toString()));
        weekMap.put("st", et);
    }
    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    list = DaoFactory.create(Object.class).getSession().selectList("com.chinacreator.c2.workbench.MyTaskBoardMapper.getCompleteTaskList", condition);
    return list;
  }
  
  /**
   * 获取更多需求
   * @param search
   * @return
   */
  public List<Map<String,Object>> getMoreStoryList(Map<String,Object> search){
	  Pageable pageable = new Pageable(Integer.valueOf(search.get("page").toString()), Integer.valueOf(search.get("rows").toString()));
	  StoryService service = new StoryService();
	    Dao<Story> dao = DaoFactory.create(Story.class);
	    RowBounds4Page page = new RowBounds4Page(pageable, null, null, true);
	    List<Map<String,Object>> storyList = dao.getSession().selectList("selectMyStoryList", search,page);//还要包含我的任务关联的需求
	    for(Map<String,Object> story:storyList){
	        Map<String,Object> nums = service.statisticsTaskInfo(((Project)story.get("project")).getId(), Integer.parseInt(story.get("id").toString()));
	        story.put("totalNum", nums.get("totalNum"));
	        story.put("doneNum", nums.get("doneNum"));
	        
	      }
	  return storyList;
  }
  
  private void addStoryList(List<Map<String, Object>> list,Map<String,Object> search,Pageable pageable) {
    StoryService service = new StoryService();
    Dao<Story> dao = DaoFactory.create(Story.class);
    RowBounds4Page page = new RowBounds4Page(pageable, null, null, true);
    List<Map<String,Object>> storyList = dao.getSession().selectList("selectMyStoryList", search,page);//还要包含我的任务关联的需求
    for(Map<String,Object> story:storyList){
      Map<String,Object> nums = service.statisticsTaskInfo(((Project)story.get("project")).getId(), Integer.parseInt(story.get("id").toString()));
      story.put("totalNum", nums.get("totalNum"));
      story.put("doneNum", nums.get("doneNum"));
      
      //判断需求的预计完成时间是否已经逾期
      /*if(story.get("status").toString().equals("3") && story.get("enddate")!=null){
    	Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    	if(currentTime.after((Timestamp)story.get("enddate"))){
    		service.updateStoryStatus(Integer.parseInt(story.get("id").toString()), 2, null);
    		story.put("status", 2);
    	}
      }*/
    }
    Map<String,Object> map = new HashMap<String,Object>();
    Stage s = new Stage();
    s.setName("需求列表");
    map.put("stage", s);
    map.put("taskList", storyList);
    map.put("totalSize", page.getTotalSize());
    list.add(map);
  }

  /**
   * 我的个人任务排序
   * @param stage 需要更新的阶段任务信息
   */
  public void orderMyTaskStage(Map<String,Object> stage){
    List<Task> list = new ArrayList<Task>();
    Dao<Task> taskDao = DaoFactory.create(Task.class);
    
    JSONObject object = ((JSONObject) stage.get("stage"));
    Stage s = (Stage) JSONObject.toJavaObject(object, Stage.class);
    JSONArray taskList = ((JSONArray) stage.get("taskList"));
    if (taskList.size() > 0) {
      int no = taskList.size();
      for (int j = 0; j < taskList.size(); j++) {
        Task task = new Task();
        int id = Integer.parseInt(((JSONObject)taskList.get(j)).get("id").toString());
        boolean isDone = (Boolean) ((JSONObject)taskList.get(j)).get("isDone");
        if(((JSONObject)taskList.get(j)).get("isConfirm")!=null){//任务确认状态
          String isConfirm = ((JSONObject)taskList.get(j)).get("isConfirm").toString();
          task.setIsConfirm(isConfirm);
        }
        int projectId = Integer.parseInt(((JSONObject)taskList.get(j)).get("projectId").toString());
        int milestoneId = ((JSONObject)taskList.get(j)).get("milestoneId")==null?0:Integer.parseInt(((JSONObject)taskList.get(j)).get("milestoneId").toString());
        task.setId(id);
        task.setStageId(getStageId(projectId, milestoneId, s.getName()));
        task.setMyTaskOrder(no);
        if(isDone){//任务完成
          task.setIsDone(true);
          String finishedDate = ((JSONObject)taskList.get(j)).get("finishedDate").toString();
          task.setFinishedDate(new Date(Long.parseLong(finishedDate)));
        }else{
          task.setIsDone(false);
        }
        list.add(task);
        no=no-1;
      }
    }
    taskDao.updateBatch(list);
  }

  //获取阶段id
  private Integer getStageId(Integer projectId, Integer milestoneId, String name) {
    Dao<Stage> dao = DaoFactory.create(Stage.class);
    Stage stage = new Stage();
    stage.setProjectId(projectId);
    stage.setMilestoneId(milestoneId);
    stage.setName(name);
    stage = dao.selectOne(stage);
    return stage.getId();
  }
  
  /**
   * 获取用户所在的项目的负责人角色
   * @param userName
   * @return
   * @throws Exception 
   */
  public Map<String,Boolean> getProjectPermitedMap() throws Exception{
    Map<String,Boolean> map = new HashMap<String,Boolean>();
    ProjectService projectService = new ProjectService();
    List<Project> projects = projectService.getMyProjects();
    if(CollectionUtils.isNotEmpty(projects)){
      ProjectAuthorizationProvider provider = new ProjectAuthorizationProvider();
      for(Project p:projects){
        map.put(p.getId()+"", provider.isProjectPermited(p.getId(), "advancedSet_menu"));
      }
    }
    return map;
  }
}
