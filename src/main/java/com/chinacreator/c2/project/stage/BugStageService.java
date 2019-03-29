package com.chinacreator.c2.project.stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Pageable;
import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;
import com.chinacreator.c2.project.bug.Bug;

public class BugStageService {

	private static List<Stage> stageList;

	static {
		stageList = new ArrayList<Stage>();
		Stage s = new Stage();
		s.setId(1);
		s.setName("New");// 创建
		stageList.add(s);
		s = new Stage();
		s.setId(2);
		s.setName("Open");// 打开
		stageList.add(s);
		s = new Stage();
		s.setId(6);
		s.setName("Fixed");// 完成
		stageList.add(s);
		s = new Stage();
		s.setId(3);
		s.setName("Reopen");// 重新打开
		stageList.add(s);
		s = new Stage();
		s.setId(4);
		s.setName("FixLater");// 延后
		stageList.add(s);
		s = new Stage();
		s.setId(5);
		s.setName("Rejected");// 拒绝
		stageList.add(s);
		s = new Stage();
		s.setId(0);
		s.setName("Closed");// 关闭
		stageList.add(s);
	}

	/**
	 * 获取bug状态列表
	 * 
	 * @param projectId
	 * @param search
	 * @return
	 */
	public List<Map<String, Object>> getBugStageList(int projectId, Map<String, Object> search) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Dao<Bug> dao = DaoFactory.create(Bug.class);
		JSONArray statusList = (JSONArray) search.get("statusList");// bug状态合集
		List<Stage> newList = getBugStatusList(statusList);
		/*
		 * //添加模块树数据 ModuleService ms=new ModuleService(); List<Module>
		 * moduleList=ms.getModuleByProject(projectId, null); Map<String,Object>
		 * mapModule = new HashMap<String,Object>(); Stage st = new Stage();
		 * st.setName("模块树"); st.setProjectId(projectId); mapModule.put("stage",
		 * st); mapModule.put("taskList", moduleList);
		 * mapModule.put("totalSize", moduleList.size()); list.add(mapModule);
		 */
		// //增加需求列
		// addStoryList(projectId,list,search);
		// 分页参数
		Pageable pageable = new Pageable(Integer.valueOf(search.get("page").toString()),
				Integer.valueOf(search.get("rows").toString()));

		for (Stage s : newList) {
			Map<String, Object> map = new HashMap<String, Object>();
			search.put("id", s.getId());
			search.put("projectId", projectId);
			RowBounds4Page page = new RowBounds4Page(pageable, null, null, true);
			List<Map<String, Object>> taskList = dao.getSession().selectList("getBugStageList", search, page);
			map.put("stage", s);
			map.put("taskList", taskList);
			map.put("totalSize", page.getTotalSize());
			list.add(map);
		}
		return list;
	}

	/**
	 * 获取更多分页bug看板列表
	 * 
	 * @param projectId
	 * @param status
	 * @param search
	 * @return
	 */
	public List<Map<String, Object>> getMoreBugStageList(int projectId, int status, Map<String, Object> search) {
		Dao<Bug> dao = DaoFactory.create(Bug.class);
		Pageable pageable = new Pageable(Integer.valueOf(search.get("page").toString()),
				Integer.valueOf(search.get("rows").toString()));

		search.put("id", status);
		search.put("projectId", projectId);
		RowBounds4Page page = new RowBounds4Page(pageable, null, null, true);
		List<Map<String, Object>> taskList = dao.getSession().selectList("getBugStageList", search, page);
		return taskList;
	}

	/**
	 * fixed和closed状态的缺陷部分查询逻辑
	 * 
	 * @param projectId
	 * @param condition
	 * @return
	 */
	public List<Map<String, Object>> getCompleteBugList(int projectId, Map<String, Object> condition) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Dao<Bug> dao = DaoFactory.create(Bug.class);
		condition.put("projectId", projectId);
		list = dao.getSession().selectList("getBugStageList", condition);
		return list;
	}

	private List<Stage> getBugStatusList(JSONArray statusList) {
		List<Stage> list = new ArrayList<Stage>();
		if (statusList == null || statusList.size() == 0) {
			return stageList;
		}
		for (Stage s : stageList) {
			for (int i = 0; i < statusList.size(); i++) {
				Map<String, String> map = JSONObject.toJavaObject((JSONObject) statusList.get(i), Map.class);
				if (s.getId().equals(map.get("value"))) {
					list.add(s);
				}
			}
		}
		return list;
	}

	/*
	 * private void addStoryList(int projectId, List<Map<String, Object>>
	 * list,Map<String,Object> search) { Dao<Story> dao =
	 * DaoFactory.create(Story.class); Map<String,Object> param = new
	 * HashMap<String,Object>(); if(search.get("milestone")!=null){ JSONObject j
	 * = (JSONObject)search.get("milestone"); Milestone m =
	 * (Milestone)JSONObject.toJavaObject(j, Milestone.class);
	 * param.put("milestone", m); } if(search.get("module")!=null){ JSONObject j
	 * = (JSONObject)search.get("module"); Module m =
	 * (Module)JSONObject.toJavaObject(j, Module.class); param.put("module", m);
	 * } Project project = new Project(); project.setId(projectId);
	 * param.put("project", project); StoryService service = new StoryService();
	 * List<Map<String,Object>> storyList =
	 * dao.getSession().selectList("selectStoryListByPage", param);
	 * for(Map<String,Object> story:storyList){ //判断需求的预计完成时间是否已经逾期
	 * if(story.get("status").toString().equals("3") &&
	 * story.get("enddate")!=null){ Timestamp currentTime = new
	 * Timestamp(System.currentTimeMillis());
	 * if(currentTime.after((Timestamp)story.get("enddate"))){
	 * service.updateStoryStatus(Integer.parseInt(story.get("id").toString()),
	 * 2, null); story.put("status", 2); } } }
	 * 
	 * Map<String,Object> map = new HashMap<String,Object>(); Stage s = new
	 * Stage(); s.setName("需求列表"); s.setProjectId(projectId); map.put("stage",
	 * s); map.put("taskList", storyList); list.add(map); }
	 */

	/**
	 * bug排序或者状态之间的改变
	 * 
	 * @param stageList
	 */
	public void orderStageBug(Map<String, Object> stage) {
		List<Bug> list = new ArrayList<Bug>();
		Dao<Bug> dao = DaoFactory.create(Bug.class);
		JSONObject object = ((JSONObject) stage.get("stage"));
		Stage s = (Stage) JSONObject.toJavaObject(object, Stage.class);
		if (stage.get("taskList") != null) {
			JSONArray taskList = ((JSONArray) stage.get("taskList"));
			if (taskList.size() > 0) {
				int no = taskList.size();
				for (int j = 0; j < taskList.size(); j++) {
					Bug bug = new Bug();
					int id = Integer.parseInt(((JSONObject) taskList.get(j)).get("id").toString());
					bug.setId(id);
					bug.setStatus(s.getId());
					bug.setOrderNo(no);
					list.add(bug);
					no--;
				}
			}
		}
		dao.updateBatch(list);
	}
}
