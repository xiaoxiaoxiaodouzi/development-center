package com.chinacreator.c2.project.bug;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.DynamicDataSource;
import com.chinacreator.c2.dao.mybatis.enhance.Order;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.dao.mybatis.enhance.Pageable;
import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;
import com.chinacreator.c2.dao.mybatis.enhance.Sortable;
import com.chinacreator.c2.project.bug.BugConstans.BUG_STATUS;
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.project.label.Label;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;
//import com.chinacreator.c2.search.C2ElasticSearchService;

public class BugService {

	/**
	 * 分页获取bug列表
	 * 
	 * @param condition
	 * @param pageable
	 * @return
	 */
	public Map<String, Object> getBugList(Map<String, Object> condition, Pageable pageable) {
		Dao<Object> dao = DaoFactory.create(Object.class);
		// Sortable sb=new Sortable(new Order("orderNo",Order.DIRECTION_DESC));
		RowBounds4Page page = new RowBounds4Page(pageable, null, null, true);

		Map<String, Object> weekMap = (Map<String, Object>) condition.get("week");
		if (weekMap == null) {
			weekMap = new HashMap<String, Object>();
			condition.put("week", weekMap);
		}
		if (weekMap != null && weekMap.get("et") != null) {
			Date et = new Date(Long.parseLong(weekMap.get("et").toString()));
			weekMap.put("et", et);
		}
		if (weekMap != null && weekMap.get("st") != null) {
			Date et = new Date(Long.parseLong(weekMap.get("st").toString()));
			weekMap.put("st", et);
		}

		List<Map<String, Object>> bugList = dao.getSession()
				.selectList("com.chinacreator.c2.project.bug.BugMapper.selectBugListByPage", condition, page);

		Dao<BugLabel> bugLabelDao = DaoFactory.create(BugLabel.class);

		List<BugLabel> blList = new ArrayList<BugLabel>();

		if (!CollectionUtils.isEmpty(bugList)) {

			for (int i = 0; i < bugList.size(); i++) {
				Map<String, Object> bugMap = bugList.get(i);
				// 查询label关联
				BugLabel bugLabel = new BugLabel();
				bugLabel.setBugId((Integer) bugMap.get("id"));
				blList.add(bugLabel);
			}

			// 统一查询所有label
			List<HashMap<String, Object>> labels = bugLabelDao.getSession().selectList("selectLabelsByBugIds", blList);
			Map<Integer, List<Map<String, Object>>> bugIdMappingLabel = new HashMap<Integer, List<Map<String, Object>>>();
			for (Map<String, Object> labelMap : labels) {
				Integer bugId = (Integer) labelMap.get("bug_id");
				if (!bugIdMappingLabel.containsKey(bugId)) {
					bugIdMappingLabel.put(bugId, new ArrayList<Map<String, Object>>());
				}
				bugIdMappingLabel.get(bugId).add(labelMap);
			}

			for (int i = 0; i < bugList.size(); i++) {
				Map<String, Object> bugMap = bugList.get(i);
				Integer bugId = (Integer) bugMap.get("id");
				bugMap.put("labels", bugIdMappingLabel.get(bugId));
			}

		}

		Page<Map<String, Object>> rePage = new Page<Map<String, Object>>(pageable.getPageIndex(),
				pageable.getPageSize(), page.getTotalSize(), bugList);
		Map<String, Object> reMap = new HashMap<String, Object>();
		reMap.put("contents", rePage.getContents());
		reMap.put("pageIndex", rePage.getPageIndex());
		reMap.put("pageSize", rePage.getPageSize());
		reMap.put("total", rePage.getTotal());
		reMap.put("totalPage", rePage.getTotalPage());

		// 统计各状态数量
		Map<String, Object> statusMap = new HashMap<String, Object>();
		List<Map<String, Object>> bugStatusCount = dao.getSession()
				.selectList("com.chinacreator.c2.project.bug.BugMapper.selectBugStatusCount", condition);

		Long bugStatusCountTotal = 0l;
		for (Map<String, Object> map : bugStatusCount) {
			Integer status = (Integer) map.get("status_");
			Long count = (Long) map.get("count");
			for (BUG_STATUS bs : BUG_STATUS.values()) {
				if (bs.ordinal() == status) {
					statusMap.put(bs.name(), count);
				}
			}

			bugStatusCountTotal += count;
		}
		reMap.put("bugStatusCount", statusMap);
		reMap.put("bugStatusCountTotal", bugStatusCountTotal);

		return reMap;
	}

	/**
	 * 新增bug
	 * 
	 * @param bug
	 */
	public Bug addBug(Bug bug, List<Label> labelList) {

		Assert.notNull(bug.getProject(), "项目信息不能为空");
		// Assert.hasText(bug.getAssignTo(),"分配人不能为空！");
		Assert.hasText(bug.getTitle(), "标题不能为空！");
		// Assert.notNull(bug.getModule(),"模块不能为空！");

		WebOperationContext context = (WebOperationContext) OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName = user.getEmail().substring(0, user.getEmail().indexOf("@"));

		bug.setCreateBy(currentUserName);
		Timestamp tt = new Timestamp(System.currentTimeMillis());
		bug.setCreateTime(tt);
		bug.setLastUpdateTime(tt);
		bug.setStatus(BugConstans.BUG_STATUS.CREATED.ordinal());
		bug.setNo(this.generateProjectBugNo(bug.getProject().getId()));

		bug.setOrderNo(getMaxOrderNo(bug.getProject().getId()));// 排序号

		Dao<Bug> bugDao = DaoFactory.create(Bug.class);
		bugDao.insert(bug);

		if (!CollectionUtils.isEmpty(labelList)) {
			Dao<BugLabel> bugLabelDao = DaoFactory.create(BugLabel.class);
			List<BugLabel> bugLabelList = new ArrayList<BugLabel>();
			for (Label label : labelList) {
				BugLabel bl = new BugLabel();
				bl.setLabelId(label.getId());
				bl.setBugId(bug.getId());
				bugLabelList.add(bl);
			}
			bugLabelDao.insertBatch(bugLabelList);
		}
		// bug索引
		// C2ElasticSearchService.bugIndex(bug.getId().toString());
		return bug;
	}

	private Integer getMaxOrderNo(Integer projectId) {
		Dao<Bug> bugDao = DaoFactory.create(Bug.class);
		Map<String, Object> order = bugDao.getSession()
				.selectOne("com.chinacreator.c2.project.bug.BugMapper.getMaxOrderNo", projectId);
		return order != null ? Integer.parseInt(order.get("maxNo").toString()) + 1 : 1;
	}

	/**
	 * 生成项目bug编号
	 * 
	 * @param projectId
	 * @return
	 */
	public Integer generateProjectBugNo(Integer projectId) {
		Dao<Bug> bugDao = DaoFactory.create(Bug.class);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("projectId", projectId);
		Integer bugNo = bugDao.getSession().selectOne("com.chinacreator.c2.project.bug.BugMapper.selectMaxBugNo",
				params);
		if (null == bugNo)
			return 1;
		return bugNo + 1;
	}

	/**
	 * 修改bug信息
	 * 
	 * @param bug
	 * @param labels
	 */
	public void updateBug(Bug bug, List<Label> labels) {

		Assert.notNull(bug.getId());
		Assert.hasText(bug.getInitAssignedTo(), "分配人不能为空！");
		Assert.hasText(bug.getTitle(), "标题不能为空！");

		Dao<Bug> bugDao = DaoFactory.create(Bug.class);
		Bug dbBug = bugDao.selectByID(bug.getId());
		dbBug.setBugVersion(bug.getBugVersion());
		dbBug.setPri(bug.getPri());
		dbBug.setTitle(bug.getTitle());
		dbBug.setSpec(bug.getSpec());
		dbBug.setInitAssignedTo(bug.getInitAssignedTo());

		// bug最后更改时间
		dbBug.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
		if (null != bug.getMilestone())
			dbBug.setMilestone(bug.getMilestone());
		if (null != bug.getProject())
			dbBug.setProject(bug.getProject());
		if (null != bug.getModule())
			dbBug.setModule(bug.getModule());
		if(null != bug.getProjectPlan())
		  dbBug.setProjectPlan(bug.getProjectPlan());
		bugDao.update(dbBug);

		// 修改bug标签关联信息
		this.cleanBugLabels(bug.getId());

		if (!CollectionUtils.isEmpty(labels)) {
			Dao<BugLabel> bugLabelDao = DaoFactory.create(BugLabel.class);
			List<BugLabel> bugLabelList = new ArrayList<BugLabel>();
			for (Label label : labels) {
				BugLabel bl = new BugLabel();
				bl.setLabelId(label.getId());
				bl.setBugId(bug.getId());
				bugLabelList.add(bl);
			}
			bugLabelDao.insertBatch(bugLabelList);
		}

		// bug索引
		// C2ElasticSearchService.updateBugIndex(bug.getId().toString());
	}

	/**
	 * 删除bug
	 * 
	 * @param bug
	 */
	public void deleteBug(Bug bug) {
		Assert.notNull(bug);

		Dao<Bug> bugDao = DaoFactory.create(Bug.class);
		bugDao.delete(bug);

		// 删除bug索引
		// C2ElasticSearchService.deleteBugIndex(bug.getId()+"");
	}

	/**
	 * 修改bug状态
	 * 
	 * @param bugId
	 *            bugID
	 * @param status
	 *            bug状态
	 * @param updateUserName
	 *            修改人
	 * @param comment
	 *            描述
	 */
	public void changeBugStatus(Integer bugId, Integer status, String comment, String bugVersion) {

		Assert.notNull(bugId);
		Assert.notNull(status);

		boolean flag = true;
		for (BUG_STATUS bs : BUG_STATUS.values()) {
			if (bs.ordinal() == status) {
				flag = false;
				break;
			}
		}
		if (flag)
			throw new RuntimeException("不识别的bug状态！");

		Timestamp ts = new Timestamp(System.currentTimeMillis());

		Dao<Bug> bugDao = DaoFactory.create(Bug.class);
		Bug bug = new Bug();
		bug.setId(bugId);
		bug = bugDao.selectByID(bug);
		if (null == bug)
			throw new RuntimeException("不存在bug[" + bugId + "]");
		bug.setStatus(status);
		bug.setLastUpdateTime(ts);
		bugDao.update(bug);

		// 更新bug索引
		Map<String, Object> little = new HashMap<String, Object>();
		little.put("status", status);
		little.put("lastUpdateTime", ts);
		// C2ElasticSearchService.updateBugIndexLittle(bugId.toString(),
		// little);

		// 记录bug状态变更
		Dao<BugComment> bugCommentDao = DaoFactory.create(BugComment.class);
		BugComment bc = new BugComment();
		bc.setBugId(bugId);
		bc.setContent(comment);
		bc.setType(status);

		// 记录版本
		if (StringUtils.isNotEmpty(bugVersion)) {
			bc.setBugVersion(bugVersion);
		}
		WebOperationContext context = (WebOperationContext) OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName = user.getEmail().substring(0, user.getEmail().indexOf("@"));
		bc.setUserId(currentUserName);
		bc.setCreateTime(ts);
		bugCommentDao.insert(bc);
	}

	/**
	 * 获取bug相关评论
	 * 
	 * @param storyId
	 * @return
	 */
	public Map<String, Object> getBugAndComments(Integer projectId, Integer bugNo) {

		Assert.notNull(projectId, "项目不能为空");
		Assert.notNull(bugNo, "bug编号不能为空");
		Map<String, Object> reMap = new HashMap<String, Object>();
		Dao<Bug> bugDao = DaoFactory.create(Bug.class);

		Bug bug = new Bug();
		bug.setNo(bugNo);
		Project project = new Project();
		project.setId(projectId);
		bug.setProject(project);
		bug = bugDao.selectOne(bug);
		if (null == bug) {
			throw new RuntimeException("不存在bug编号[" + bugNo + "]");
		}

		String assignedTo = bug.getInitAssignedTo();

		// Dao<Task> taskDao = DaoFactory.create(Task.class);
		// Task task=new Task();
		// task.setBugId(bug.getId());
		// Map<String,Object>
		// taskMap=(Map<String,Object>)taskDao.selectOne(task);
		// if(null!=taskMap){
		// assignedTo=(String)taskMap.get("assignedTo");
		// }else{
		// assignedTo=bug.getInitAssignedTo();
		// }
		reMap.put("projectPlan", bug.getProjectPlan());
		reMap.put("createTime", bug.getCreateTime());
		reMap.put("id", bug.getId());
		reMap.put("lastUpdateTime", bug.getLastUpdateTime());
		reMap.put("pri", bug.getPri());
		reMap.put("project", bug.getProject());
		reMap.put("status", bug.getStatus());
		reMap.put("title", bug.getTitle());
		reMap.put("spec", bug.getSpec());
		reMap.put("module", bug.getModule());
		reMap.put("milestone", bug.getMilestone());
		reMap.put("bugVersion", bug.getBugVersion());
		Map<String, Object> createBy = OrgUserService.getUserByName(bug.getCreateBy());
		reMap.put("createBy", createBy);

		Map<String, Object> assignedToDTO = OrgUserService.getUserByName(assignedTo);
		reMap.put("assignedTo", assignedToDTO);

		Dao<BugComment> bugCommentDao = DaoFactory.create(BugComment.class);
		BugComment bc = new BugComment();
		bc.setBugId(bug.getId());
		Sortable st = new Sortable(new Order("createTime", Order.DIRECTION_ASC));
		List<BugComment> bugCommentList = bugCommentDao.select(bc, st);
		List<Map<String, Object>> scList = new ArrayList<Map<String, Object>>();
		for (BugComment bugComment : bugCommentList) {
			Map<String, Object> scRow = new HashMap<String, Object>();
			scRow.put("content", bugComment.getContent());
			scRow.put("createTime", bugComment.getCreateTime());
			scRow.put("id", bugComment.getId());
			Map<String, Object> user = OrgUserService.getUserByName(bugComment.getUserId());
			scRow.put("userRealname", user.get("user_realname"));
			scRow.put("userIcon", user.get("remark1"));
			scRow.put("type", bugComment.getType());
			scRow.put("bugVersion", bugComment.getBugVersion());
			scList.add(scRow);
		}
		reMap.put("bugCommentList", scList);

		// 查询label关联
		Dao<BugLabel> bugLabelDao = DaoFactory.create(BugLabel.class);
		BugLabel bugLabel = new BugLabel();
		bugLabel.setBugId(bug.getId());
		List<Map<String, Object>> labels = bugLabelDao.getSession()
				.selectList("com.chinacreator.c2.project.bug.BugLabelMapper.selectLabelsByBugId", bugLabel);
		if (labels.size() > 0) {
			reMap.put("bugLabel", labels);
		}

		return reMap;
	}

	/**
	 * 评论bug
	 * 
	 * @param bugId
	 * @param content
	 */
	public void replyBug(int bugId, String content) {
		WebOperationContext context = (WebOperationContext) OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName = user.getEmail().substring(0, user.getEmail().indexOf("@"));

		Dao<BugComment> bugCommentDao = DaoFactory.create(BugComment.class);
		BugComment bc = new BugComment();
		bc.setBugId(bugId);
		bc.setContent(content);
		bc.setUserId(currentUserName);
		bc.setCreateTime(new Timestamp(new Date().getTime()));
		bugCommentDao.insert(bc);
	}

	/**
	 * 保存bug标签
	 * 
	 * @param bugId
	 * @param labelId
	 * @param selected
	 */
	public void saveBugLabel(Integer bugId, Integer labelId, boolean selected) {
		Assert.notNull(bugId, "bug主键不能空");
		Assert.notNull(labelId, "label主键不能空");
		Dao<BugLabel> bugLabelDao = DaoFactory.create(BugLabel.class);
		BugLabel bl = new BugLabel();
		bl.setBugId(bugId);
		bl.setLabelId(labelId);
		if (selected) {
			int count = bugLabelDao.count(bl);
			if (count <= 0) {
				bugLabelDao.insert(bl);
			}
		} else {
			bugLabelDao.getSession().delete("com.chinacreator.c2.project.bug.BugLabelMapper.deleteByBugLabel", bl);
		}
	}

	/**
	 * 清空bug标签
	 * 
	 * @param bugId
	 */
	public void cleanBugLabels(Integer bugId) {
		Assert.notNull(bugId, "bug主键不能空");
		Dao<BugLabel> bugLabelDao = DaoFactory.create(BugLabel.class);
		BugLabel bl = new BugLabel();
		bl.setBugId(bugId);
		bugLabelDao.getSession().delete("com.chinacreator.c2.project.bug.BugLabelMapper.deleteByBugLabel", bl);
	}

	/**
	 * 获取项目历史版本
	 * 
	 * @param projectId
	 * @return
	 */
	public List<Map<String, Object>> getProjectHistoryVersion(Integer projectId) {
		List<Map<String, Object>> reList = new ArrayList<Map<String, Object>>();
		Bug bug = new Bug();
		Project p = new Project();
		p.setId(projectId);
		bug.setProject(p);
		Dao<Bug> bugDao = DaoFactory.create(Bug.class);
		reList = bugDao.getSession().selectList("com.chinacreator.c2.project.bug.BugMapper.selectHistoryBugVersion",
				bug);
		return reList;
	}

	/**
	 * bug任务重新分配人。
	 * 
	 * @param bugId
	 * @param man
	 * @param changeMan
	 */
	public void changeBugMan(Integer bugId, String man, String manRealname, String changeMan) {
		Bug bug = new Bug();
		bug.setId(bugId);
		bug.setInitAssignedTo(man);
		DaoFactory.create(Bug.class).update(bug);

		BugComment bc = new BugComment();
		bc.setBugId(bugId);
		bc.setUserId(changeMan);
		bc.setCreateTime(new Timestamp(System.currentTimeMillis()));
		bc.setType(10);
		bc.setContent(manRealname);
		DaoFactory.create(BugComment.class).insert(bc);

		// DynamicDataSource.setDataSourceKey("");
	}

	public void databaseTransform() {
		SqlSession session = DaoFactory.create(Map.class).getSession();
		DynamicDataSource.setDataSourceKey("zendao");
		List<Integer> zendaoProjects = session.selectList("zendaoProjects");
		List<Map<String, Object>> zbugs = session.selectList("zendaoBugs");

		Map<Integer, String> fileProjectMap = new HashMap<Integer, String>();
		for (Map<String, Object> b : zbugs) {
			List<Map<String, Object>> fils = session.selectList("zendaoBugFile", b.get("id"));
			String fileString = "";
			for (Map<String, Object> bugfile : fils) {
				String pathname = "file/" + bugfile.get("pathname").toString();
				String fileTitle = bugfile.get("title").toString();
				String extension = bugfile.get("extension").toString();
				if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("png")) {
					fileString += "![" + fileTitle + "](" + pathname + ")";
				} else {
					fileString += "<p>[" + fileTitle + "](" + pathname + ")</p>";
				}
			}
			fileProjectMap.put(Integer.parseInt(b.get("id").toString()), fileString);
		}

		DynamicDataSource.setDataSourceKey("dev-center");
		Map<Integer, Integer> noProjectMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < zendaoProjects.size(); i++) {
			int a = zendaoProjects.get(i);
			Integer maxNo = session.selectOne("maxProjectNo", a);
			if (maxNo == null)
				maxNo = 0;
			noProjectMap.put(zendaoProjects.get(i), maxNo + 1);
		}

		List<Map<String, Object>> newBugs = new ArrayList<Map<String, Object>>();
		for (Object zen : zbugs) {
			Map<String, Object> zbug = (Map<String, Object>) zen;
			System.out.println(zbug);
			Map<String, Object> newBug = new HashMap<String, Object>();

			int no = noProjectMap.get(zbug.get("project"));
			newBug.put("no", no);
			noProjectMap.put(Integer.parseInt(zbug.get("project").toString()), no + 1);
			newBug.put("project", zbug.get("project"));
			newBug.put("title", zbug.get("title"));
			newBug.put("pri", zbug.get("severity"));
			int status = 1;
			if (zbug.get("status").equals("closed"))
				status = 0;
			if (zbug.get("status").equals("resolved"))
				status = 6;
			if (zbug.get("status").equals("active"))
				status = 1;
			newBug.put("status", status);
			newBug.put("createBy", zbug.get("openedBy"));
			newBug.put("createTime", zbug.get("openedDate"));
			String assignedTo = "";
			if (status == 0 || status == 6)
				assignedTo = zbug.get("resolvedBy").toString();
			if (status == 1)
				assignedTo = zbug.get("assignedTo").toString();
			newBug.put("initAssignedTo", assignedTo);
			newBug.put("lastUpdateTime", new Date());

			String projectFiles = fileProjectMap.get(zbug.get("id"));
			String spec = "";
			if (projectFiles != null)
				spec = zbug.get("steps") + projectFiles;
			else
				spec = zbug.get("steps").toString();
			newBug.put("spec", spec);
			session.insert("insertZendaoBug", newBug);
			newBugs.add(newBug);
		}
		System.out.println(noProjectMap);
		System.out.println(newBugs);

	}

}
