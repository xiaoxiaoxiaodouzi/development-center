package com.chinacreator.c2.project.story;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.DaoService;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.dao.mybatis.enhance.Pageable;
import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;
import com.chinacreator.c2.project.info.Module;
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.project.label.Label;
import com.chinacreator.c2.project.log.ActionBuilder;
import com.chinacreator.c2.project.log.Change;
import com.chinacreator.c2.project.milestone.Constants.MilestoneStatus;
import com.chinacreator.c2.project.milestone.Constants.MilestoneTargetType;
import com.chinacreator.c2.project.milestone.Milestone;
import com.chinacreator.c2.project.milestone.MilestoneHistory;
import com.chinacreator.c2.project.task.Action;
import com.chinacreator.c2.project.task.Task;
//import com.chinacreator.c2.search.C2ElasticSearchService;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;
import com.google.common.collect.Lists;

/**
 * 需求服务
 * 
 * @author tbw
 */
@Service
public class StoryService {

	/**
	 * 分页获取需求列表
	 * 
	 * @param condition
	 * @param pageable
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public Page<?> getStoryList(Map<String, Object> condition, Pageable pageable)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		getStoryListByProject(condition);
		Dao<Story> storyDao = DaoFactory.create(Story.class);
		// Sortable sb=new Sortable(new Order("orderNo",Order.DIRECTION_ASC));
		RowBounds4Page page = new RowBounds4Page(pageable, null, null, true);
		List<Map<String, Object>> storyList = storyDao.getSession()
				.selectList("com.chinacreator.c2.project.story.StoryMapper.selectStoryListByPage", condition, page);
		// for (Map<String, Object> story : storyList) {
		// // 判断需求的预计完成时间是否已经逾期
		// if (story.get("status").toString().equals("3") &&
		// story.get("enddate") != null) {
		// Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		// if (currentTime.after((Timestamp) story.get("enddate"))) {
		// updateStoryStatus(Integer.parseInt(story.get("id").toString()), 2,
		// null);
		// story.put("status", 2);
		// }
		// }
		// }

		Dao<StoryLabel> storyLabelDao = DaoFactory.create(StoryLabel.class);

		if (!CollectionUtils.isEmpty(storyList)) {

			// 统一查询所有label集合
			List<HashMap<String, Object>> labels = storyLabelDao.getSession().selectList("selectLabelsByStoryList",
					storyList);
			Map<Integer, List<Map<String, Object>>> storyIdMappingLabel = new HashMap<Integer, List<Map<String, Object>>>();
			for (Map<String, Object> labelMap : labels) {
				Integer storyId = (Integer) labelMap.get("story_id");
				if (!storyIdMappingLabel.containsKey(storyId)) {
					storyIdMappingLabel.put(storyId, new ArrayList<Map<String, Object>>());
				}
				storyIdMappingLabel.get(storyId).add(labelMap);
			}

			// 查询项目下所有需求任务统计
			@SuppressWarnings("unchecked")
			Map<String, Object> projectMap = (Map<String, Object>) condition.get("project");
			List<HashMap<String, Object>> taskInfos = storyDao.getSession().selectList("statiProjectStoryTaskInfo",
					projectMap.get("id"));
			Map<Integer, Map<String, Long>> storyIdMappingTask = new HashMap<Integer, Map<String, Long>>();
			for (Map<String, Object> taskMap : taskInfos) {
				Integer storyId = (Integer) taskMap.get("story_id");
				String isDone = (String) taskMap.get("is_done");
				Long num = (Long) taskMap.get("num");

				Map<String, Long> tMap = storyIdMappingTask.get(storyId);
				if (null == tMap) {
					tMap = new HashMap<String, Long>();
					tMap.put("undoneNum", 0l);
					tMap.put("doneNum", 0l);
					storyIdMappingTask.put(storyId, tMap);
				}

				if ("1".equals(isDone)) {
					tMap.put("doneNum", num);
				} else if ("0".equals(isDone)) {
					tMap.put("undoneNum", num);
				}
			}

			// 关联子集合
			for (int i = 0; i < storyList.size(); i++) {
				Map<String, Object> storyMap = storyList.get(i);
				Integer storyId = (Integer) storyMap.get("id");

				storyMap.put("labels", storyIdMappingLabel.get(storyId));
				storyMap.put("taskInfo", storyIdMappingTask.get(storyId));
				storyList.set(i, storyMap);
			}

		}

		return new Page<Map<String, Object>>(pageable.getPageIndex(), pageable.getPageSize(), page.getTotalSize(),
				storyList);
	}

	public List<Map<String, Object>> getStoryListByProject(Map<String, Object> condition) {
		Dao<Story> storyDao = DaoFactory.create(Story.class);
		// Sortable sb=new Sortable(new Order("orderNo",Order.DIRECTION_ASC));
		List<Map<String, Object>> storyList = storyDao.getSession()
				.selectList("com.chinacreator.c2.project.story.StoryMapper.selectStoryListByPage", condition);
		// for (Map<String, Object> story : storyList) {
		// // 判断需求的预计完成时间是否已经逾期
		// if (story.get("status").toString().equals("3") &&
		// story.get("enddate") != null) {
		// Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		// if (currentTime.after((Timestamp) story.get("enddate"))) {
		// updateStoryStatus(Integer.parseInt(story.get("id").toString()), 2,
		// null);
		// story.put("status", 2);
		// }
		// }
		// }

		Dao<StoryLabel> storyLabelDao = DaoFactory.create(StoryLabel.class);

		if (!CollectionUtils.isEmpty(storyList)) {

			// 统一查询所有label集合
			List<HashMap<String, Object>> labels = storyLabelDao.getSession().selectList("selectLabelsByStoryList",
					storyList);
			Map<Integer, List<Map<String, Object>>> storyIdMappingLabel = new HashMap<Integer, List<Map<String, Object>>>();
			for (Map<String, Object> labelMap : labels) {
				Integer storyId = (Integer) labelMap.get("story_id");
				if (!storyIdMappingLabel.containsKey(storyId)) {
					storyIdMappingLabel.put(storyId, new ArrayList<Map<String, Object>>());
				}
				storyIdMappingLabel.get(storyId).add(labelMap);
			}

			// 查询项目下所有需求任务统计
			@SuppressWarnings("unchecked")
			Map<String, Object> projectMap = (Map<String, Object>) condition.get("project");
			List<HashMap<String, Object>> taskInfos = storyDao.getSession().selectList("statiProjectStoryTaskInfo",
					projectMap.get("id"));
			Map<Integer, Map<String, Long>> storyIdMappingTask = new HashMap<Integer, Map<String, Long>>();
			for (Map<String, Object> taskMap : taskInfos) {
				Integer storyId = (Integer) taskMap.get("story_id");
				String isDone = (String) taskMap.get("is_done");
				Long num = (Long) taskMap.get("num");

				Map<String, Long> tMap = storyIdMappingTask.get(storyId);
				if (null == tMap) {
					tMap = new HashMap<String, Long>();
					tMap.put("undoneNum", 0l);
					tMap.put("doneNum", 0l);
					storyIdMappingTask.put(storyId, tMap);
				}

				if ("1".equals(isDone)) {
					tMap.put("doneNum", num);
				} else if ("0".equals(isDone)) {
					tMap.put("undoneNum", num);
				}
			}

			// NumberFormat numberFormat = NumberFormat.getInstance();
			// numberFormat.setMaximumFractionDigits(2);
			// 关联子集合
			for (int i = 0; i < storyList.size(); i++) {
				Map<String, Object> storyMap = storyList.get(i);
				Integer storyId = (Integer) storyMap.get("id");
				storyMap.put("labels", storyIdMappingLabel.get(storyId));
				storyMap.put("taskInfo", storyIdMappingTask.get(storyId));
				// Long fm= 0l;
				// if(null!=storyIdMappingTask.get(storyId)){
				// fm=storyIdMappingTask.get(storyId).get("doneNum")+storyIdMappingTask.get(storyId).get("undoneNum");
				// }
				/*
				 * if(fm!=0){
				 * storyMap.put("completes",(storyIdMappingTask.get(storyId).get
				 * ("doneNum")*1.0/fm)*100+"%"); }else{
				 * storyMap.put("completes","0%"); }
				 */
				storyList.set(i, storyMap);
			}
		}
		return storyList;
	}

	public List<Milestone> getMilestoneListByProject(Integer projectId) {
		Dao<Milestone> mDao = DaoFactory.create(Milestone.class);
		Milestone milestone = new Milestone();
		Project project = new Project();
		project.setId(projectId);
		milestone.setProjectId(project.getId());
		return mDao.select(milestone);
	}

	public List<Module> getModuleListByProject(Integer projectId) {
		Dao<Module> dao = DaoFactory.create(Module.class);
		Module arg0 = new Module();
		Project project = new Project();
		project.setId(projectId);
		arg0.setProjectId(project);
		return dao.select(arg0);
	}

	/*
	 * 根据模块查询模块完成进度计划
	 * 
	 * 
	 * 
	 */
	public List<Module> getModulePlan(List<Module> module) {
		if (module.size() > 0) {
			Dao<Story> storyDao = DaoFactory.create(Story.class);
			Dao<Task> taskDao = DaoFactory.create(Task.class);
			for (Module mod : module) {
				// 查询模块下的需求
				Story story = new Story();
				story.setModule(mod);
				List<Story> stories = storyDao.select(story);
				List<Integer> ids = new ArrayList<>();
				for (Story sto : stories) {
					ids.add(sto.getId());
				}

				List<Task> ta = new ArrayList<>();
				// 查询需求下的任务
				if (ids.size() > 0) {
					ta.addAll(taskDao.getSession()
							.selectList("com.chinacreator.c2.project.task.TaskMapper.selectByStoryids", ids));
				}

				// 查询模块下的任务
				Task task = new Task();
				task.setModuleId(mod.getId());
				task.setProjectId(mod.getProjectId().getId());
				List<Task> tasks = taskDao.select(task);

				// 去重
				List<Integer> integers = new ArrayList<>();
				// List<Task> list =new ArrayList<>();
				int done = 0;
				int no = 0;
				NumberFormat numberFormat = NumberFormat.getInstance();
				numberFormat.setMaximumFractionDigits(2);

				for (Task t : ta) {
					if (integers.indexOf(t.getId()) == -1) {
						if (t.isIsDone()) {
							done++;
						} else {
							no++;
						}
						integers.add(t.getId());
					}
				}

				for (Task t : tasks) {
					if (integers.indexOf(t.getId()) == -1) {
						if (t.isIsDone()) {
							done++;
						} else {
							no++;
						}
						integers.add(t.getId());
					}
				}
				if (0 != done + no) {
					mod.setCompletes(numberFormat.format(((float) done / (float) (done + no)) * 100));
				} else {
					mod.setCompletes("0");
				}
			}
		}
		return module;
	}

	public Story getStoryById(Integer storyid) {
		Dao<Story> dao = DaoFactory.create(Story.class);
		Story entity = dao.selectByID(storyid);
		return entity;
	}

	public int delStoryById(Integer storyid) {

		Dao<Story> dao = DaoFactory.create(Story.class);
		Story story = dao.selectByID(storyid);
		if (null == story)
			throw new RuntimeException("不存在此需求！");

		List<Change> storyAttrList = new ArrayList<Change>();

		if (StringUtils.isNotEmpty(story.getTitle())) {
			Change sa = new Change();
			sa.setName("标题");
			sa.setType("title");
			sa.setOldVal(story.getTitle());
			storyAttrList.add(sa);
		}

		if (StringUtils.isNotEmpty(story.getSpec())) {
			Change sa = new Change();
			sa.setName("内容");
			sa.setType("content");
			sa.setOldVal(story.getSpec());
			storyAttrList.add(sa);
		}

		if (null != story.getMilestone()) {
			Change sa = new Change();
			sa.setName("里程碑");
			sa.setType("milestone");
			sa.setOldVal(story.getMilestone());
			storyAttrList.add(sa);
		}

		if (null != story.getModule()) {
			Change sa = new Change();
			sa.setName("模块");
			sa.setType("module");
			sa.setOldVal(story.getModule());
			storyAttrList.add(sa);
		}

		// 删除关联label数据
		Dao<StoryLabel> storyLabeldao = DaoFactory.create(StoryLabel.class);
		StoryLabel sl = new StoryLabel();
		sl.setStoryId(storyid);
		List<StoryLabel> storyLabelList = storyLabeldao.select(sl);
		for (StoryLabel storyLabel : storyLabelList) {
			storyLabeldao.delete(storyLabel);
		}

		// 删除需求操作记录
		Dao<StoryComment> storyCommentDao = DaoFactory.create(StoryComment.class);
		int deleteCount = storyCommentDao.getSession().delete("deleteByStoryId", story.getId());
		System.out.println("deleteCount:" + deleteCount);
		int deleteNum = dao.delete(story);

		// 删除索引
		// C2ElasticSearchService.deleteStoryIndex(storyid.toString());

		// 里程碑记录删除动作
		if (storyAttrList.size() > 0 && null != story.getMilestone()
				&& MilestoneStatus.OPENED.getVal().equals(story.getMilestone().getStatus())) {
			WebOperationContext context = (WebOperationContext) OperationContextHolder.getContext();
			User user = OrgUserService.getUserInfoById(context.getUser().getId());
			String updateBy = user.getEmail().substring(0, user.getEmail().indexOf("@"));
			Dao<MilestoneHistory> daoMilestoneHistory = DaoFactory.create(MilestoneHistory.class);
			MilestoneHistory milestoneHistory = new MilestoneHistory();
			milestoneHistory.setTargetId(story.getId());
			milestoneHistory.setMilestoneId(story.getMilestone().getId());
			milestoneHistory.setTargetName(story.getTitle());
			milestoneHistory.setType(MilestoneTargetType.DELETE_STORY.getVal());
			milestoneHistory.setUserId(updateBy);
			String changeStr = JSON.toJSONString(storyAttrList);
			milestoneHistory.setAttrVal(changeStr);
			milestoneHistory.setCreateTime(new Timestamp(System.currentTimeMillis()));
			daoMilestoneHistory.insert(milestoneHistory);
		}

		return deleteNum;
	}

	public List<Task> getTaskListByStory(Integer storyid) {
		Dao<Task> tDao = DaoFactory.create(Task.class);
		Task arg0 = new Task();

		return tDao.select(arg0);
	}

	/**
	 * 保存需求
	 * 
	 * @param story
	 * @param taskList
	 * @return
	 */
	@Transactional
	public void saveStory(Story story, List<Task> taskList) {
		Dao<Story> dao = DaoFactory.create(Story.class);
		if (story != null) {
			if (story.getId() == null) {
				WebOperationContext context = (WebOperationContext) OperationContextHolder.getContext();
				User user = OrgUserService.getUserInfoById(context.getUser().getId());
				String openedby = user.getEmail().substring(0, user.getEmail().indexOf("@"));
				story.setOpenedby(openedby);
				story.setStatus("1");
				story.setOrderNo(getMaxOrderNo(story.getProject().getId()));// 排序号
				dao.insert(story);
			} else {
				dao.update(story);
			}
		}

		if (!CollectionUtils.isEmpty(taskList)) {
			Dao<Task> tDao = DaoFactory.create(Task.class);
			for (Task task : taskList) {
				if (task.getId() != null) {
					tDao.update(task);
				} else {
					tDao.insert(task);
				}
			}
		}

	}

	private Integer getMaxOrderNo(int projectId) {
		Dao<Story> dao = DaoFactory.create(Story.class);
		Story s = dao.getSession().selectOne("com.chinacreator.c2.project.story.StoryMapper.getMaxOrderNo", projectId);
		return s != null ? s.getOrderNo() + 1 : 1;
	}

	/**
	 * 获取需求和相关评论
	 * 
	 * @param storyId
	 * @return
	 */
	public Map<String, Object> getStoryAndComments(int storyId) {

		Map<String, Object> reMap = new HashMap<String, Object>();
		Dao<Story> dao = DaoFactory.create(Story.class);
		Story story = dao.selectByID(storyId);

		reMap.put("story", story);

		Dao<Object> obdao = DaoFactory.create(Object.class);
		Map<String, Object> userDto = obdao.getSession()
				.selectOne("com.chinacreator.c2.project.info.MemberMapper.existsByUserName", story.getAssignedTo());
		reMap.put("manageUser", userDto);

		userDto = obdao.getSession().selectOne("com.chinacreator.c2.project.info.MemberMapper.existsByUserName",
				story.getOpenedby());
		reMap.put("createUser", userDto);

		userDto = obdao.getSession().selectOne("com.chinacreator.c2.project.info.MemberMapper.existsByUserName",
				story.getProposer());
		reMap.put("proposer", userDto);

		Dao<StoryComment> storyCommentdao = DaoFactory.create(StoryComment.class);
		StoryComment sc = new StoryComment();
		sc.setStoryId(storyId);

		List<HashMap<String, Object>> storyCommentList = storyCommentdao.getSession().selectList("getStoryAndComments",
				sc);
		for (HashMap<String, Object> storyCommentMap : storyCommentList) {

			String type = (String) storyCommentMap.get("type");
			if (null == type) {
				storyCommentMap.put("type", "story_comment");
			}

			if ("story_update".equals(type)) {
				String attr_val = (String) storyCommentMap.get("attr_val");
				if (StringUtils.isNotEmpty(attr_val)) {
					storyCommentMap.put("attr_val", JSONArray.parse(attr_val));
				}
			}
		}
		reMap.put("actionList", storyCommentList);
		reMap.put("storyLabel", this.getStoryLabel(storyId));
		return reMap;
	}

	/**
	 * 获取需求关联label
	 * 
	 * @param storyId
	 * @return
	 */
	public List<Label> getStoryLabel(int storyId) {
		List<Label> reList = new ArrayList<Label>();
		Dao<StoryLabel> storyLabelDao = DaoFactory.create(StoryLabel.class);
		Dao<Label> labelDao = DaoFactory.create(Label.class);
		StoryLabel storyLabel = new StoryLabel();
		storyLabel.setStoryId(storyId);
		List<StoryLabel> sotryLabelList = storyLabelDao.select(storyLabel);
		for (StoryLabel sl : sotryLabelList) {
			Label label = labelDao.selectByID(sl.getLabelId());
			reList.add(label);
		}

		return reList;
	}

	/**
	 * 评论需求
	 * 
	 * @param storyId
	 * @param content
	 */
	public void replyStory(Integer projectId, Integer storyId, String content) {

		WebOperationContext context = (WebOperationContext) OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String userName = user.getEmail().substring(0, user.getEmail().indexOf("@"));

		Dao<StoryComment> storyCommentDao = DaoFactory.create(StoryComment.class);
		StoryComment sc = new StoryComment();
		sc.setStoryId(storyId);
		sc.setContent(content);
		sc.setUserId(userName);
		sc.setCreateTime(new Timestamp(System.currentTimeMillis()));
		storyCommentDao.insert(sc);
	}

	/**
	 * 获取所有标签和里程碑
	 * 
	 * @param projectId
	 * @return
	 */
	public Map<String, Object> getAllLabelAndMilestone(int projectId, Boolean isUnclosed) {

		if (null == isUnclosed)
			isUnclosed = false;
		Map<String, Object> reMap = new HashMap<String, Object>();
		Dao<Label> labelDao = DaoFactory.create(Label.class);
		Label label = new Label();
		label.setProject(projectId);
		reMap.put("allLabelList", labelDao.select(label));

		Dao<Milestone> milestoneDao = DaoFactory.create(Milestone.class);
		Milestone milestone = new Milestone();
		milestone.setProjectId(projectId);
		if (isUnclosed) {
			reMap.put("allMilestoneList",
					milestoneDao.getSession().selectList("selectAllUnclosedMilestone", milestone));
		} else {
			reMap.put("allMilestoneList", milestoneDao.select(milestone));
		}
		return reMap;
	}

	/**
	 * 保存需求标签
	 * 
	 * @param storyId
	 * @param labelId
	 * @param selected
	 */
	public void saveStoryLabel(int storyId, int labelId, boolean selected) {
		Dao<StoryLabel> storyLabelDao = DaoFactory.create(StoryLabel.class);
		StoryLabel sl = new StoryLabel();
		sl.setStoryId(storyId);
		sl.setLabelId(labelId);
		if (selected) {
			int count = storyLabelDao.count(sl);
			if (count <= 0) {
				storyLabelDao.insert(sl);
			}
		} else {
			storyLabelDao.getSession().delete("com.chinacreator.c2.project.story.StoryLabelMapper.deleteByStoryLabel",
					sl);
		}
	}

	/**
	 * 清空需求标签
	 * 
	 * @param storyId
	 */
	public void cleanStoryLabels(int storyId) {
		Dao<StoryLabel> storyLabelDao = DaoFactory.create(StoryLabel.class);
		StoryLabel sl = new StoryLabel();
		sl.setStoryId(storyId);
		storyLabelDao.getSession().delete("com.chinacreator.c2.project.story.StoryLabelMapper.deleteByStoryLabel", sl);
	}

	/**
	 * 保存需里程碑
	 * 
	 * @param storyId
	 * @param milestoneId
	 * @param selected
	 */
	public void saveStoryMilesone(int storyId, int milestoneId, boolean selected) {
		Dao<Story> storyDao = DaoFactory.create(Story.class);
		Story story = new Story();
		story.setId(storyId);

		Milestone milestone = new Milestone();
		milestone.setId(milestoneId);
		if (selected) {
			story.setMilestone(milestone);
		} else {
			story.setMilestone(null);
		}

		// 自定义sql，处理清空操作
		storyDao.getSession().update("com.chinacreator.c2.project.story.StoryMapper.updateStoryMilestone", story);
	}

	/**
	 * 新增需求
	 * 
	 * @param story
	 * @param labelList
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> addStory(Story story, List<Label> labelList) throws Exception {
		Assert.hasText(story.getTitle());
		// Assert.hasText(story.getSpec());

		WebOperationContext context = (WebOperationContext) OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String openedby = user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Dao<Story> dao = DaoFactory.create(Story.class);
		if (story != null) {
			if (story.getId() == null) {
				story.setOpenedby(openedby);
				story.setStatus("1");
				dao.insert(story);
			}
		}

		if (!CollectionUtils.isEmpty(labelList)) {
			Dao<StoryLabel> storyLabelDao = DaoFactory.create(StoryLabel.class);
			List<StoryLabel> storyLabelList = new ArrayList<StoryLabel>();
			for (Label label : labelList) {
				StoryLabel sl = new StoryLabel();
				sl.setLabelId(label.getId());
				sl.setStoryId(story.getId());
				storyLabelList.add(sl);
			}
			storyLabelDao.insertBatch(storyLabelList);
		}

		// 需求索引
		// C2ElasticSearchService.storyIndex(story.getId().toString());

		Action action = ActionBuilder.Default().recordTimestamp(new Timestamp(System.currentTimeMillis()))
				.target("story", story.getId()).type(StoryActionType.STORY_OPENED.getKey()).actor(openedby)
				.project(story.getProject().getId()).build();

		new DaoService().insertAllCascade(action);

		Map<String, Object> result = new HashMap<String, Object>();
		Class<?> clazz = story.getClass();
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			String fieldName = field.getName();
			Object value = field.get(story);
			result.put(fieldName, value);
		}

		Dao<Object> obdao = DaoFactory.create(Object.class);
		Map<String, Object> userDto = obdao.getSession()
				.selectOne("com.chinacreator.c2.project.info.MemberMapper.existsByUserName", story.getAssignedTo());
		result.put("assignedTo", userDto);

		userDto = obdao.getSession().selectOne("com.chinacreator.c2.project.info.MemberMapper.existsByUserName",
				story.getProposer());
		result.put("proposer", userDto);

		return result;

	}

	/**
	 * 修改需求
	 * 
	 * @param story
	 * @param labelList
	 * @param changeReason
	 */
	public Integer updateStoryBasicInfo(Story story, List<Label> labelList, String changeReason) {

		Assert.hasText(story.getTitle());
		// Assert.hasText(story.getSpec());

		WebOperationContext context = (WebOperationContext) OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String updateBy = user.getEmail().substring(0, user.getEmail().indexOf("@"));

		Dao<Story> dao = DaoFactory.create(Story.class);
		Story dbStory = dao.selectByID(story);
		Assert.notNull(dbStory, "不存在此需求");

		List<Change> storyAttrList = new ArrayList<Change>();

		// 比较模块
		String newModuleId = (null == story.getModule()) ? "" : String.valueOf(story.getModule().getId());
		String oldModuleId = (null == dbStory.getModule()) ? "" : String.valueOf(dbStory.getModule().getId());
		if (!newModuleId.equals(oldModuleId)) {

			Change sa = new Change();
			sa.setName("模块");
			sa.setType("module");
			if (StringUtils.isNotEmpty(oldModuleId)) {
				sa.setOldVal(dbStory.getModule());
				dao.getSession().update("com.chinacreator.c2.project.task.TaskMapper.updateTaskModule", story);
			}

			Dao<Module> moduleDao = DaoFactory.create(Module.class);
			if (StringUtils.isNotEmpty(newModuleId)) {
				Module module = moduleDao.selectByID(newModuleId);
				module.setModules(null);
				sa.setNewVal(module);
			}
			storyAttrList.add(sa);
		}

		// 比较里程碑
		String newMilestoneId = (null == story.getMilestone()) ? "" : String.valueOf(story.getMilestone().getId());
		String oldMilestoneId = (null == dbStory.getMilestone()) ? "" : String.valueOf(dbStory.getMilestone().getId());

		/*
		 * boolean storyOverTime = false;//需求是否逾期状态
		 * if(!newMilestoneId.equals("")&&!oldMilestoneId.equals("")&&!
		 * oldMilestoneId.equals(newMilestoneId)){ storyOverTime = true; }
		 */

		MilestoneHistory milestoneHistory = null;
		if (!newMilestoneId.equals("") && oldMilestoneId.equals("")) {// 为需求选上了里程碑

			Change sa = new Change();
			sa.setName("里程碑");
			sa.setType("milestone");

			/*
			 * if(StringUtils.isNotEmpty(oldMilestoneId)){
			 * sa.setOldVal(dbStory.getMilestone());
			 * 
			 * //如果旧里程碑是已确认状态，则需在往里程碑插入日志
			 * if(StringUtils.isNotEmpty(changeReason)&&MilestoneStatus.OPENED.
			 * getVal().equals(dbStory.getMilestone().getStatus())){
			 * milestoneHistory=new MilestoneHistory();
			 * milestoneHistory.setMilestoneId(dbStory.getMilestone().getId());
			 * milestoneHistory.setTargetName(dbStory.getTitle());
			 * milestoneHistory.setContent(changeReason); } }
			 */

			Dao<Milestone> milestoneDao = DaoFactory.create(Milestone.class);
			if (StringUtils.isNotEmpty(newMilestoneId)) {
				Milestone milestone = milestoneDao.selectByID(newMilestoneId);
				sa.setNewVal(milestone);
			}

			storyAttrList.add(sa);
		}

		// 比较优先级
		if (!dbStory.getPri().equals(story.getPri())) {
			Change sa = new Change();
			sa.setName("优先级");
			sa.setType("pri");
			sa.setOldVal(dbStory.getPri());
			sa.setNewVal(story.getPri());
			storyAttrList.add(sa);
		}

		// 比较标题
		if (!dbStory.getTitle().equals(story.getTitle())) {
			Change sa = new Change();
			sa.setName("标题");
			sa.setType("title");
			sa.setOldVal(dbStory.getTitle());
			sa.setNewVal(story.getTitle());
			storyAttrList.add(sa);

			// 如果旧里程碑是已确认状态，则需在往里程碑插入日志
			if (null == milestoneHistory && StringUtils.isNotEmpty(changeReason)
					&& MilestoneStatus.OPENED.getVal().equals(dbStory.getMilestone().getStatus())) {
				milestoneHistory = new MilestoneHistory();
				milestoneHistory.setMilestoneId(dbStory.getMilestone().getId());
				milestoneHistory.setTargetName(dbStory.getTitle());
				milestoneHistory.setContent(changeReason);
			}
		}

		// 比较肉容
		if (!dbStory.getSpec().equals(story.getSpec())) {
			Change sa = new Change();
			sa.setName("内容");
			sa.setType("content");
			sa.setOldVal(dbStory.getSpec());
			sa.setNewVal(story.getSpec());
			storyAttrList.add(sa);

			// 如果旧里程碑是已确认状态，则需在往里程碑插入日志
			if (null == milestoneHistory && StringUtils.isNotEmpty(changeReason)
					&& MilestoneStatus.OPENED.getVal().equals(dbStory.getMilestone().getStatus())) {
				milestoneHistory = new MilestoneHistory();
				milestoneHistory.setMilestoneId(dbStory.getMilestone().getId());
				milestoneHistory.setTargetName(dbStory.getTitle());
				milestoneHistory.setContent(changeReason);
			}
		}

		/*
		 * //比较需求類型 if(!dbStory.getType().equals(story.getType())){ Change
		 * sa=new Change(); sa.setName("需求類型"); sa.setType("type");
		 * sa.setOldVal(dbStory.getType()); sa.setNewVal(story.getType());
		 * storyAttrList.add(sa);
		 * 
		 * //如果旧里程碑是已确认状态，则需在往里程碑插入日志
		 * if(null==milestoneHistory&&StringUtils.isNotEmpty(changeReason)&&
		 * MilestoneStatus.OPENED.getVal().equals(dbStory.getMilestone().
		 * getStatus())){ milestoneHistory=new MilestoneHistory();
		 * milestoneHistory.setMilestoneId(dbStory.getMilestone().getId());
		 * milestoneHistory.setTargetName(dbStory.getTitle());
		 * milestoneHistory.setContent(changeReason); } }
		 * 
		 * //比较需求主體 if(!dbStory.getMainStory().equals(story.getMainStory())){
		 * Change sa=new Change(); sa.setName("需求主體"); sa.setType("mainStory");
		 * sa.setOldVal(dbStory.getMainStory());
		 * sa.setNewVal(story.getMainStory()); storyAttrList.add(sa);
		 * 
		 * //如果旧里程碑是已确认状态，则需在往里程碑插入日志
		 * if(null==milestoneHistory&&StringUtils.isNotEmpty(changeReason)&&
		 * MilestoneStatus.OPENED.getVal().equals(dbStory.getMilestone().
		 * getStatus())){ milestoneHistory=new MilestoneHistory();
		 * milestoneHistory.setMilestoneId(dbStory.getMilestone().getId());
		 * milestoneHistory.setTargetName(dbStory.getTitle());
		 * milestoneHistory.setContent(changeReason); } }
		 * 
		 * //比较需求內容
		 * if(!dbStory.getStoryFunction().equals(story.getStoryFunction())){
		 * Change sa=new Change(); sa.setName("需求內容");
		 * sa.setType("storyFunction");
		 * sa.setOldVal(dbStory.getStoryFunction());
		 * sa.setNewVal(story.getStoryFunction()); storyAttrList.add(sa);
		 * 
		 * //如果旧里程碑是已确认状态，则需在往里程碑插入日志
		 * if(null==milestoneHistory&&StringUtils.isNotEmpty(changeReason)&&
		 * MilestoneStatus.OPENED.getVal().equals(dbStory.getMilestone().
		 * getStatus())){ milestoneHistory=new MilestoneHistory();
		 * milestoneHistory.setMilestoneId(dbStory.getMilestone().getId());
		 * milestoneHistory.setTargetName(dbStory.getTitle());
		 * milestoneHistory.setContent(changeReason); } }
		 * 
		 * //比较需求功能
		 * if(!dbStory.getStoryEffect().equals(story.getStoryEffect())){ Change
		 * sa=new Change(); sa.setName("需求功能"); sa.setType("storyEffect");
		 * sa.setOldVal(dbStory.getStoryEffect());
		 * sa.setNewVal(story.getStoryEffect()); storyAttrList.add(sa);
		 * 
		 * //如果旧里程碑是已确认状态，则需在往里程碑插入日志
		 * if(null==milestoneHistory&&StringUtils.isNotEmpty(changeReason)&&
		 * MilestoneStatus.OPENED.getVal().equals(dbStory.getMilestone().
		 * getStatus())){ milestoneHistory=new MilestoneHistory();
		 * milestoneHistory.setMilestoneId(dbStory.getMilestone().getId());
		 * milestoneHistory.setTargetName(dbStory.getTitle());
		 * milestoneHistory.setContent(changeReason); } }
		 */

		dbStory.setTitle(story.getTitle());
		dbStory.setSpec(story.getSpec());
		dbStory.setModule(story.getModule());
		dbStory.setPri(story.getPri());
		dbStory.setAssignedTo(story.getAssignedTo());// 修改需求负责人

		dbStory.setStoryEffect(story.getStoryEffect());
		dbStory.setStoryFunction(story.getStoryFunction());
		dbStory.setMainStory(story.getMainStory());
		dbStory.setOpeneddate(story.getOpeneddate());
		dbStory.setStartDate(story.getStartDate());
		dbStory.setEndDate(story.getEndDate());
		dbStory.setType(story.getType());
		dbStory.setMilestone(story.getMilestone());
		dbStory.setCompletes(story.getCompletes());
		// 若需求的里程碑被变更，则该需求状态为逾期，同时新里程碑下增加一条该需求
		/*
		 * Story newStory = new Story(); if(storyOverTime){
		 * newStory.setTitle(story.getTitle());
		 * newStory.setSpec(story.getSpec());
		 * newStory.setModule(story.getModule());
		 * newStory.setPri(story.getPri());
		 * newStory.setProject(story.getProject());
		 * newStory.setOpenedby(updateBy);
		 * newStory.setOrderNo(story.getOrderNo());
		 * newStory.setAssignedTo(story.getAssignedTo());
		 * newStory.setStoryEffect(story.getStoryEffect());
		 * newStory.setStoryFunction(story.getStoryFunction());
		 * newStory.setMainStory(story.getMainStory());
		 * newStory.setStartDate(story.getStartDate());
		 * newStory.setEndDate(story.getEndDate());
		 * newStory.setType(story.getType()); newStory.setOpeneddate(new
		 * Timestamp(System.currentTimeMillis())); Milestone milestone = new
		 * Milestone(); milestone.setId(Integer.parseInt(newMilestoneId));
		 * milestone.setProjectId(story.getProject().getId());
		 * newStory.setMilestone(milestone); newStory.setStatus("1");
		 * dao.insert(newStory);//新里程碑下添加需求 dbStory.setStatus("2");//逾期 }else{
		 * dbStory.setMilestone(story.getMilestone()); }
		 */
		dao.update(dbStory);

		if (null != labelList) {
			Dao<StoryLabel> storyLabelDao = DaoFactory.create(StoryLabel.class);
			StoryLabel sl = new StoryLabel();
			sl.setStoryId(story.getId());
			List<Map<String, Object>> oldLabelList = storyLabelDao.getSession().selectList("selectLabelsByStoryId", sl);

			HashMap<Integer, Map<String, Object>> oldKVMap = new HashMap<Integer, Map<String, Object>>();
			for (Map<String, Object> map : oldLabelList) {
				Integer id = ((Long) map.get("id")).intValue();
				oldKVMap.put(id, map);
			}

			HashMap<Integer, Label> newKVMap = new HashMap<Integer, Label>();
			for (Label label : labelList) {
				newKVMap.put(label.getId(), label);
			}

			List<StoryLabel> insertStoryLabelList = new ArrayList<StoryLabel>();
			List<Label> insertLabelList = new ArrayList<Label>();
			for (Label label : labelList) {
				if (!oldKVMap.containsKey(label.getId())) {
					StoryLabel insertSL = new StoryLabel();
					insertSL.setLabelId(label.getId());
					insertSL.setStoryId(story.getId());
					insertStoryLabelList.add(insertSL);
					insertLabelList.add(label);
				}
			}

			List<StoryLabel> deleteStoryLabelList = new ArrayList<StoryLabel>();
			List<Label> deleteLabelList = new ArrayList<Label>();
			for (Entry<Integer, Map<String, Object>> entry : oldKVMap.entrySet()) {
				if (!newKVMap.containsKey(entry.getKey())) {
					StoryLabel deleteSL = new StoryLabel();
					deleteSL.setId(((Integer) entry.getValue().get("label_story_id")));
					deleteSL.setLabelId(entry.getKey());
					deleteSL.setStoryId(story.getId());
					deleteStoryLabelList.add(deleteSL);

					Label label = new Label();
					label.setId(((Long) entry.getValue().get("id")).intValue());
					label.setName((String) entry.getValue().get("name"));
					label.setColor((String) entry.getValue().get("color"));
					label.setProject(((Long) entry.getValue().get("project")).intValue());
					deleteLabelList.add(label);
				}
			}

			Change change = new Change();
			change.setName("标签");
			change.setType("label");
			if (deleteStoryLabelList.size() > 0) {
				storyLabelDao.deleteBatch(deleteStoryLabelList);
				change.setOldVal(deleteLabelList);
			}

			if (insertStoryLabelList.size() > 0) {
				storyLabelDao.insertBatch(insertStoryLabelList);
				change.setNewVal(insertLabelList);
			}

			if (deleteStoryLabelList.size() > 0 || insertStoryLabelList.size() > 0)
				storyAttrList.add(change);

		}

		if (storyAttrList.size() > 0) {
			// 保存操作

			String changeStr = JSON.toJSONString(storyAttrList);
			Dao<StoryComment> storyCommentDao = DaoFactory.create(StoryComment.class);
			StoryComment storyComment = new StoryComment();
			storyComment.setStoryId(story.getId());
			storyComment.setCreateTime(new Timestamp(System.currentTimeMillis()));
			storyComment.setUserId(updateBy);
			storyComment.setType(StoryActionType.STORY_UPDATE.getKey());
			storyComment.setAttrVal(changeStr);
			storyComment.setContent(changeReason);
			storyCommentDao.insert(storyComment);

			// 往里程碑模块记录变更日志
			if (null != milestoneHistory) {
				Dao<MilestoneHistory> daoMilestoneHistory = DaoFactory.create(MilestoneHistory.class);
				milestoneHistory.setTargetId(story.getId());
				milestoneHistory.setType(MilestoneTargetType.UPDATE_STORY.getVal());
				milestoneHistory.setUserId(updateBy);

				milestoneHistory.setAttrVal(changeStr);
				milestoneHistory.setCreateTime(new Timestamp(System.currentTimeMillis()));
				daoMilestoneHistory.insert(milestoneHistory);
			}
		}

		// 需求索引
		// C2ElasticSearchService.updateStoryIndex(story.getId().toString());
		return story.getId();
	}

	/**
	 * 统计需求下的任务信息
	 * 
	 * @param projectId
	 * @param storyId
	 * @return
	 */
	public Map<String, Object> statisticsTaskInfo(int projectId, int storyId) {

		Long totalNum = 0l;
		Long doneNum = 0l;
		Dao<Task> taskDao = DaoFactory.create(Task.class);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("projectId", projectId);
		params.put("storyId", storyId);
		List<Map<String, Object>> list = taskDao.getSession()
				.selectList("com.chinacreator.c2.project.story.StoryMapper.statisticsTaskInfo", params);
		for (Map<String, Object> map : list) {
			Long num = (Long) map.get("num");
			totalNum += num;
			String isDone = (String) map.get("is_done");
			if ("1".equals(isDone)) {
				doneNum = num;
			}
		}

		Map<String, Object> reMap = new HashMap<String, Object>();
		reMap.put("totalNum", totalNum);
		reMap.put("doneNum", doneNum);
		return reMap;
	}

	public void synStoryStatus() {
		Story s = new Story();
		s.setStatus("3");
		List<Story> list = DaoFactory.create(Story.class).select(s);
		List<Story> updatelist = new ArrayList<Story>();
		if (list.size() > 0) {
			for (Story story : list) {
				if (null != story.getEndDate() && story.getEndDate().before(new Date())) {
					story.setStatus("2");
					updatelist.add(story);
				}
			}
			System.out.println("updatelist" + updatelist);
			DaoFactory.create(Story.class).updateBatch(updatelist);
		}

	}

	/**
	 * 修改需求状态
	 * 
	 * @param storyId
	 * @param status
	 */
	public void updateStoryStatus(Integer storyId, Integer status, Story story) {

		MilestoneHistory milestoneHistory = new MilestoneHistory();

		Dao<Story> dao = DaoFactory.create(Story.class);
		Story dbStory = dao.selectByID(storyId);
		Assert.notNull(dbStory, "不存在此需求");

		WebOperationContext context = (WebOperationContext) OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String updateBy = user.getEmail().substring(0, user.getEmail().indexOf("@"));

		StoryComment storyComment = new StoryComment();

		if (status == 1) {
			dbStory.setStatus("1");
			storyComment.setType(StoryActionType.STORY_OPENED.getKey());
			milestoneHistory.setType(MilestoneTargetType.OPENED_STORY.getVal());
		} else if (status == 0) {
			dbStory.setStatus("0");
			storyComment.setType(StoryActionType.STORY_CLOSED.getKey());
			milestoneHistory.setType(MilestoneTargetType.CLOSED_STORY.getVal());
			// 確認狀態
		} else if (status == 3) {
			if (null != story.getStartDate())
				dbStory.setStartDate(story.getStartDate());
			if (null != story.getEndDate())
				dbStory.setEndDate(story.getEndDate());
			if (story.getEndDate().before(new Timestamp(System.currentTimeMillis()))) {
				dbStory.setStatus("2");
			} else {
				dbStory.setStatus("3");
			}
			storyComment.setType(StoryActionType.STORY_CONFIRM.getKey());
			milestoneHistory.setType(MilestoneTargetType.CONFIRM_STORY.getVal());
		} else {
			dbStory.setStatus("2");// 逾期状态
		}
		if (null != story && null != story.getCompletes())
			dbStory.setCompletes(story.getCompletes());
		dao.update(dbStory);
		// 更新索引
		Map<String, Object> storyStatus = new HashMap<String, Object>();
		storyStatus.put("status", dbStory.getStatus());
		// C2ElasticSearchService.updateStoryIndexLittle(storyId.toString(),storyStatus);

		// 保存操作
		Dao<StoryComment> storyCommentDao = DaoFactory.create(StoryComment.class);
		storyComment.setStoryId(storyId);
		storyComment.setCreateTime(new Timestamp(System.currentTimeMillis()));
		storyComment.setUserId(updateBy);
		storyCommentDao.insert(storyComment);

		// 已确里程碑记录需求打开和关闭状态
		if (null != dbStory.getMilestone()
				&& MilestoneStatus.OPENED.getVal().equals(dbStory.getMilestone().getStatus())) {
			Dao<MilestoneHistory> daoMilestoneHistory = DaoFactory.create(MilestoneHistory.class);
			milestoneHistory.setTargetId(dbStory.getId());
			milestoneHistory.setMilestoneId(dbStory.getMilestone().getId());
			milestoneHistory.setTargetName(dbStory.getTitle());
			milestoneHistory.setUserId(updateBy);
			milestoneHistory.setCreateTime(new Timestamp(System.currentTimeMillis()));
			daoMilestoneHistory.insert(milestoneHistory);
		}
	}

	/**
	 * 修改评论
	 * 
	 * @param commentId
	 * @param content
	 */
	public void updateCommentContent(Integer commentId, String content) {
		WebOperationContext context = (WebOperationContext) OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String updateBy = user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Dao<StoryComment> storyCommentDao = DaoFactory.create(StoryComment.class);
		StoryComment sc = storyCommentDao.selectByID(commentId);
		if (!updateBy.equals(sc.getUserId()))
			throw new RuntimeException("没有修改权限");
		sc.setContent(content);
		storyCommentDao.update(sc);
	}

	/**
	 * 需求排序
	 * 
	 * @param storyList
	 */
	public void orderStory(List<Map<String, Object>> storyList) {
		Dao<Story> dao = DaoFactory.create(Story.class);
		List<Story> list = Lists.newArrayList();
		if (storyList.size() > 0) {
			int no = storyList.size();
			for (int i = 0; i < storyList.size(); i++) {
				Story map = new Story();
				int storyId = Integer.parseInt(storyList.get(i).get("id").toString());
				map.setId(storyId);
				map.setOrderNo(no);
				list.add(map);
				no = no - 1;
			}
			dao.updateBatch(list);
		}

	}
	
	public List<Map<String,Object>> getStoryListByIds(List<String> ids){
		if(CollectionUtils.isEmpty(ids)){
			return Lists.newArrayList();
		}else{
			List<Map<String,Object>> lists = DaoFactory.create(Story.class).getSession().selectList("com.chinacreator.c2.project.story.StoryMapper.selectStoryListByIds", ids);
			for(Map<String,Object> s:lists){
				Map<String, Object> assignedTo = (Map<String,Object>)s.get("assignedTo");
				if(s.get("status")!=null && s.get("status").equals("2")){
					assignedTo.put("remark1", "3");
				}else{
					assignedTo.put("remark1", "1");
				}
			}
			return lists;
		}
	}

}
