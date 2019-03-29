package com.chinacreator.c2.project.issue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;
import com.chinacreator.c2.dao.mybatis.enhance.Sortable;
import com.chinacreator.c2.project.label.Label;
import com.chinacreator.c2.project.milestone.Milestone;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;

public class IssueService {

	/**
	 * 分页获取用户列表
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @param condition
	 * @return
	 */
	public List<User> getAuthorListForPage(int pageIndex, int pageSize,
			Map<String, Object> condition) {
		Page<User> userPage = OrgUserService.queryByUser(pageIndex,pageSize);
		return userPage.getContents();
	}

	/**
	 * 分页获取问题列表
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public Map<String, Object> getIssueListForPage(int pageIndex, int pageSize,
			Map<String, Object> condition) {

		Map<String, Object> reData = new HashMap<String, Object>();
		
		
		String product=(null==condition?null:String.valueOf(condition.get("product")));
		if(null==product||"".equals(product)){
			reData.put("rows",new ArrayList());
			reData.put("totalCount",0);
			return reData;
		}

		Dao daoIssue = DaoFactory.create(Issue.class);

		Dao daoComment = DaoFactory.create(IssueComment.class);
		Sortable sortable = new Sortable();

		// 拆分参数
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("product",product);
		String searchStr = (String) condition.get("searchStr");

		if (StringUtils.isNotEmpty(searchStr)) {

			String title = null;
			String[] searchs = searchStr.split(" ");
			List<String> labelList = new ArrayList<String>();
			for (String searchField : searchs) {
				if (searchField.contains(":")) {
					String[] keyValue = searchField.split(":");
					if (keyValue.length == 2
							&& StringUtils.isNotEmpty(keyValue[1])) {
						if ("status".equals(keyValue[0])) {
							if ("open".equals(keyValue[1])) {
								paramMap.put(keyValue[0], "1");
							} else if ("close".equals(keyValue[1])) {
								paramMap.put(keyValue[0], "0");
							} else {
								paramMap.put(keyValue[0], keyValue[1]);
							}
						} else if ("label".equals(keyValue[0])) {
							labelList.add(keyValue[1]);
						} else {
							paramMap.put(keyValue[0], keyValue[1]);
						}
					}
				} else {
					paramMap.put("title", searchField);
				}
			}

			if (labelList.size() > 0) {
				paramMap.put("labels", labelList);
			}

			if (searchs.length <= 0) {
				paramMap.put("title", searchStr);
			}
		}

		RowBounds4Page rowBounds = new RowBounds4Page((pageIndex - 1)
				* pageSize, pageSize, null);
		List<Object> reList = daoIssue
				.getSession().selectList(
						"com.chinacreator.c2.project.issue.IssueMapper.selectIssueCommentByPage",
						paramMap, rowBounds);

		
		Dao daoLabel = DaoFactory.create(Label.class);
		
		for (Object rowObj : reList) {
			Map<String, Object> row = (Map<String, Object>) rowObj;
			
			int issueId =Integer.parseInt(row.get("id").toString());
			IssueComment ic = new IssueComment();
			ic.setIssueId(issueId);
			int replyCount = daoComment.count(ic);
			row.put("replyCount", replyCount);
			
			//查询标签
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("issueId",issueId);
			List labelList=daoLabel.getSession().selectList("com.chinacreator.c2.project.label.LabelMapper.selectLabelByIssueId",params);
			row.put("labelList",labelList);
		}
		
		reData.put("rows", reList);

		// 获取打开和关闭的总数
		List<Object> reOpenCount = daoIssue.getSession().selectList(
				"com.chinacreator.c2.project.issue.IssueMapper.selectIssueOpenCount",
				paramMap);
		int OpenCount = (Integer) reOpenCount.get(0);
		List<Object> reCloseCount = daoIssue.getSession().selectList(
				"com.chinacreator.c2.project.issue.IssueMapper.selectIssueCloseCount",
				paramMap);
		int closeCount = (Integer) reCloseCount.get(0);
		reData.put("openCount", OpenCount);
		reData.put("closeCount", closeCount);

		return reData;
	}

	/**
	 * 新增issue
	 * 
	 * @param issueMap
	 */
	@Transactional
	public void addIssue(Map<String, Object> issueMap) {
		
		Assert.notEmpty(issueMap, "参数不能为空！");
		
		String title = (String) issueMap.get("title");
		Object projectObj=issueMap.get("product");
		Assert.hasText(title, "标题不能为空！");
		Assert.notNull(projectObj,"产品编号不能为空！");
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
		
		if (null == uu || uu.get("user_id").toString().trim().equals("")) {
			throw new RuntimeException("作者为空！");
		}
		int product =Integer.parseInt(projectObj.toString());
		String content = (String) issueMap.get("content");
		List<String> labels = (List<String>) issueMap.get("labels");

		Issue issue = new Issue();
		issue.setTitle(title);
		issue.setProject(product);
		issue.setStatus("1");

		IssueComment issueComment = new IssueComment();
		issueComment.setAuthorId(uu.get("user_id").toString());
		issueComment.setContent(content);
		issueComment.setCreatetime(new Timestamp(System.currentTimeMillis()));

		Dao<Issue> issueDao = DaoFactory.create(Issue.class);
		issueDao.getSession().insert(
				"com.chinacreator.c2.project.issue.IssueMapper.insert", issue);

		int issueId = issue.getId();
		issueComment.setIssueId(issueId);

		Dao<IssueComment> issueCommentDao = DaoFactory
				.create(IssueComment.class);
		issueCommentDao.getSession().insert(
				"com.chinacreator.c2.project.issue.IssueCommentMapper.insert",
				issueComment);

		if (null != labels && !labels.isEmpty()) {
			List<IssueLabel> issueLabels = new ArrayList<IssueLabel>();
			for (String labelId : labels) {
				if (null != labelId&&!"".equals(labelId)) {
					IssueLabel issueLabel = new IssueLabel();
					issueLabel.setIssueId(issueId);
					issueLabel.setLabelId(Integer.parseInt(labelId));
					issueLabels.add(issueLabel);
				}
			}
			if (!issueLabels.isEmpty()) {
				Dao<IssueLabel> issueLabelDao = DaoFactory
						.create(IssueLabel.class);
				issueLabelDao.insertBatch(issueLabels);
			}
		}
	}

	private Map<String, Object> queryUserById() {
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
		
		String userId = uu.get("user_id").toString();
		Dao<Object> dao = DaoFactory.create(Object.class);
		return dao.getSession().selectOne("com.chinacreator.c2.project.info.MemberMapper.queryUserById", userId);
	}
	
	/**
	 * 获取问题回复
	 * 
	 * @param issueId
	 * @return 问题回复集合
	 */
	public Map<String, Object> getIssueCommentByIssueId(String issueId) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (null != issueId && !issueId.trim().equals("")) {
			Map<String, Object> userDTO = queryUserById();
			if (null == userDTO) {
				throw new RuntimeException("当前用户为空！");
			}

			Dao<Issue> issueDao = DaoFactory.create(Issue.class);
			Map<String, Object> issueMap = issueDao
					.getSession()
					.selectOne(
							"com.chinacreator.c2.project.issue.IssueMapper.selectIssueByIssueId",
							issueId);

			Dao<IssueComment> issueCommentDao = DaoFactory
					.create(IssueComment.class);
			List<Map<String, Object>> issueCommentList = issueCommentDao
					.getSession()
					.selectList(
							"com.chinacreator.c2.project.issue.IssueCommentMapper.selectIssueCommentByIssueId",
							issueId);

			int product =Integer.parseInt(issueMap.get("product").toString());

			List<Label> allLabels = null;
			List<Label> labels = null;

			List<Milestone> allMilestones = null;
			Milestone milestones = null;

			Integer projectId = null;

			Label label = new Label();
			label.setProject(product);

			Dao<Label> labelDao = DaoFactory.create(Label.class);
			allLabels = labelDao.getSession().selectList(
					"com.chinacreator.c2.project.label.LabelMapper.select", label);

			labels = labelDao.getSession().selectList(
							"com.chinacreator.c2.project.label.LabelMapper.selectByIssueId",
							issueId);

			Milestone milestone = new Milestone();
			milestone.setProjectId(product);

			Dao<Milestone> milestoneDao = DaoFactory.create(Milestone.class);
			allMilestones = milestoneDao.getSession().selectList(
					"com.chinacreator.c2.project.milestone.MilestoneMapper.select",
					milestone);

			milestones = milestoneDao.getSession().selectOne(
							"com.chinacreator.c2.project.milestone.MilestoneMapper.selectByIssueId",
							issueId);

//				ProjectProduct ppBean = new ProjectProduct();
//				ppBean.setProduct(product.intValue());
//				Dao<ProjectProduct> ppDao = DaoFactory
//						.create(ProjectProduct.class);
//				ProjectProduct projectProduct = ppDao
//						.getSession()
//						.selectOne(
//								"com.chinacreator.c2.izentao.ProjectProductMapper.selectByBean",
//								ppBean);

//				if (null == projectProduct
//						|| null == projectProduct.getProduct()) {
//					throw new TaskEstimateSynException("产品编号为空！");
//				}

//				projectId = projectProduct.getProject();


//			List<Task> tasks = new ArrayList<Task>();
			int taskClosed = 0;

			Dao<IssueTask> issueTaskDao = DaoFactory.create(IssueTask.class);
			List<IssueTask> issueTasks = issueTaskDao
					.getSession()
					.selectList(
							"com.chinacreator.c2.project.issue.IssueTaskMapper.selectByIssueId",
							issueId);
//
//			if (null != issueTasks && !issueTasks.isEmpty()) {
//				Dao<Task> taskDao = DaoFactory.create(Task.class);
//				for (IssueTask issueTask : issueTasks) {
//					Task task = taskDao.selectByID(issueTask.getTaskId());
//					tasks.add(task);
//					if ("done".equals(task.getStatus())
//							|| "closed".equals(task.getStatus())) {
//						taskClosed++;
//					}
//				}
//			}

			String newAuthor = userDTO.get("user_realname").toString();
			String newAuthorId = userDTO.get("user_id").toString();
			OrgUserService service = new OrgUserService();
			boolean isCanClosed = service.isPermitted("isCanClosed");
			boolean isEditLabels = service.isPermitted("isEditLabels");
			boolean isEditMilestones = service.isPermitted("isEditMilestones");
			boolean isAddTask = service.isPermitted("isAddTask");

			map.put("newAuthorId", newAuthorId);
			map.put("newAuthor", newAuthor);
			map.put("isCanClosed", isCanClosed);
			map.put("isEditLabels", isEditLabels);
			map.put("isEditMilestones", isEditMilestones);
			map.put("isAddTask", isAddTask);
			map.putAll(issueMap);
			map.put("comments", issueCommentList);
			map.put("allLabels", allLabels);
			map.put("labels", labels);
			map.put("allMilestones", allMilestones);
			map.put("milestones", milestones);
//			map.put("tasks", tasks);
			map.put("taskClosed", taskClosed);
			map.put("projectId", projectId);
		}
		return map;
	}

	/**
	 * 新增问题回复
	 * 
	 * @param issueCommentMap
	 */
	@Transactional
	public void addIssueComment(Map<String, Object> issueCommentMap) {
		if (null == issueCommentMap || issueCommentMap.isEmpty()) {
			throw new RuntimeException("参数为空！");
		}
		
		int issueId = Integer.parseInt(issueCommentMap.get("issueId").toString());

		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
		
		if (null == uu || uu.get("user_id").toString().trim().equals("")) {
			throw new RuntimeException("作者为空！");
		}

		String content = (String) issueCommentMap.get("content");
		if (null == content || content.trim().equals("")) {
			throw new RuntimeException("内容为空！");
		}

		IssueComment issueComment = new IssueComment();
		issueComment.setIssueId(issueId);
		issueComment.setAuthorId(uu.get("user-id").toString());
		issueComment.setCreatetime(new Timestamp(System.currentTimeMillis()));
		issueComment.setContent(content);

		Dao<IssueComment> issueCommentDao = DaoFactory
				.create(IssueComment.class);
		issueCommentDao.getSession().insert(
				"com.chinacreator.c2.project.issue.IssueCommentMapper.insert",
				issueComment);
	}

	/**
	 * 编辑问题回复
	 * 
	 * @param issueComment
	 */
	@Transactional
	public void editIssueComment(IssueComment issueComment) {
		if (null == issueComment) {
			throw new RuntimeException("参数为空！");
		}

		String content = issueComment.getContent();
		if (null == content || content.trim().equals("")) {
			throw new RuntimeException("内容为空！");
		}
		issueComment.setAuthorId(null);
		issueComment.setCreatetime(null);
		issueComment.setIssueId(null);

		Dao<IssueComment> issueCommentDao = DaoFactory
				.create(IssueComment.class);
		issueCommentDao.getSession().update(
				"com.chinacreator.c2.project.issue.IssueCommentMapper.update",
				issueComment);
	}

	/**
	 * 修改问题状态
	 * 
	 * @param issueId
	 * @param status
	 */
	@Transactional
	public void editIssueStatus(int issueId, String status) {

		if (null == status || status.trim().equals("")) {
			throw new RuntimeException("问题状态为空！");
		} else if (!"1".equals(status) && !"0".equals(status)) {
			throw new RuntimeException("问题状态" + status + "错误！");
		}
		Issue issue = new Issue();
		issue.setId(issueId);
		issue.setStatus(status);

		Dao<Issue> issueDao = DaoFactory.create(Issue.class);
		issueDao.getSession().update(
				"com.chinacreator.c2.project.issue.IssueMapper.update", issue);
	}

	
	/**
	 * 获取产品编号下所有标签
	 * 
	 * @param productId
	 * @return
	 */
	public Map<String, Object> getAllLabels(int product) {
		Map<String, Object> map = new HashMap<String, Object>();

		Map<String, Object> userDTO = queryUserById();
		if (null == userDTO) {
			throw new RuntimeException("当前用户为空！");
		}
		String author = userDTO.get("user_realname").toString();

		map.put("author", author);
		map.put("product", product);

		Label label = new Label();
		label.setProject(product);

		Dao<Label> labelDao = DaoFactory.create(Label.class);
		List<Label> labelList = labelDao.getSession().selectList(
				"com.chinacreator.c2.project.label.LabelMapper.select", label);

		map.put("allLabels", labelList);
	

		return map;
	}

	/**
	 * 设置问题标签
	 * 
	 * @param label
	 */
	@Transactional
	public void setIssueLabel(Map<String, Object> label) {
		
		Assert.notEmpty(label,"参数不能为空！");
		Assert.notNull(label.get("issueId"),"issueId不能为空！");
		Assert.notNull(label.get("labelId"),"labelId不能为空！");
		Assert.notNull(label.get("selected"),"selected不能为空！");
		
		int issueId =Integer.parseInt(label.get("issueId").toString());
		int labelId =Integer.parseInt(label.get("labelId").toString());
		Boolean selected = (Boolean) label.get("selected");
		
		IssueLabel issueLabel = new IssueLabel();
		issueLabel.setIssueId(issueId);
		issueLabel.setLabelId(labelId);

		Dao<IssueLabel> issueLabelDao = DaoFactory.create(IssueLabel.class);
		int count = issueLabelDao.count(issueLabel);

		if (selected) {
			if (count == 0) {
				issueLabelDao.insert(issueLabel);
			}
		} else {
			if (count > 0) {
				issueLabelDao
						.getSession()
						.delete("com.chinacreator.c2.project.issue.IssueLabelMapper.deleteByissueIdAndLabelId",
								issueLabel);
			}
		}
	}

	/**
	 * 设置问题里程碑
	 * 
	 * @param milestone
	 */
	@Transactional
	public void setIssueMilestone(Map<String, Object> milestone) {
		
		Assert.notEmpty(milestone,"参数不能为空！");
		Assert.notNull(milestone.get("issueId"),"issueId不能为空！");
		Assert.notNull(milestone.get("milestoneId"),"milestoneId不能为空！");
		Assert.notNull(milestone.get("selected"),"selected不能为空！");
		
		int issueId =Integer.parseInt(milestone.get("issueId").toString());
		int milestoneId =Integer.parseInt(milestone.get("milestoneId").toString());
		Boolean selected = (Boolean) milestone.get("selected");

		IssueMilestone issueMilestone = new IssueMilestone();
		issueMilestone.setIssueId(issueId);
		issueMilestone.setMilestoneId(milestoneId);
		Dao<IssueMilestone> issueMilestoneDao = DaoFactory
				.create(IssueMilestone.class);

		issueMilestoneDao
				.getSession()
				.delete("com.chinacreator.c2.project.issue.IssueMilestoneMapper.deleteByIssueId",
						issueMilestone);

		if (selected) {
			issueMilestoneDao.insert(issueMilestone);
		}
	}

	
	/**
	 * 新增问题任务
	 * 
	 * @param task
	 */
	@Transactional
	public void addIssueTask(Map<String, Object> task) {
		
		Assert.notEmpty(task,"参数不能为空！");
		Assert.notNull(task.get("issueId"),"issueId不能为空！");
		Assert.notNull(task.get("taskId"),"taskId不能为空！");
		
		int issueId =Integer.parseInt(task.get("issueId").toString());
		Integer taskId = (Integer) task.get("taskId");

		IssueTask issueTask = new IssueTask();
		issueTask.setIssueId(issueId);
		issueTask.setTaskId(taskId);

		Dao<IssueTask> issueTaskDao = DaoFactory.create(IssueTask.class);
		int conut = issueTaskDao.count(issueTask);

		if (conut == 0) {
			issueTaskDao.insert(issueTask);
		}
	}
}
