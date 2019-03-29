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
import com.chinacreator.c2.dao.mybatis.enhance.Order;
import com.chinacreator.c2.dao.mybatis.enhance.Pageable;
import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;
import com.chinacreator.c2.dao.mybatis.enhance.Sortable;
import com.chinacreator.c2.project.milestone.Milestone;
import com.chinacreator.c2.project.task.Task;

public class TaskStageService {

	/**
	 * 获取任务阶段列表信息
	 * 
	 * @param projectId
	 * @param search
	 *            过滤条件
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getStageList(int projectId, Map<String, Object> search) {
		Map<String, Object> weekMap = (Map<String, Object>) search.get("week");
		if (weekMap == null) {
			weekMap = new HashMap<String, Object>();
			search.put("week", weekMap);
		}
		JSONArray usersArray = (JSONArray) search.get("users");
		if (usersArray == null || usersArray.size() == 0)
			search.remove("users");
		JSONArray labelsArray = (JSONArray) search.get("labels");
		if (labelsArray == null || labelsArray.size() == 0)
			search.remove("labels");
		if (weekMap != null && weekMap.get("et") != null) {
			Date et = new Date(Long.parseLong(weekMap.get("et").toString()));
			weekMap.put("et", et);
		}
		if (weekMap != null && weekMap.get("st") != null) {
			Date et = new Date(Long.parseLong(weekMap.get("st").toString()));
			weekMap.put("st", et);
		}
		Milestone milestone = (Milestone) JSONObject.toJavaObject((JSONObject) search.get("milestone"),
				Milestone.class);

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Dao<Stage> dao = DaoFactory.create(Stage.class);
		Stage stage = new Stage();
		stage.setProjectId(projectId);
		if (null != milestone) {
			stage.setMilestoneId(milestone.getId());
		} else {
			stage.setMilestoneId(0);
		}

		List<Stage> stageList = dao.select(stage, new Sortable(new Order("orderNo")));

		/*
		 * // 添加模块树数据 ModuleService ms = new ModuleService(); List<Module>
		 * moduleList = ms.getModuleByProject(projectId,
		 * 
		 * Integer.parseInt(((Map)((JSONObject)search.get("module")).get
		 * ("id")).toString() ) null); Map<String, Object> mapModule = new
		 * HashMap<String, Object>(); Stage st = new Stage(); st.setName("模块树");
		 * st.setProjectId(projectId); mapModule.put("stage", st);
		 * mapModule.put("taskList", moduleList); mapModule.put("totalSize",
		 * moduleList.size()); list.add(mapModule);
		 */

		// 分页参数
		Pageable pageable = new Pageable(Integer.valueOf(search.get("page").toString()),
				Integer.valueOf(search.get("rows").toString()));

		// 增加需求列
		// addStoryList(projectId,list,search);
		for (Stage s : stageList) {
			Map<String, Object> map = new HashMap<String, Object>();
			search.put("id", s.getId());
			search.put("stageName", s.getName());
			search.put("state", s.getState());
			search.put("projectId", projectId);
			RowBounds4Page page = new RowBounds4Page(pageable, null, null, true);
			List<Map<String, Object>> taskList = dao.getSession()
					.selectList("com.chinacreator.c2.project.stage.StageMapper.getTaskListByStage", search, page);
			map.put("stage", s);
			map.put("taskList", taskList);
			map.put("totalSize", page.getTotalSize());
			list.add(map);
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
	 * for(Map<String,Object> story:storyList){ Map<String,Object> nums =
	 * service.statisticsTaskInfo(projectId,
	 * Integer.parseInt(story.get("id").toString())); story.put("totalNum",
	 * nums.get("totalNum")); story.put("doneNum", nums.get("doneNum"));
	 * //判断需求的预计完成时间是否已经逾期 if(story.get("status").toString().equals("3") &&
	 * story.get("enddate")!=null){ Timestamp currentTime = new
	 * Timestamp(System.currentTimeMillis());
	 * if(currentTime.after((Timestamp)story.get("enddate"))){
	 * service.updateStoryStatus(Integer.parseInt(story.get("id").toString()),
	 * 2, null); story.put("status", 2); } } } Map<String,Object> map = new
	 * HashMap<String,Object>(); Stage s = new Stage(); s.setName("需求列表");
	 * s.setProjectId(projectId); map.put("stage", s); map.put("taskList",
	 * storyList); list.add(map); }
	 */

	/**
	 * 分页获取更多的任务看板列表
	 * 
	 * @param projectId
	 * @param stageId
	 * @param search
	 * @return
	 */
	public List<Map<String, Object>> getMoreStageList(int projectId, int stageId, Map<String, Object> search) {
		Map<String, Object> weekMap = (Map<String, Object>) search.get("week");
		if (weekMap == null) {
			weekMap = new HashMap<String, Object>();
			search.put("week", weekMap);
		}
		JSONArray usersArray = (JSONArray) search.get("users");
		if (usersArray == null || usersArray.size() == 0)
			search.remove("users");
		JSONArray labelsArray = (JSONArray) search.get("labels");
		if (labelsArray == null || labelsArray.size() == 0)
			search.remove("labels");
		if (weekMap != null && weekMap.get("et") != null) {
			Date et = new Date(Long.parseLong(weekMap.get("et").toString()));
			weekMap.put("et", et);
		}
		if (weekMap != null && weekMap.get("st") != null) {
			Date et = new Date(Long.parseLong(weekMap.get("st").toString()));
			weekMap.put("st", et);
		}

		// 分页参数
		Pageable pageable = new Pageable(Integer.valueOf(search.get("page").toString()),
				Integer.valueOf(search.get("rows").toString()));

		Map<String, Object> map = new HashMap<String, Object>();
		search.put("id", stageId);
		search.put("projectId", projectId);
		RowBounds4Page page = new RowBounds4Page(pageable, null, null, true);
		Dao<Stage> dao = DaoFactory.create(Stage.class);
		List<Map<String, Object>> taskList = dao.getSession()
				.selectList("com.chinacreator.c2.project.stage.StageMapper.getTaskListByStage", search, page);
		return taskList;
	}

	/**
	 * 新增任务阶段
	 * 
	 * @param stage
	 */
	public void addStage(Stage stage) {
		Dao<Stage> dao = DaoFactory.create(Stage.class);
		stage.setOrderNo(getMaxOrderNo(stage.getProjectId(), stage.getMilestoneId()));
		dao.insert(stage);
	}

	private Integer getMaxOrderNo(int projectId, int milestoneId) {
		Dao<Stage> dao = DaoFactory.create(Stage.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("projectId", projectId);
		map.put("milestoneId", milestoneId);
		Stage stage = dao.getSession().selectOne("com.chinacreator.c2.project.stage.StageMapper.getMaxOrderNo", map);
		return stage != null ? stage.getOrderNo() + 1 : 1;
	}

	/**
	 * 拖拽排序阶段或任务列表信息
	 * 
	 * @param stageList
	 */
	public void orderStageTask(Map<String, Object> stage) {
		List<Task> list = new ArrayList<Task>();
		Dao<Task> taskDao = DaoFactory.create(Task.class);

		JSONObject object = ((JSONObject) stage.get("stage"));
		Stage s = (Stage) JSONObject.toJavaObject(object, Stage.class);
		JSONArray taskList = ((JSONArray) stage.get("taskList"));
		if (taskList.size() > 0) {
			int no = taskList.size();
			for (int j = 0; j < taskList.size(); j++) {
				Task task = new Task();
				int id = Integer.parseInt(((JSONObject) taskList.get(j)).get("id").toString());
				boolean isDone = ((JSONObject) taskList.get(j)).getBoolean("isDone");
				if (((JSONObject) taskList.get(j)).get("isConfirm") != null) {// 任务确认状态
					String isConfirm = ((JSONObject) taskList.get(j)).get("isConfirm").toString();
					task.setIsConfirm(isConfirm);
				}
				task.setId(id);
				task.setStageId(s.getId());
				task.setProjectTaskOrder(no);
				if (isDone) {// 任务完成
					task.setIsDone(true);
					String finishedDate = ((JSONObject) taskList.get(j)).get("finishedDate").toString();
					task.setFinishedDate(new Date(Long.parseLong(finishedDate)));
				} else {
					task.setIsDone(false);
				}
				list.add(task);
				no = no - 1;
			}
		}
		taskDao.updateBatch(list);
	}

	/**
	 * 任务完成时查询已完成所属的阶段id
	 * 
	 * @param projectId
	 * @param milesonteId
	 * @return
	 */
	public int getCompeleteStage(int projectId, int milesonteId) {
		Dao<Stage> dao = DaoFactory.create(Stage.class);
		Stage s = new Stage();
		s.setProjectId(projectId);
		s.setMilestoneId(milesonteId);
		s.setName("已完成");
		s = dao.selectOne(s);
		return s.getId();
	}

	/**
	 * 删除某任务阶段
	 * 
	 * @param id
	 */
	public void delStage(int id) {
		Dao<Stage> dao = DaoFactory.create(Stage.class);
		dao.delete(id);
	}

	/**
	 * 更新某任务阶段名称
	 * 
	 * @param stage
	 */
	public void editStage(Stage stage) {
		Dao<Stage> dao = DaoFactory.create(Stage.class);
		dao.update(stage);
	}

	/**
	 * 归档任务
	 * 
	 * @param taskList
	 */
	public void closeStage(List<Map<String, Object>> taskList) {
		Dao<Stage> dao = DaoFactory.create(Stage.class);
		dao.getSession().update("closeStage", taskList);
	}

	/**
	 * 查询部分或者全部已完成任务
	 * 
	 * @param condition
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getCompleteTaskList(int projectId, Map<String, Object> condition) {
		Map<String, Object> weekMap = (Map<String, Object>) condition.get("week");
		if (weekMap == null) {
			weekMap = new HashMap<String, Object>();
			condition.put("week", weekMap);
		}
		JSONArray usersArray = (JSONArray) condition.get("users");
		if (usersArray == null || usersArray.size() == 0)
			condition.remove("users");
		JSONArray labelsArray = (JSONArray) condition.get("labels");
		if (labelsArray == null || labelsArray.size() == 0)
			condition.remove("labels");
		if (weekMap != null && weekMap.get("et") != null) {
			Date et = new Date(Long.parseLong(weekMap.get("et").toString()));
			weekMap.put("et", et);
		}
		if (weekMap != null && weekMap.get("st") != null) {
			Date et = new Date(Long.parseLong(weekMap.get("st").toString()));
			weekMap.put("st", et);
		}
		condition.put("projectId", projectId);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = DaoFactory.create(Object.class).getSession()
				.selectList("com.chinacreator.c2.project.stage.StageMapper.getCompleteTaskList", condition);
		return list;
	}

}
