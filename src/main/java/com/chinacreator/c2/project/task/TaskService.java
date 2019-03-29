package com.chinacreator.c2.project.task;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.chinacreator.asp.comp.sys.oauth2.common.CredentialStore;
import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Order;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.dao.mybatis.enhance.Pageable;
import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;
import com.chinacreator.c2.dao.mybatis.enhance.Sortable;
import com.chinacreator.c2.project.info.Member;
import com.chinacreator.c2.project.info.MembersMgt;
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.project.info.UserPreferences;
import com.chinacreator.c2.project.info.UserPreferencesService;
import com.chinacreator.c2.project.label.Label;
import com.chinacreator.c2.project.label.LabelTask;
import com.chinacreator.c2.project.log.ActionBuilder;
import com.chinacreator.c2.project.log.ActionLogUtil;
import com.chinacreator.c2.project.log.ActionTypes;
import com.chinacreator.c2.project.log.HistoryRecordResolver;
import com.chinacreator.c2.project.preference.Preference;
import com.chinacreator.c2.project.preference.ProjectPreferenceService;
import com.chinacreator.c2.project.service.ProjectService;
import com.chinacreator.c2.project.stage.Stage;
import com.chinacreator.c2.project.task.exception.TaskPersistenceException;
//import com.chinacreator.c2.search.C2ElasticSearchService;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;
import com.google.common.collect.Lists;

public class TaskService {

	private static TaskServiceTool persisTool = TaskServiceTool.getINSTANCE();

	private static ActionLogUtil actionUtil = ActionLogUtil.getINSTANCE();

	private static Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

	private static UserPreferencesService preferenceService = new UserPreferencesService();

	/**
	 * 修改任务
	 * 
	 * @param task
	 * @return
	 */
	public Task updateTask(Task task) {
		Task oldTask = null;
		Timestamp recordTimestamp = new Timestamp(System.currentTimeMillis());
		try {
			Dao<Task> taskDao = DaoFactory.create(Task.class);
			oldTask = taskDao.selectByID(task.getId());
			if ((oldTask.getMilestoneId() == null && task.getMilestoneId() != null)
					|| (oldTask.getMilestoneId() != null && task.getMilestoneId() != null
							&& !oldTask.getMilestoneId().equals(task.getMilestoneId()))
					|| (oldTask.getMilestoneId() != null && task.getMilestoneId() == null)) {// 切换了里程碑
				Dao<Stage> dao = DaoFactory.create(Stage.class);
				Stage s = dao.selectByID(oldTask.getStageId());
				Stage stage = new Stage();
				stage.setName(s.getName());
				stage.setProjectId(task.getProjectId());
				if (task.getMilestoneId() == null) {
					stage.setMilestoneId(0);
				} else {
					stage.setMilestoneId(task.getMilestoneId());
				}
				stage = dao.selectOne(stage);
				task.setStageId(stage.getId());
			}
			if (task.getEstimate() != oldTask.getEstimate()) {
				task.setLeft(task.getEstimate() - task.getConsumed());// 重新计算剩余工时
			}
			taskDao.update(task);
			return task;
			// 更新任务索引
			// C2ElasticSearchService.updateTaskIndex(task.getId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskPersistenceException("任务修改失败..." + e.getCause().getMessage());
		} finally {
			try {
				Action action = ActionBuilder.taskDefault(task.getId(), recordTimestamp)
						.type(ActionTypes.TASK_EDIT.getColVal()).build();
				actionUtil.Compare2Insert(oldTask, task, action);
			} catch (Exception e) {
				LOGGER.warn("任务属性变更历史记录过程中出现异常...异常信息为:{}", e.getCause().getMessage());
			}
		}
	}

	/**
	 * 修改任务,包括空值更新
	 * 
	 * @param task
	 * @return
	 */
	public void updateTaskWithNull(Task task) {
		Task oldTask = null;
		Timestamp recordTimestamp = new Timestamp(System.currentTimeMillis());
		try {
			Dao<Task> taskDao = DaoFactory.create(Task.class);
			oldTask = taskDao.selectByID(task.getId());
			if ((oldTask.getMilestoneId() == null && task.getMilestoneId() != null)
					|| (oldTask.getMilestoneId() != null && task.getMilestoneId() != null
							&& !oldTask.getMilestoneId().equals(task.getMilestoneId()))
					|| (oldTask.getMilestoneId() != null && task.getMilestoneId() == null)) {// 切换了里程碑
				Dao<Stage> dao = DaoFactory.create(Stage.class);
				Stage s = dao.selectByID(oldTask.getStageId());
				Stage stage = new Stage();
				stage.setName(s.getName());
				stage.setProjectId(task.getProjectId());
				if (task.getMilestoneId() == null) {
					stage.setMilestoneId(0);
				} else {
					stage.setMilestoneId(task.getMilestoneId());
				}
				stage = dao.selectOne(stage);
				task.setStageId(stage.getId());
			}
			taskDao.getSession().update("updateWithNull", task);

			// 更新任务索引
			// C2ElasticSearchService.updateTaskIndex(task.getId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskPersistenceException("任务修改失败..." + e.getCause().getMessage());
		} finally {
			try {
				Action action = ActionBuilder.taskDefault(task.getId(), recordTimestamp)
						.type(ActionTypes.TASK_EDIT.getColVal()).build();
				actionUtil.CompareAll2Insert(oldTask, task, action);
			} catch (Exception e) {
				LOGGER.warn("任务属性变更历史记录过程中出现异常...异常信息为:{}", e.getCause().getMessage());
			}
		}
	}

	/**
	 * 修改任务,用于描述更新时不记录日志
	 * 
	 * @param task
	 * @return
	 */
	public void updateTaskWithOutRegisAction(Task task) {
		try {
			DaoFactory.create(Task.class).update(task);

			// 更新任务索引
			// C2ElasticSearchService.updateTaskIndex(task.getId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskPersistenceException("任务修改失败..." + e.getCause().getMessage());
		}
	}

	/**
	 * 创建任务
	 * 
	 * @param task
	 * @param stageName
	 *            阶段名称
	 * @param taskConfirm
	 *            项目任务确认设置
	 * @return
	 */
	public Task createTask(Task task, String stageName, boolean taskConfirm) {
		Timestamp recordTimestamp = new Timestamp(System.currentTimeMillis());
		try {
			Dao<Task> taskDao = DaoFactory.create(Task.class);
			WebOperationContext context = (WebOperationContext) OperationContextHolder.getContext();
			User user = OrgUserService.getUserInfoById(context.getUser().getId());
			String username = user.getEmail().substring(0, user.getEmail().indexOf("@"));
			task.setCreator(username);
			if (stageName != null) {
				task.setStageId(persisTool.getStageId(task.getProjectId(), task.getMilestoneId(), stageName));
			} else {
				if (task.getStageId() == null) {// 设置默认阶段，默认阶段不存在时设置为第一个阶段
					if (task.getBugId() == null && taskConfirm) {// 任务需要确认进入待确认看板
																	// 由bug创建的任务不需要进行确认
						task.setStageId(persisTool.getStageId(task.getProjectId(), task.getMilestoneId(), "待确认"));
					} else {
						task.setStageId(persisTool.getStageId(task.getProjectId(), task.getMilestoneId(), "未开始"));
					}
				}
			}
			if (task.getBugId() == null && taskConfirm) {// 任务需要确认
				task.setIsConfirm("0");
			}
			task.setProjectTaskOrder(persisTool.projectTaskMaxOrder(task.getProjectId()) + 1);
			task.setMyTaskOrder(persisTool.myTaskMaxOrder(username) + 1);
			if (stageName != null) {
				task.setStageId(persisTool.getStageId(task.getProjectId(), task.getMilestoneId(), stageName));
			}
			taskDao.insert(task);
			
			if(CollectionUtils.isNotEmpty(task.getLabelTask())){
			  List<LabelTask> list = Lists.newArrayList();
			  for(LabelTask lable:task.getLabelTask()){
			    lable.setTaskId(task.getId());
			    list.add(lable);
			  }
			  DaoFactory.create(LabelTask.class).insertBatch(list);
			}
			
			
			// persisTool.createTask(task);
			// 添加任务索引
			// C2ElasticSearchService.taskIndex(task.getId().toString());
			return task;
		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskPersistenceException("任务创建失败..." + e.getCause().getMessage());
		} finally {
			try {
				Action action = ActionBuilder.taskDefault(task.getId(), recordTimestamp)
						.type(ActionTypes.TASK_ADD.getColVal()).build();
				actionUtil.insert(action);
			} catch (Exception e) {
				LOGGER.warn("任务创建过程中操作记录出现异常...异常信息为:{}", e.getCause().getMessage());
			}
		}
	}

	public void deleteTask(Task task) {
		try {
			persisTool.deleteTask(task);

			// 删除任务索引
			// C2ElasticSearchService.deleteTaskIndex(task.getId().toString());

		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskPersistenceException("删除任务出现异常..." + e.getCause().getMessage());
		}
	}

	/**
	 * 获取状态为：doing的项目
	 * 
	 * @return
	 */
	public List<Project> getDoingProjects() {
		Dao<Project> projects = DaoFactory.create(Project.class);
		Project condition = new Project();
		condition.setStatus("doing");
		return projects.select(condition);
	}

	public void changeTaskStatus(Task task) {
		Timestamp recordTimestamp = new Timestamp(System.currentTimeMillis());
		try {
			Dao<Task> taskDao = DaoFactory.create(Task.class);
			Task oldTask = taskDao.selectByID(task.getId());
			if (task.isIsDone()) {
				oldTask.setStageId(persisTool.getStageId(oldTask.getProjectId(), oldTask.getMilestoneId(), "已完成"));// 任务完成时阶段为已完成
				// 任务完成时若截止日期或者起始日期为空,则补全之
				if (oldTask.getDeadline() == null) {
					oldTask.setDeadline(new Date(System.currentTimeMillis()));
				}
				if (oldTask.getEstStartDate() == null) {
					oldTask.setEstStartDate((Date) DaoFactory.create(Estimate.class).getSession()
							.selectOne("selectOldestEstimateWorkDate", task.getId()));
				}
				oldTask.setFinishedDate(new Date(System.currentTimeMillis()));
				WebOperationContext context = (WebOperationContext) OperationContextHolder.getContext();
				User user = OrgUserService.getUserInfoById(context.getUser().getId());
				String username = user.getEmail().substring(0, user.getEmail().indexOf("@"));
				oldTask.setFinishedBy(username);
			} else {
				oldTask.setFinishedDate(null);
				oldTask.setFinishedBy(null);
				oldTask.setStageId(persisTool.getStageId(oldTask.getProjectId(), oldTask.getMilestoneId(), "未开始"));
			}
			oldTask.setIsDone(task.isIsDone());
			taskDao.getSession().update("com.chinacreator.c2.project.task.TaskMapper.updateWithNull", oldTask);

			// 更新任务索引
			// C2ElasticSearchService.updateTaskIndex(task.getId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskPersistenceException("任务状态修改失败..." + e.getCause().getMessage());
		} finally {
			try {
				Action action = null;
				if (task.isIsDone()) {
					action = ActionBuilder.taskDefault(task.getId(), recordTimestamp)
							.type(ActionTypes.TASK_FINISH.getColVal()).build();
				} else {
					action = ActionBuilder.taskDefault(task.getId(), recordTimestamp)
							.type(ActionTypes.TASK_START.getColVal()).build();
				}
				actionUtil.insert(action);
			} catch (Exception e) {
				LOGGER.warn("任务操作历史记录过程中出现异常...异常信息为:{}", e.getCause().getMessage());
			}
		}
	}

	public void changeTaskClosed(Task task) {
		Timestamp recordTimestamp = new Timestamp(System.currentTimeMillis());
		try {
			Dao<Task> taskDao = DaoFactory.create(Task.class);
			task.setStageId(null);
			taskDao.update(task);

			// 更新任务索引
			// C2ElasticSearchService.updateTaskIndex(task.getId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskPersistenceException("任务关闭修改失败..." + e.getCause().getMessage());
		} finally {
			try {
				Action action = null;
				if (task.isClosed()) {
					action = ActionBuilder.taskDefault(task.getId(), recordTimestamp)
							.type(ActionTypes.TASK_CLOSED.getColVal()).build();
				} else {
					action = ActionBuilder.taskDefault(task.getId(), recordTimestamp)
							.type(ActionTypes.TASK_OPEN.getColVal()).build();
				}
				actionUtil.insert(action);
			} catch (Exception e) {
				LOGGER.warn("任务操作历史记录过程中出现异常...异常信息为:{}", e.getCause().getMessage());
			}
		}
	}

	public void changeTaskConfirmed(Task task) {
		Timestamp recordTimestamp = new Timestamp(System.currentTimeMillis());
		try {
			Dao<Task> taskDao = DaoFactory.create(Task.class);
			// 确认状态改变时，任务看板也需改变
			if (task.getIsConfirm().equals("1")) {
				task.setStageId(persisTool.getStageId(task.getProjectId(), task.getMilestoneId(), "未开始"));
			} else {
				task.setStageId(persisTool.getStageId(task.getProjectId(), task.getMilestoneId(), "待确认"));
			}
			taskDao.update(task);

			// 更新任务索引
			// C2ElasticSearchService.updateTaskIndex(task.getId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskPersistenceException("任务确认修改失败..." + e.getCause().getMessage());
		} finally {
			try {
				Action action = null;
				if (StringUtils.isNotBlank(task.getIsConfirm()) && task.getIsConfirm().equals("1")) {
					action = ActionBuilder.taskDefault(task.getId(), recordTimestamp)
							.type(ActionTypes.TASK_CONFIRM.getColVal()).build();
				}
				if (StringUtils.isNotBlank(task.getIsConfirm()) && task.getIsConfirm().equals("0")) {
					action = ActionBuilder.taskDefault(task.getId(), recordTimestamp)
							.type(ActionTypes.TASK_UNCONFIRM.getColVal()).build();
				}
				actionUtil.insert(action);
			} catch (Exception e) {
				LOGGER.warn("任务操作历史记录过程中出现异常...异常信息为:{}", e.getCause().getMessage());
			}
		}
	}

	public void saveEstimate(Estimate estimate) {
		Task oldTask = null;
		Task task = null;
		Timestamp recordTimestamp = new Timestamp(System.currentTimeMillis());
		try {
			oldTask = DaoFactory.create(Task.class).selectByID(estimate.getTaskId());
			task = persisTool.saveEstimate(estimate, oldTask);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskPersistenceException("录入工时出现异常..." + e.getCause().getMessage());
		} finally {
			try {
				Action action = ActionBuilder.taskDefault(oldTask.getId(), recordTimestamp)
						.type(ActionTypes.TASK_RECORD_ESTIMATE.getColVal()).extra(estimate.getConsumed().toString())
						.extra1(actionDateFormat(estimate.getWorkDate())).comment(estimate.getDescription()).build();
				actionUtil.Compare2Insert(oldTask, task, action);
			} catch (Exception e) {
				LOGGER.warn("录入工时历史记录过程中出现异常...异常信息为:{}", e.getCause().getMessage());
			}
		}

	}

	public void updateEstimate(Estimate estimate) {
		Task oldTask = null;
		Task task = null;
		Timestamp recordTimestamp = new Timestamp(System.currentTimeMillis());
		try {
			oldTask = DaoFactory.create(Task.class).selectByID(estimate.getTaskId());
			task = persisTool.updateEstimate(estimate, oldTask);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskPersistenceException("修改工时出现异常..." + e.getCause().getMessage());
		} finally {
			try {
				Action action = ActionBuilder.taskDefault(oldTask.getId(), recordTimestamp)
						.type(ActionTypes.TASK_EDIT_ESTIMATE.getColVal())
						.extra1(actionDateFormat(estimate.getWorkDate())).build();
				actionUtil.Compare2Insert(oldTask, task, action);
			} catch (Exception e) {
				LOGGER.warn("修改工时历史记录过程中出现异常...异常信息为:{}", e.getCause().getMessage());
			}
		}

	}

	public void deleteEstimate(Estimate estimate) {
		Task oldTask = null;
		Task task = null;
		Timestamp recordTimestamp = new Timestamp(System.currentTimeMillis());
		try {
			oldTask = DaoFactory.create(Task.class).selectByID(estimate.getTaskId());
			task = persisTool.deleteEstimate(estimate, oldTask);
		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskPersistenceException("删除工时出现异常..." + e.getCause().getMessage());
		} finally {
			try {
				Action action = ActionBuilder.taskDefault(oldTask.getId(), recordTimestamp)
						.type(ActionTypes.TASK_DELETE_ESTIMATE.getColVal()).extra(estimate.getConsumed().toString())
						.extra1(actionDateFormat(estimate.getWorkDate())).build();
				actionUtil.Compare2Insert(oldTask, task, action);
			} catch (Exception e) {
				LOGGER.warn("删除工时历史记录过程中出现异常...异常信息为:{}", e.getCause().getMessage());
			}
		}

	}

	@SuppressWarnings("deprecation")
	private String actionDateFormat(Date date) {
		if (date.getYear() == new Timestamp(System.currentTimeMillis()).getYear()) {
			return new SimpleDateFormat("M月d日").format(date);
		} else {
			return new SimpleDateFormat("yy年M月d日").format(date);
		}
	}

	/**
	 * 获取任务操作历史记录
	 * 
	 * @param taskId
	 * @return
	 */
	public Object getTaskActionHistory(int taskId, boolean isAll) {
		Dao<Task> taskDao = DaoFactory.create(Task.class);

		List<Action> actions = null;
		if (isAll) {
			// 查询当前任务的所有操作
			actions = taskDao.getSession()
					.selectList("com.chinacreator.c2.project.task.ActionMapper.selectActionHistory", taskId);
		} else {
			// 查询前5条
			actions = taskDao.getSession().selectList(
					"com.chinacreator.c2.project.task.ActionMapper.selectActionHistory", taskId,
					new RowBounds4Page(0, 5));
		}

		List<Map<String, Object>> historyList = new ArrayList<Map<String, Object>>();
		// 解析action,拼出任务历史记录描述
		for (Action action : actions) {
			HistoryRecordResolver resolve = new HistoryRecordResolver(action, Task.class);
			historyList.add(resolve.resolve());
			;
		}
		return historyList;
	}

	/*
	 * 任务列表
	 */
	public Page<Object> taskList(Map<String, Object> search, Pageable pageable) {
		@SuppressWarnings("unchecked")
		Map<String, Object> weekMap = (Map<String, Object>) search.get("week");
		if (weekMap == null) {
			weekMap = new HashMap<String, Object>();
			search.put("week", weekMap);
		}
		JSONArray usersArray = (JSONArray) search.get("users");
		if (usersArray == null || usersArray.size() == 0)
			search.remove("users");
		JSONArray projectsArray = (JSONArray) search.get("projects");
		if (projectsArray == null || projectsArray.size() == 0)
			search.remove("projects");
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
		Dao<Object> baseDao = DaoFactory.create(Object.class);
		RowBounds4Page page = new RowBounds4Page(pageable, null, null, true);
		List<Object> list = baseDao.getSession().selectList("projectTaskList", search, page);
		// Bug bugBean = new Bug();
		// List<Map<String,Object>> bugList =
		// DaoFactory.create(Bug.class).getSession().selectList("addToTaskBugs",
		// search);
		// list.addAll(0, bugList);

		Page<Object> taskPage = new Page<Object>(pageable.getPageIndex(), pageable.getPageSize(), page.getTotalSize(),
				list);

		return taskPage;
	}

	/*
	 * 任务详情
	 */
	public Map<String, Object> projectTask(Integer id) {
		Dao<Object> baseDao = DaoFactory.create(Object.class);
		Map<String, Object> projectTask = baseDao.getSession().selectOne("projectTask", id);
		return projectTask;
	}

	/**
	 * 获取任务工时日历时间记录,包含当前任务工时记录的本月工时记录
	 * 
	 * @param conditions
	 *            start:起始时间字符串(yyyy-MM-dd); end:结束时间字符串; taskId:任务主键;
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Map<String, Object> getCalenderEstimateEvents(Map<String, Object> conditions) {
		Dao<Estimate> dao = DaoFactory.create(Estimate.class);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 获取当前任务对应的工时记录并遍历出当前任务工时对应的日期
		Map<String, Object> currentEventDateMap = new HashMap<String, Object>();
		List<Map<String, Object>> taskEstEvents = dao.getSession().selectList("getEstEventsOfSpecificTask", conditions);
		for (Map<String, Object> est : taskEstEvents) {
			currentEventDateMap.put((String) est.get("start"), est);
		}
		// 获取时间范围内的工时记录
		List<Map<String, Object>> estEventList = dao.getSession().selectList("getEstEventsOfOneMonth", conditions);
		Map<String, Object> eventList = new HashMap();
		Map<String, Object> maps = new HashMap<String, Object>();
		for (Map<String, Object> event : estEventList) {
			if (!maps.containsKey(event.get("start").toString())) {
				List<Object> list = new ArrayList<Object>();
				for (Map<String, Object> e : estEventList) {
					if (e.get("start".toString()).equals(event.get("start").toString())) {
						list.add(e);
					}
				}
				maps.put(event.get("start").toString(), list);
			}
			if (currentEventDateMap.containsKey(event.get("start").toString())) {
				event.put("current", true);
				event.put("currentEvent", currentEventDateMap.get(event.get("start").toString()));
			}
		}
		resultMap.put("estimateEvents", estEventList);
		resultMap.put("estimateList", currentEventDateMap);
		resultMap.put("estimateAry", maps);

		return resultMap;
	}

	/**
	 * 获取任务工时日历时间记录,包含当前任务工时记录的本月工时记录
	 * 
	 * @param conditions
	 *            start:起始时间字符串(yyyy-MM-dd); end:结束时间字符串; taskId:任务主键;
	 * @return
	 * @throws ParseException
	 */
	public Map<String, Object> getCalenderEstimateEvents4App(Map<String, Object> conditions) throws ParseException {
		// getCalenderEstimateEventsForApp
		Dao<Estimate> dao = DaoFactory.create(Estimate.class);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		SimpleDateFormat std = new SimpleDateFormat("yyyy-MM-dd");

		// 将时间戳格式转换成时间字符串
		// Long starttmp = Long.parseLong(conditions.get("start").toString());
		// Long endtmp = Long.parseLong(conditions.get("end").toString());

		// String startdate =std.format(starttmp);
		// String enddate =std.format(endtmp);

		// conditions.put("start", startdate);
		// conditions.put("end", enddate);

		// 获取当前任务对应的工时记录并遍历出当前任务工时对应的日期
		Map<String, Object> currentEventDateMap = new HashMap<String, Object>();
		List<Map<String, Object>> taskEstEvents = dao.getSession().selectList("getEstEventsOfSpecificTask", conditions);
		for (Map<String, Object> est : taskEstEvents) {
			String title = est.get("title").toString();
			if (title.charAt(title.length() - 1) == '0') {
				est.put("title", title.substring(0, title.length() - 2));
			}
			currentEventDateMap.put((String) est.get("start"), est);
			java.util.Date date = std.parse(est.get("start").toString());
			est.put("start", date);
		}
		// 获取时间范围内的工时记录
		List<Map<String, Object>> estEventList = dao.getSession().selectList("getEstEventsOfOneMonth", conditions);

		for (Map<String, Object> event : estEventList) {
			String title = event.get("title").toString();
			if (title.charAt(title.length() - 1) == '0') {
				event.put("title", title.substring(0, title.length() - 2));
			}
			if (currentEventDateMap.containsKey(event.get("start").toString())) {
				event.put("current", true);
				event.put("currentEvent", currentEventDateMap.get(event.get("start").toString()));
			}
			java.util.Date date = std.parse(event.get("start").toString());
			event.put("start", date);

		}
		resultMap.put("estimateEvents", estEventList);
		resultMap.put("estimateList", taskEstEvents);

		return resultMap;
	}

	public Map<String, Object> getLabelsByTaskId(int taskId, int projectId) {
		Map<String, Object> result = new HashMap<String, Object>();
		Label label = new Label();
		label.setProject(projectId);

		result.put("currentLabels",
				DaoFactory.create(LabelTask.class).getSession().selectList("getLabelsByTaskId", taskId));
		result.put("AllLabels", DaoFactory.create(Label.class).select(label));

		return result;
	}

	public Map<String, Object> getAllLabels(int projectId) {
		Map<String, Object> result = new HashMap<String, Object>();
		Label label = new Label();
		label.setProject(projectId);

		result.put("AllLabels", DaoFactory.create(Label.class).select(label));

		return result;
	}

	public void updateTaskLabels(int taskId, List<LabelTask> labels) {
		try {
			persisTool.updateTaskLabels(taskId, labels);

			// 更新任务索引
			// C2ElasticSearchService.updateTaskIndex(taskId+"");
		} catch (Exception e) {
			e.printStackTrace();
			throw new TaskPersistenceException("修改标签出现异常..." + e.getCause().getMessage());
		}
	}

	/*
	 * 首页任务列表
	 */
	public Page<Task> getIndexTaskList(Map<String, Object> search, Pageable pageable) {

		Dao<Task> taskDao = DaoFactory.create(Task.class);
		Sortable sortAble = new Sortable(new Order("id", Order.DIRECTION_DESC));
		Task task = new Task();
		WebOperationContext context = (WebOperationContext) OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName = user.getEmail().substring(0, user.getEmail().indexOf("@"));
		task.setAssignedTo(currentUserName);
		task.setIsDone(false);
		Page<Task> taskPage = taskDao.selectByPage(task, pageable, sortAble, true);
		return taskPage;
	}

	public List<Estimate> getEstimates(Estimate estimate) {
		return DaoFactory.create(Estimate.class).getSession().selectList("getEstimatesInfo", estimate);
	}

	/**
	 * 根据工作日期获取当天总的工时
	 * 
	 * @param workDate
	 *            要查询的时间
	 * @return 返回当天已经录入的任务的总工时
	 **/
	public float getSumEstimates(Estimate estimate) {
		Float selectOne = DaoFactory.create(Estimate.class).getSession().selectOne("getSumEstimates", estimate);
		return (Float) (selectOne == null ? 0 : selectOne);
	}

	public List<Map<String, Object>> getTaskList(String assignedTo) {
		return DaoFactory.create(Task.class).getSession().selectList("getTaskList", assignedTo);
	}

	/**
	 * 通过偏好名称获取当前用户偏好信息,若名称为空则获取当前用户所有偏好信息
	 * 
	 * @param infoName
	 *            偏好名称
	 * @return
	 */
	public Map<String, Object> getTaskPreference(String infoName) {
		Map<String, Object> result = new HashMap<String, Object>();
		String userId = CredentialStore.getCurrCredential().getUserInfo().getId();
		User user = OrgUserService.getUserInfoById(userId);
		String userName = user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String, Object> u = OrgUserService.getUserByName(userName);
		if (infoName != null) {
			UserPreferences preference = preferenceService.queryByPK(u.get("user_id").toString(), infoName);
			if (preference != null && preference.getInfoContent() != null)
				result.put(preference.getInfoName(), preference);
		} else {
			UserPreferences preference = new UserPreferences();
			preference.setUserId(u.get("user_id").toString());
			for (UserPreferences pre : preferenceService.queryByUserPreferences(preference)) {
				if (preference != null && preference.getInfoContent() != null)
					result.put(pre.getInfoName(), pre);
			}
		}
		return result;
	}

	/**
	 * 设置当前用户偏好信息,若当前用户已经存在此偏好则修改之
	 * 
	 * @param preference
	 *            偏好对象
	 */
	public void setTaskPreference(UserPreferences preference) {
		WebOperationContext context = (WebOperationContext) OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName = user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String, Object> uu = OrgUserService.getUserByName(currentUserName);
		preference.setUserId(uu.get("user_id").toString());
		UserPreferences pre = preferenceService.queryByPK(uu.get("user_id").toString(), preference.getInfoName());
		if (pre != null) {
			pre.setInfoContent(preference.getInfoContent());
			preferenceService.update(pre);
		} else {
			preferenceService.create(preference);
		}
	}

	/**
	 * 判断项目是否允许普通成员创建和编辑任务
	 * 
	 * @param projectId
	 * @return
	 */
	public boolean isProjectTaskCU(int projectId) {
		WebOperationContext context = (WebOperationContext) OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName = user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String, Object> uu = OrgUserService.getUserByName(currentUserName);
		boolean state = true;
		String key = "projectTaskCUState";
		ProjectPreferenceService ps = new ProjectPreferenceService();
		Map<String, Object> map = ps.get(projectId, key);
		Preference preference = (Preference) map.get(key);
		if (null != preference) {
			state = "true".equals(preference.getPreferContent());
			if (!state) {
				String userId = uu.get("user_id").toString();
				Set<String> projectCRSet = getProjectCR(projectId);
				if (projectCRSet.contains(userId)) {
					state = true;
				}
			}
		}

		return state;
	}

	/**
	 * 获取项目创建者和负责人
	 * 
	 * @param projectId
	 * @return
	 */
	private Set<String> getProjectCR(int projectId) {
		Set<String> set = new HashSet<String>();
		MembersMgt mm = new MembersMgt();
		Map<String, Object> userDTO = mm.getProjectCreator(projectId);
		if (null != userDTO) {
			set.add(userDTO.get("user_id").toString());
		}
		List<Map<String, Object>> list = mm.getProjectResponsible(projectId);
		if (null != list && !list.isEmpty()) {
			for (Map<String, Object> user : list) {
				set.add(userDTO.get("user_id").toString());
			}
		}

		return set;
	}

	public void bugTaskClosed(Integer bugId) {
		Dao<Task> taskDao = DaoFactory.create(Task.class);
		List<Task> bugTaskIds = taskDao.getSession().selectList("selectTaskIdByBug", bugId);
		for (Task task : bugTaskIds) {
			Task bt = new Task();
			bt.setClosed(true);
			bt.setId(task.getId());
			bt.setIsConfirm(task.getIsConfirm());
			taskDao.update(bt);
			Action action = ActionBuilder.taskDefault(task.getId(), new Timestamp(System.currentTimeMillis()))
					.type(ActionTypes.TASK_CLOSED.getColVal()).build();
			ActionLogUtil.getINSTANCE().insert(action);
		}
	}

	public Object getTaskOperationInfo(Integer taskId) {
		Map<String, Object> info = DaoFactory.create(Task.class).getSession().selectOne("selectTaskOperationInfo",
				taskId);
		return info;
	}

	/**
	 * 用于新建任务的 分配人列表
	 * 
	 * @param projectId
	 *            任务所在的项目id
	 * @return
	 */
	public List<Map<String, Object>> getMembersForAdd(int projectId) {
		Dao<Member> dao = DaoFactory.create(Member.class);
		List<Map<String, Object>> list = dao.getSession()
				.selectList("com.chinacreator.c2.project.info.MemberMapper.queryUserByProjectIdForAdd", projectId);

		return list;
	}

	public List<Project> getMyDoingProjects() {
		Dao<Project> projects = DaoFactory.create(Project.class);

		List<Project> allMyProject = new ProjectService().getMyProjects();
		List<Project> list = new ArrayList<Project>();

		if (allMyProject != null && allMyProject.size() > 0) {
			Set<Integer> ids = new HashSet<Integer>();
			for (Project p : allMyProject) {
				int projectId = p.getId();
				ids.add(projectId);
			}
			list = projects.getSession().selectList("getMyDoingProjects", Arrays.asList(ids.toArray()));
		}

		// 需更改此处的sql语句，查询arc_status为2
		return list;
	}

	@Transactional
	public void projectTaskOrder(Integer moveTaskId, Integer targetTaskId, String direction, Integer projectId) {
		Dao<Task> taskDao = DaoFactory.create(Task.class);
		Task moveTask = taskDao.selectByID(moveTaskId);
		Task targetTask = taskDao.selectByID(targetTaskId);
		Map<String, Object> orderParam = new HashMap<String, Object>();
		orderParam.put("projectId", projectId);
		orderParam.put("direction", direction);
		orderParam.put("moveTaskOrder", moveTask.getProjectTaskOrder());
		orderParam.put("targetTaskOrder", targetTask.getProjectTaskOrder());
		taskDao.getSession().update("projectTaskListOrder", orderParam);
		Task mt = new Task();
		mt.setId(moveTask.getId());
		mt.setStageId(moveTask.getStageId());
		mt.setProjectTaskOrder(targetTask.getProjectTaskOrder());
		taskDao.update(mt);
	}

	@Transactional
	public void myTaskOrder(Integer moveTaskId, Integer targetTaskId, String direction, String username) {
		Dao<Task> taskDao = DaoFactory.create(Task.class);
		Task moveTask = taskDao.selectByID(moveTaskId);
		Task targetTask = taskDao.selectByID(targetTaskId);
		Map<String, Object> orderParam = new HashMap<String, Object>();
		orderParam.put("username", username);
		orderParam.put("direction", direction);
		orderParam.put("moveTaskOrder", moveTask.getMyTaskOrder());
		orderParam.put("targetTaskOrder", targetTask.getMyTaskOrder());
		taskDao.getSession().update("myTaskListOrder", orderParam);
		Task mt = new Task();
		mt.setId(moveTask.getId());
		mt.setStageId(moveTask.getStageId());
		mt.setMyTaskOrder(targetTask.getMyTaskOrder());
		taskDao.update(mt);
	}
}
