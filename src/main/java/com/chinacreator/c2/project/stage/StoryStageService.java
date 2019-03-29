package com.chinacreator.c2.project.stage;

import java.sql.Date;
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
import com.chinacreator.c2.project.info.Module;
import com.chinacreator.c2.project.info.ModuleService;
import com.chinacreator.c2.project.story.Story;

public class StoryStageService {

	private static List<Stage> stageList;

	static {
		stageList = new ArrayList<Stage>();
		Stage s = new Stage();
		s.setId(1);
		s.setName("已打开");// 打开
		stageList.add(s);
		s = new Stage();
		s.setId(3);
		s.setName("已确认");// 确认
		stageList.add(s);
		s = new Stage();
		s.setId(2);
		s.setName("已逾期");// 逾期
		stageList.add(s);
		s = new Stage();
		s.setId(0);
		s.setName("已关闭");// 关闭
		stageList.add(s);
	}

	/**
	 * 获取需求状态列表
	 * 
	 * @param projectId
	 * @param search
	 * @return
	 */
	public List<Map<String, Object>> getStoryStageList(int projectId, Map<String, Object> search) {
		Map<String, Object> weekMap = (Map<String, Object>) search.get("week");
		if (weekMap == null) {
			weekMap = new HashMap<String, Object>();
			search.put("week", weekMap);
		}
		if (weekMap != null && weekMap.get("et") != null) {
			Date et = new Date(Long.parseLong(weekMap.get("et").toString()));
			weekMap.put("et", et);
		}
		if (weekMap != null && weekMap.get("st") != null) {
			Date et = new Date(Long.parseLong(weekMap.get("st").toString()));
			weekMap.put("st", et);
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Dao<Story> dao = DaoFactory.create(Story.class);
		JSONArray statusList = (JSONArray) search.get("statusList");// 需求状态合集
		List<Stage> newList = getStoryStatusList(statusList);
		/*
		 * // 添加模块树数据 ModuleService ms = new ModuleService(); List<Module>
		 * moduleList = ms.getModuleByProject(projectId, null); Map<String,
		 * Object> mapModule = new HashMap<String, Object>(); Stage st = new
		 * Stage(); st.setName("模块树"); st.setProjectId(projectId);
		 * mapModule.put("stage", st); mapModule.put("taskList", moduleList);
		 * mapModule.put("totalSize", moduleList.size()); list.add(mapModule);
		 * 
		 * List<Module> childModules = new ArrayList<Module>(); if (0 ==
		 * Integer.parseInt((((Map) ((JSONObject)
		 * search.get("module"))).get("id")).toString())) {
		 * childModules.addAll(moduleList); } else { Module m =
		 * DaoFactory.create(Module.class) .selectByID(Integer.parseInt((((Map)
		 * ((JSONObject) search.get("module"))).get("id")).toString()));
		 * ModuleService moduleService = new ModuleService();
		 * childModules.add(m);
		 * childModules.addAll(moduleService.getTreeNodes(m, moduleList, new
		 * ArrayList<Module>())); } search.put("moduleList", childModules);
		 */

		// 分页参数
		Pageable pageable = new Pageable(Integer.valueOf(search.get("page").toString()),
				Integer.valueOf(search.get("rows").toString()));

		for (Stage s : newList) {
			Map<String, Object> map = new HashMap<String, Object>();
			search.put("status", s.getId());
			RowBounds4Page page = new RowBounds4Page(pageable, null, null, true);
			List<Map<String, Object>> taskList = dao.getSession().selectList("getStoryStageList", search, page);
			map.put("stage", s);
			map.put("taskList", taskList);
			map.put("totalSize", page.getTotalSize());
			list.add(map);
		}
		return list;
	}

	private List<Stage> getStoryStatusList(JSONArray statusList) {
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
	 * 获取更多分页需求看板数据
	 * 
	 * @param projectId
	 * @param status
	 * @param search
	 * @return
	 */
	public List<Map<String, Object>> getMoreStoryStageList(int projectId, int status, Map<String, Object> search) {
		Map<String, Object> weekMap = (Map<String, Object>) search.get("week");
		if (weekMap == null) {
			weekMap = new HashMap<String, Object>();
			search.put("week", weekMap);
		}
		if (weekMap != null && weekMap.get("et") != null) {
			Date et = new Date(Long.parseLong(weekMap.get("et").toString()));
			weekMap.put("et", et);
		}
		if (weekMap != null && weekMap.get("st") != null) {
			Date et = new Date(Long.parseLong(weekMap.get("st").toString()));
			weekMap.put("st", et);
		}
		List<Module> childModules = new ArrayList<Module>();// 增加模块参数
		if (0 != Integer.parseInt((((Map) ((JSONObject) search.get("module"))).get("id")).toString())) {
			Module m = DaoFactory.create(Module.class)
					.selectByID(Integer.parseInt((((Map) ((JSONObject) search.get("module"))).get("id")).toString()));
			ModuleService moduleService = new ModuleService();
			childModules.add(m);
			List<Module> moduleList = moduleService.getModuleByProject(projectId, null, null);
			childModules.addAll(moduleService.getTreeNodes(m, moduleList, new ArrayList<Module>()));
			search.put("moduleList", childModules);
		}

		// 分页参数
		Pageable pageable = new Pageable(Integer.valueOf(search.get("page").toString()),
				Integer.valueOf(search.get("rows").toString()));

		Dao<Story> dao = DaoFactory.create(Story.class);
		search.put("status", status);
		RowBounds4Page page = new RowBounds4Page(pageable, null, null, true);
		List<Map<String, Object>> taskList = dao.getSession().selectList("getStoryStageList", search, page);

		return taskList;
	}

	/**
	 * 需求排序或者状态之间的改变
	 * 
	 * @param stageList
	 */
	public void orderStageStory(Map<String, Object> stage) {
		List<Story> list = new ArrayList<Story>();
		Dao<Story> dao = DaoFactory.create(Story.class);
		JSONObject object = ((JSONObject) stage.get("stage"));
		Stage s = (Stage) JSONObject.toJavaObject(object, Stage.class);
		if (stage.get("taskList") != null) {
			JSONArray taskList = ((JSONArray) stage.get("taskList"));
			if (taskList.size() > 0) {
				int no = taskList.size();
				for (int j = 0; j < taskList.size(); j++) {
					Story story = new Story();
					int id = Integer.parseInt(((JSONObject) taskList.get(j)).get("id").toString());
					story.setId(id);
					story.setStatus(s.getId().toString());
					story.setOrderNo(no);
					list.add(story);
					no--;
				}
			}
		}
		dao.updateBatch(list);
	}
}
