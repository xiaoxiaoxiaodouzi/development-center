package com.chinacreator.c2.project.milestone;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONArray;
import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Order;
import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;
import com.chinacreator.c2.dao.mybatis.enhance.Sortable;
import com.chinacreator.c2.dao.reflection.EntityReflector;
import com.chinacreator.c2.dao.reflection.invoker.Invoker;
import com.chinacreator.c2.project.issue.IssueMilestone;
import com.chinacreator.c2.project.log.Change;
import com.chinacreator.c2.project.stage.Stage;
import com.chinacreator.c2.project.story.Story;
import com.chinacreator.c2.project.task.Task;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;

public class MilestoneService {

	/**
	 * 获取里程碑
	 * 
	 * @param id
	 * @return
	 */
	public Milestone getMilestoneById(int id) {

		Assert.notNull(id, "参数不正确");
		Dao<Milestone> dao = DaoFactory.create(Milestone.class);
		Milestone m = new Milestone();
		m.setId(id);
		return dao.selectByID(m);
	}

	/**
	 * 添加
	 * 
	 * @param Milestone
	 * @return
	 */
	public Milestone addMilestone(Milestone milestone,String template) {
	  Dao<Milestone> dao = DaoFactory.create(Milestone.class);
	  if (StringUtils.isEmpty(milestone.getTitle())) {
	    throw new RuntimeException("参数不正确");
	  }
	  milestone.setStatus("1");//打开状态
	  dao.insert(milestone);
	  if(template!=null&&!template.equals("")){//新增里程碑时可选择模板信息
	    Dao<Stage> stageDao = DaoFactory.create(Stage.class);
	    String[] names = template.split(" > ");
	    int i=1;
	    if(names.length==5) i=0;
	    for(String s:names){
	      Stage stage = new Stage();
	      stage.setName(s);
	      stage.setProjectId(milestone.getProjectId());
	      stage.setMilestoneId(milestone.getId());
	      stage.setOrderNo(i++);//阶段最大排序号
	      stageDao.insert(stage);
	    }
	  }
		
	  return milestone;
	}
	
	private Integer getMaxOrderNo(Integer projectId,Integer integer) {
		Dao<Stage> dao = DaoFactory.create(Stage.class);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("projectId", projectId);
		map.put("milesonteId", integer);
		Stage stage = dao.getSession().selectOne("com.chinacreator.c2.project.stage.StageMapper.getMaxOrderNo", map);
		return stage!=null?stage.getOrderNo()+1:1;
	}

	/**
	 * 删除
	 * 
	 * @param Milestone
	 * @return
	 */
	@Transactional
	public boolean deleteMilestone(Milestone milestone) {
		Assert.notNull(milestone.getId(), "参数不正确");
		Dao<Milestone> daoMilestone = DaoFactory.create(Milestone.class);
		Dao<IssueMilestone> daoIssueMilestone = DaoFactory
				.create(IssueMilestone.class);

		Dao<Task> daoTask = DaoFactory.create(Task.class);
		Task task = new Task();
		task.setMilestoneId(milestone.getId());
		daoTask.getSession().delete("com.chinacreator.c2.project.task.TaskMapper.deleteByMilestoneId", task);//删除任务

		Dao<Story> daoStory = DaoFactory.create(Story.class);
		Story story = new Story();
		story.setMilestone(milestone);
		daoStory.getSession().delete("com.chinacreator.c2.project.story.StoryMapper.deleteByMilestoneId", story);//删除需求
		
		Dao<Stage> daoStage = DaoFactory.create(Stage.class);
		Stage stage = new Stage();
		stage.setMilestoneId(milestone.getId());
		daoStage.getSession().delete("com.chinacreator.c2.project.stage.StageMapper.deleteByMilestoneId", stage);//删除看板

		
		IssueMilestone im = new IssueMilestone();
		im.setMilestoneId(milestone.getId());
		daoIssueMilestone
				.getSession()
				.delete("com.chinacreator.c2.project.issue.IssueMilestoneMapper.deleteByMilestoneId",
						im);
		daoMilestone.delete(milestone);
		return true;
	}

	/**
	 * 修改
	 * 
	 * @param Milestone
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public boolean updateMilestone(Milestone milestone, String comment)
			throws Exception {

		Assert.notNull(milestone.getProjectId(), "参数不正确");
		Assert.notNull(milestone.getTitle(), "参数不正确");
		Dao<Milestone> dao = DaoFactory.create(Milestone.class);
		Milestone oldMilestone = dao.selectByID(milestone.getId());
		Map<String, Map<String, Object>> diffMap = compareEntity(oldMilestone,
				milestone);
		Timestamp opendate=oldMilestone.getOpendate();
		if (diffMap != null && diffMap.size() > 0) {
			JSONArray changeArray = new JSONArray();
			MilestoneHistory history = new MilestoneHistory();
			String targetType = Constants.MilestoneTargetType.UPDATE.getVal();
			
			for (String col : diffMap.keySet()) {
				if (Constants.MilestoneEntity.status.getVal().equals(col)) {
					targetType = Constants.MilestoneTargetType.UPDATE_STATUS
							.getVal();
					opendate=new Timestamp(System.currentTimeMillis());
				}
				Change change = new Change();
				change.setName(Constants.MilestoneEntity.toMap().get(col));
				Map<String, Object> valMap = diffMap.get(col);
				Object newVal = translateColVal2Name(col, valMap.get("newVal"));
				Object oldVal = translateColVal2Name(col, valMap.get("oldVal"));
				change.setOldVal(oldVal);
				change.setNewVal(newVal);
				change.setType(oldVal.getClass().getSimpleName());
				changeArray.add(change);
			}
			WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
			User user = OrgUserService.getUserInfoById(context.getUser().getId());
			String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
			history.setUserId(currentUserName);
			history.setContent(comment);
			history.setMilestoneId(milestone.getId());
			history.setAttrVal(changeArray.toJSONString());
			history.setTargetId(milestone.getId());
			history.setType(targetType);
			Dao<MilestoneHistory> historyDao = DaoFactory
					.create(MilestoneHistory.class);
			historyDao.insert(history);
		}
		milestone.setOpendate(opendate);
		dao.update(milestone);
		return true;
	}

	public Object translateColVal2Name(String col, Object val) {
		Object name = val;
		if (Constants.MilestoneEntity.status.getVal().equals(col)) {
			name = Constants.MilestoneStatus.toMap().get(val);
		}
		return name;
	}

	/**
	 * 里程碑查询服务
	 * 
	 * @param condition
	 * @return
	 */
	public List<Milestone> searchMilestoneList(Map<String, Object> condition) {

		Milestone milestone = new Milestone();
		if (null != condition && null != condition.get("name")) {
			milestone.setTitle((String) condition.get("name"));
		}
		Dao<Milestone> dao = DaoFactory.create(Milestone.class);
		List<Milestone> list = dao.select(milestone);
		return list;

	}
	
	/**
	 * 查询未关闭的里程碑
	 * @param milestone
	 * @return
	 */
	public Map<String,Object> getMilestoneList(Milestone milestone){
		Dao<Milestone> milestoneDao = DaoFactory.create(Milestone.class);
		//List<Milestone> reList = milestoneDao.select(milestone);
		List<Milestone> reList = milestoneDao.getSession().selectList("selectAllUnclosedMilestone", milestone);
		Milestone e = new Milestone();
		e.setProjectId(milestone.getProjectId());
		e.setTitle("未分配里程碑");
		e.setId(0);
		reList.add(e);
		Map<String, Object> reData = new HashMap<String, Object>();
		List<Map<String, Object>> milestoneList = new ArrayList<Map<String, Object>>();
		for (Milestone milestoneObj : reList) {

			// 统计issue
			Map<String, Object> milestoneMap = new HashMap<String, Object>();
			Milestone milestoneRow = (Milestone) milestoneObj;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("milestoneId", milestoneRow.getId());
			milestoneMap.put("id", milestoneRow.getId());
			milestoneMap.put("title", milestoneRow.getTitle());
			milestoneMap.put("product", milestoneRow.getProjectId());
			milestoneMap.put("description", milestoneRow.getDescription());
			milestoneMap.put("endDate", milestoneRow.getEnddate());
			milestoneMap.put("openDate", milestoneRow.getOpendate());
			milestoneMap.put("status", milestoneRow.getStatus());
			milestoneMap.put("closeDate", milestoneRow.getClosedate());
			// 统计任务
			params.put("projectId", milestone.getProjectId());
			Map<String, Object> milestoneTaskReport = getMilestoneTaskReport(params);
			milestoneMap.put("milestoneTaskReport", milestoneTaskReport);

			// 统计需求
			Map<String, Object> milestoneStoryReport = getMilestoneStoryReport(params);
			milestoneMap.put("milestoneStoryReport", milestoneStoryReport);

			// 统计BUG
			Map<String, Object> milestoneBugReport = getMilestoneBugReport(params);
			milestoneMap.put("milestoneBugReport", milestoneBugReport);

			//计算进度
			milestoneMap.put("percent", getPercent(milestoneStoryReport,milestoneTaskReport));
			milestoneList.add(milestoneMap);
		}

		reData.put("rows", milestoneList);

		return reData;
	}

	private Object getPercent(Map<String, Object> milestoneStoryReport,
			Map<String, Object> milestoneTaskReport) {
		DecimalFormat df = new DecimalFormat("0.0");
		int storyClose = Integer.parseInt(milestoneStoryReport.get("closed").toString());
		int taskClose = Integer.parseInt(milestoneTaskReport.get("done").toString());
		int storyOpen = Integer.parseInt(milestoneStoryReport.get("opened").toString());
		int taskOpen = Integer.parseInt(milestoneTaskReport.get("undone").toString());
		int sum = storyClose+taskClose+storyOpen+ taskOpen;
		if (sum == 0) {
			return 0;
		}
		int close = storyClose+taskClose;
		return df.format((float)close / sum*100);
	}
	
	/**
	 * 分页获取已关闭的里程碑列表
	 * @param pageIndex
	 * @param pageSize
	 * @param condition
	 * @return
	 */
	public Map<String,Object> getCloseMilestoneListForPage(int pageIndex,
			int pageSize, Map<String, Object> condition){
		return getMilestoneByPage(pageIndex, pageSize, condition,"getCloseMilestoneList");
	}

	private Map<String, Object> getMilestoneByPage(int pageIndex, int pageSize,
			Map<String, Object> condition,String sql) {
		if (pageIndex <= 0)
			pageIndex = 1;

		Map<String, Object> reData = new HashMap<String, Object>();

		if (null == condition.get("product")
				|| "".equals(condition.get("product").toString())) {
			reData.put("rows", new ArrayList());
			reData.put("totalCount", 0);
			return reData;
		}

		int product = Integer.parseInt(condition.get("product").toString());

		Dao dao = DaoFactory.create(Milestone.class);

		Milestone milestone = new Milestone();
		milestone.setTitle(null == condition ? null : "".equals(condition
				.get("title")) ? null : (String) condition.get("title"));
		milestone.setProjectId(product);

		RowBounds4Page rowBounds = new RowBounds4Page((pageIndex - 1)
				* pageSize, pageSize, new Sortable(new Order("id", "desc")));
		List<Object> reList = dao
				.getSession()
				.selectList(
						"com.chinacreator.c2.project.milestone.MilestoneMapper."+sql,
						milestone, rowBounds);

		int totalCount = dao.count(milestone);
		reData.put("totalCount", totalCount);

		List<Map<String, Object>> milestoneList = new ArrayList<Map<String, Object>>();
		for (Object milestoneObj : reList) {

			// 统计issue
			Map<String, Object> milestoneMap = new HashMap<String, Object>();
			Milestone milestoneRow = (Milestone) milestoneObj;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("milestoneId", milestoneRow.getId());
			milestoneMap.put("id", milestoneRow.getId());
			milestoneMap.put("title", milestoneRow.getTitle());
			milestoneMap.put("product", milestoneRow.getProjectId());
			milestoneMap.put("description", milestoneRow.getDescription());
			milestoneMap.put("endDate", milestoneRow.getEnddate());
			milestoneMap.put("openDate", milestoneRow.getOpendate());
			milestoneMap.put("status", milestoneRow.getStatus());
			milestoneMap.put("closeDate", milestoneRow.getClosedate());
			// 统计任务
			params.put("projectId", product);
			Map<String, Object> milestoneTaskReport = getMilestoneTaskReport(params);
			milestoneMap.put("milestoneTaskReport", milestoneTaskReport);

			// 统计需求
			Map<String, Object> milestoneStoryReport = getMilestoneStoryReport(params);
			milestoneMap.put("milestoneStoryReport", milestoneStoryReport);

			// 统计BUG
			Map<String, Object> milestoneBugReport = getMilestoneBugReport(params);
			milestoneMap.put("milestoneBugReport", milestoneBugReport);

			milestoneList.add(milestoneMap);
		}

		reData.put("rows", milestoneList);

		return reData;
	}

	/**
	 * 分页获取里程碑列表
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public Map<String, Object> getMilestoneListForPage(int pageIndex,
			int pageSize, Map<String, Object> condition) {

		return getMilestoneByPage(pageIndex, pageSize, condition,"selectByPage");
	}

	public Object getMileStonesByProductId(String product) {
		Dao dao = DaoFactory.create(Milestone.class);
		Milestone milestone = new Milestone();
		milestone.setProjectId(Integer.parseInt(product));
		return dao.select(milestone);
	}

	public List<Milestone> getMilestoneListByProjectId(String projectId) {
		List<Milestone> milestones = new ArrayList<Milestone>();

		return milestones;
	}

	public List<MilestoneHistoryDTO> getMileStoneHistory(int milestoneId,
			boolean isAll) {
		Dao<MilestoneHistoryDTO> historyDao = DaoFactory
				.create(MilestoneHistoryDTO.class);
		MilestoneHistoryDTO historyArg = new MilestoneHistoryDTO();
		historyArg.setMilestoneId(milestoneId);
		return historyDao.select(historyArg);
	}

	public Map<String, Object> getMileStoneHistoryDict() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("MilestoneTargetType", Constants.MilestoneTargetType.toMap());
		map.put("MilestoneEntity", Constants.MilestoneEntity.toMap());
		map.put("MilestoneStatus", Constants.MilestoneStatus.toMap());
		return map;
	}

	/*
	 * 获取里程碑下的相关统计信息
	 */
	public Map<String, Object> getMilestoneTaskReport(Map<String, Object> search) {

		Map<String, Object> reportMap = new HashMap<String, Object>();
		Assert.notNull(search, "统计参数不正确");
		Assert.notNull(search.get("milestoneId"), "统计参数不正确");
		Assert.notNull(search.get("projectId"), "统计参数不正确");

		Dao<Object> baseDao = DaoFactory.create(Object.class);
		List<Object> reportList = baseDao
				.getSession()
				.selectList(
						"com.chinacreator.c2.project.milestone.MilestoneMapper.getMilestoneTaskReport",
						search);
		for (Object object : reportList) {
			Map<String, Object> row = (Map<String, Object>) object;
			String isDone = (String) row.get("is_done");
			Object num = row.get("num");
			if (isDone.equals("1")) {
				reportMap.put("done", num);
			} else {
				reportMap.put("undone", num);
			}
		}

		if (null == reportMap.get("done")) {
			reportMap.put("done", 0);
		}

		if (null == reportMap.get("undone")) {
			reportMap.put("undone", 0);
		}

		return reportMap;
	}

	/*
	 * 获取里程碑下的相关需求信息
	 */
	public Map<String, Object> getMilestoneStoryReport(
			Map<String, Object> search) {

		Map<String, Object> reportMap = new HashMap<String, Object>();
		Assert.notNull(search, "统计参数不正确");
		Assert.notNull(search.get("milestoneId"), "统计参数不正确");
		Assert.notNull(search.get("projectId"), "统计参数不正确");

		Dao<Object> baseDao = DaoFactory.create(Object.class);
		List<Object> reportList = baseDao
				.getSession()
				.selectList(
						"com.chinacreator.c2.project.milestone.MilestoneMapper.getMilestoneStoryReport",
						search);
		int opened = 0,closed = 0;
		for (Object object : reportList) {
			Map<String, Object> row = (Map<String, Object>) object;
			String status = (String) row.get("status_");
			Object num = row.get("num");
			if (!status.equals("0")) {
			   opened += Integer.parseInt(num.toString());
			} else {
			  closed += Integer.parseInt(num.toString());
			}
		}
		reportMap.put("opened", opened);
		reportMap.put("closed", closed);

		if (null == reportMap.get("opened")) {
			reportMap.put("opened", 0);
		}

		if (null == reportMap.get("closed")) {
			reportMap.put("closed", 0);
		}

		return reportMap;
	}

	/*
	 * 获取里程碑下的相关需求信息
	 */
	public Map<String, Object> getMilestoneBugReport(Map<String, Object> search) {
		Map<String, Object> reportMap = new HashMap<String, Object>();
		Assert.notNull(search, "统计参数不正确");
		Assert.notNull(search.get("milestoneId"), "统计参数不正确");
		Assert.notNull(search.get("projectId"), "统计参数不正确");

		Dao<Object> baseDao = DaoFactory.create(Object.class);
		List<Map<String, Object>> reportList = baseDao
				.getSession()
				.selectList(
						"com.chinacreator.c2.project.milestone.MilestoneMapper.getMilestoneBugReport",
						search);
		int opened = 0,closed = 0;
		for (Map<String, Object> object : reportList) {
			String status = object.get("status_") + "";
			Object num = object.get("num");
			if (!status.equals("0")) {
			  opened += Integer.parseInt(num.toString());
			} else {
			  closed += Integer.parseInt(num.toString());
			}
		}
		reportMap.put("opened", opened);
		reportMap.put("closed", closed);

		if (null == reportMap.get("opened")) {
			reportMap.put("opened", 0);
		}

		if (null == reportMap.get("closed")) {
			reportMap.put("closed", 0);
		}

		return reportMap;
	}

	public Map<String, Map<String, Object>> compareEntity(Object oldEntityObj,
			Object newEntityObj) throws Exception {
		// 若新对象某个属性不为空，则拿旧对象的该属性值作比对，不同则记录属性变更历史记录
		Map<String, Map<String, Object>> diffColumnMap = new HashMap<String, Map<String, Object>>();
		for (Invoker invoker : EntityReflector
				.forClass(oldEntityObj.getClass()).getColumnFieldInvokers()) {
			// 子对象不处理
			Map<String, Object> valueMap = new HashMap<String, Object>();
			Column anno = (Column) invoker.getAnnotation();
			if (anno.association())
				continue;

			Object newVal = invoker.getObj(newEntityObj);
			Object oldVal = invoker.getObj(oldEntityObj);
			if (newVal != null) {
				// 若旧对象属性为空,则记录为null
				if (oldVal == null) {
					valueMap.put("oldVal", "");
				} else if (oldVal.toString().equals(newVal.toString())) {
					continue;
				} else {
					valueMap.put("oldVal", oldVal);
				}
				valueMap.put("newVal", newVal);
				diffColumnMap.put(anno.id(), valueMap);
			}
		}

		return diffColumnMap;

	}
	
	
	/**
	 * 获取所有未关闭的里程碑
	 * @param milestone
	 * @return
	 */
	public List<Milestone> selectAllUnclosedMilestone(Milestone milestone){
		Dao<Milestone> milestoneDao = DaoFactory.create(Milestone.class);
		return milestoneDao.getSession().selectList("selectAllUnclosedMilestone",milestone);
	}
}
