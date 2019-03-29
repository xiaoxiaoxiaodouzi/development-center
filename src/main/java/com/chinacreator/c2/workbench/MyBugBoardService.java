package com.chinacreator.c2.workbench;

import java.sql.Timestamp;
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
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.project.stage.Stage;
import com.chinacreator.c2.project.story.Story;
import com.chinacreator.c2.project.story.StoryService;
import com.google.common.collect.Lists;

public class MyBugBoardService {

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
	 * 根据条件查询我的bug看板列表内容
	 * 
	 * @param search
	 * @return
	 */
	public List<Map<String, Object>> getBugStageList(Map<String, Object> search) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		JSONArray projectsArray = (JSONArray) search.get("projects");
		if (projectsArray == null || projectsArray.size() == 0)
			search.remove("projects");
		JSONArray labelsArray = (JSONArray) search.get("labels");
		if (labelsArray == null || labelsArray.size() == 0)
			search.remove("labels");
		JSONArray statusArray = (JSONArray) search.get("statusList");
		if (statusArray == null || statusArray.size() == 0)
			search.remove("statusList");

		List<Stage> newList = getBugStatusList(statusArray);

		// 分页参数
		Pageable pageable = new Pageable(Integer.valueOf(search.get("page").toString()),
				Integer.valueOf(search.get("rows").toString()));

		// 增加需求列
		addStoryList(list, search, pageable);

		for (Stage s : newList) {
			Map<String, Object> map = new HashMap<String, Object>();
			search.put("id", s.getId());
			RowBounds4Page page = new RowBounds4Page(pageable, null, null, true);
			List<Map<String, Object>> taskList = DaoFactory.create(Object.class).getSession()
					.selectList("getMyBugStageList", search, page);
			map.put("stage", s);
			map.put("taskList", taskList);
			map.put("totalSize", page.getTotalSize());
			list.add(map);
		}

		return list;
	}

	/**
	 * 获取工作台更多bug分页看板数据
	 * 
	 * @param status
	 * @param search
	 * @return
	 */
	public List<Map<String, Object>> getMoreBugStageList(int status, Map<String, Object> search) {
		JSONArray projectsArray = (JSONArray) search.get("projects");
		if (projectsArray == null || projectsArray.size() == 0)
			search.remove("projects");
		JSONArray labelsArray = (JSONArray) search.get("labels");
		if (labelsArray == null || labelsArray.size() == 0)
			search.remove("labels");
		JSONArray statusArray = (JSONArray) search.get("statusList");
		if (statusArray == null || statusArray.size() == 0)
			search.remove("statusList");
		Pageable pageable = new Pageable(Integer.valueOf(search.get("page").toString()),
				Integer.valueOf(search.get("rows").toString()));
		search.put("id", status);
		RowBounds4Page page = new RowBounds4Page(pageable, null, null, true);
		List<Map<String, Object>> taskList = DaoFactory.create(Object.class).getSession()
				.selectList("getMyBugStageList", search, page);
		return taskList;
	}

	/**
	 * fixed和closed状态的缺陷部分查询逻辑
	 * 
	 * @param projectId
	 * @param condition
	 * @return
	 */
	public List<Map<String, Object>> getCompleteBugList(Map<String, Object> condition) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Dao<Bug> dao = DaoFactory.create(Bug.class);
		list = dao.getSession().selectList("getMyBugStageList", condition);
		return list;
	}

	private void addStoryList(List<Map<String, Object>> list, Map<String, Object> search, Pageable pageable) {
		Dao<Story> dao = DaoFactory.create(Story.class);
		RowBounds4Page page = new RowBounds4Page(pageable, null, null, true);
		List<Map<String, Object>> storyList = dao.getSession().selectList("selectMyStoryList", search, page);
		Map<String, Object> map = new HashMap<String, Object>();
		Stage s = new Stage();
		s.setName("需求列表");
		map.put("stage", s);
		map.put("taskList", storyList);
		map.put("totalSize", page.getTotalSize());
		list.add(map);
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

	/**
	 * 我的bug看板排序
	 * 
	 * @param stage
	 */
	public void orderMyBugStage(Map<String, Object> stage) {
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

	/**
	 * 获取我的项目bug中的所有标签
	 * 
	 * @param projects
	 * @return
	 */
	public List<Map<String, Object>> getLabelByProjects(List<Project> projects) {
		List<Map<String, Object>> list = Lists.newArrayList();
		if (projects.size() == 1) {
			list = DaoFactory.create(Object.class).getSession().selectList("getLabelByProject", projects);
		}
		if (projects.size() > 1) {
			list = DaoFactory.create(Object.class).getSession().selectList("getLabelByProjects", projects);
		}
		for (Map<String, Object> map : list) {
			if (map.containsKey("projectName")) {
				map.put("group", map.get("projectName"));
				map.remove("projectName");
			}
			map.put("id", map.get("ID"));
			map.put("name", map.get("NAME"));
			map.put("color", map.get("COLOR"));
			map.remove("ID");
			map.remove("NAME");
			map.remove("COLOR");
		}
		return list;
	}
}
