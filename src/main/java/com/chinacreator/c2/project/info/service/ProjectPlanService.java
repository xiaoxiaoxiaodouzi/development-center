package com.chinacreator.c2.project.info.service;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Conditions;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.dao.mybatis.enhance.Pageable;
import com.chinacreator.c2.dao.mybatis.enhance.Sortable;
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.project.info.ProjectPlan;
import com.chinacreator.c2.project.task.Task;
import com.chinacreator.c2.web.util.ConditionsUtil;
import com.chinacreator.c2.web.util.SortableUtil;

@Service
public class ProjectPlanService {

	public ProjectPlan update(ProjectPlan entity) {
		// TODO auto-generated method stub
		DaoFactory.create(ProjectPlan.class).update(entity);
		return entity;
	}

	public List<ProjectPlan> getPlanByproject(Integer projectId, Integer parent, Map<String, Object> search) {
		Dao<ProjectPlan> planDao = DaoFactory.create(ProjectPlan.class);
		ProjectPlan projectPlan = new ProjectPlan();
		Project project = new Project();
		project.setId(projectId);
		projectPlan.setProjectId(project);
		projectPlan.setParent(parent);
		List<ProjectPlan> planList = planDao.select(projectPlan);
		return selectPlan(planList, search);
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public List<ProjectPlan> getPlanByProjectId(Integer projectId, Integer parent, Map<String, Object> search) {
		List<ProjectPlan> planList = getPlanCompletes(projectId, parent);
		return selectPlan(planList, search);
	}

	// 计算计划完成度
	public List<ProjectPlan> getPlanCompletes(Integer projectId, Integer parent) {
		// 循环过滤查询条件
		Dao<ProjectPlan> planDao = DaoFactory.create(ProjectPlan.class);
		ProjectPlan projectPlan = new ProjectPlan();
		Project project = new Project();
		project.setId(projectId);
		projectPlan.setProjectId(project);
		projectPlan.setParent(parent);
		List<ProjectPlan> planList = planDao.select(projectPlan);
		// 查出属于这个项目的所有任务
		Task task = new Task();
		task.setProjectId(projectId);
		List<Task> tasklist = DaoFactory.create(Task.class).select(task);
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(0);
		for (ProjectPlan plan : planList) {
			int done = 0;
			int undone = 0;
			Double consumed = 0.0;
			Double left = 0.0;
			/*
			 * //找到所有的子计划
			 */
			Map<String, Object> taskNum = new HashMap<>();
			List<ProjectPlan> childList = getTreeNodes(plan, planList, new ArrayList<>());
			List<Task> childtasklist = new ArrayList<>();
			// 找出每个计划自己所有的任务
			for (Task t : tasklist) {
				if (null != t.getProjectPlan()) {
					if (t.getProjectPlan().getId().equals(plan.getId())) {
						if (t.isIsDone())
							done++;
						else
							undone++;
						consumed += t.getConsumed();
						left += t.getLeft();
					}
					// 只统计子计划的任务完成度，不添加到子集里面去
					if (childList.size() != 0) {
						for (ProjectPlan p : childList) {
							if (t.getProjectPlan().getId().equals(p.getId())) {
								if (t.isIsDone())
									done++;
								else
									undone++;
								consumed += t.getConsumed();
								left += t.getLeft();
							}
						}
					}
				}
			}
			if (0 != done + undone) {
				plan.setCompletes(numberFormat.format(((float) done / (float) (done + undone)) * 100));
			} else {
				plan.setCompletes("0");
			}
			taskNum.put("done", done);
			taskNum.put("undone", undone);
			taskNum.put("total", done + undone);
			taskNum.put("consumed", consumed);
			taskNum.put("left", left);
			plan.setTaskNum(taskNum);
			// 现在暂时不需要将任务放到计划里面去，只需要算出完成度即可；
			// plan.setTaskList(childtasklist);
		}
		return planList;
	}

	// 过滤计划
	public List<ProjectPlan> selectPlan(List<ProjectPlan> list, Map<String, Object> search) {
		List<ProjectPlan> result = new ArrayList<>();
		if (null == search)
			search = new HashMap<>();
		String name = null;
		if (null != search.get("name")) {
			name = search.get("name").toString();
		}
		Integer status = null;
		if (null != search.get("planStatus")) {
			Map<String, Integer> planStatus = (Map<String, Integer>) search.get("planStatus");
			// if (2 != planStatus.get("value"))
			status = planStatus.get("value");
		}

		Map<String, Object> week = (Map<String, Object>) search.get("week");
		Timestamp start = null;
		Timestamp end = null;
		if (null != week) {
			if (null != week.get("st"))
				start = new Timestamp((long) week.get("st"));
			if (null != week.get("et"))
				end = new Timestamp((long) week.get("et"));
		}
		List<Map<String, String>> userList = (List<Map<String, String>>) search.get("assignedToList");
		List<String> usernames = new ArrayList<>();
		if (null != userList && userList.size() > 0) {
			for (Map<String, String> user : userList) {
				usernames.add(user.get("userName"));
			}
		}
		for (ProjectPlan plan : list) {
			// 判断任务的创建时间及名字
			Boolean flag = true;

			if ((null != start && plan.getEndDate().before(start))) {
				flag = false;
			}

			if ((null != end && plan.getStartDate().after(end))) {
				flag = false;
			}

			if (null != name && plan.getName().indexOf(name) == -1) {
				flag = false;
			}

			if (null != status) {
				if (status.equals(0) && Double.parseDouble(plan.getCompletes()) >= 25) {
					flag = false;
				} else if (status.equals(1) && (Double.parseDouble(plan.getCompletes()) < 25
						|| Double.parseDouble(plan.getCompletes()) >= 50)) {
					flag = false;
				} else if (status.equals(2) && (Double.parseDouble(plan.getCompletes()) < 50
						|| Double.parseDouble(plan.getCompletes()) >= 75)) {
					flag = false;
				} else if (status.equals(3) && Double.parseDouble(plan.getCompletes()) < 75) {
					flag = false;
				}
			}

			if (usernames.size() > 0 && !usernames.contains(plan.getAssignedTo())) {
				flag = false;
			}

			if (flag)
				result.add(plan);
		}

		return result;
	}

	public ProjectPlan insert(ProjectPlan entity) {
		// TODO auto-generated method stub
		DaoFactory.create(ProjectPlan.class).insert(entity);
		return entity;
	}

	public ProjectPlan get(@PathParam(value = "id") java.lang.Integer id) {
		// TODO auto-generated method stub
		return DaoFactory.create(ProjectPlan.class).selectByID(id);
	}

	public void deleteBatch(@QueryParam(value = "id") List<java.lang.Integer> ids) {
		// TODO auto-generated method stub
		DaoFactory.create(ProjectPlan.class).deleteBatch(ids);
	}

	public void delete(@PathParam(value = "id") java.lang.Integer id) {
		// TODO auto-generated method stub
		ProjectPlan entity = new ProjectPlan();
		entity.setId(id);
		DaoFactory.create(ProjectPlan.class).delete(entity);
	}

	public Page<ProjectPlan> getListByPage(@QueryParam(value = "page") int page, @QueryParam(value = "rows") int rows,
			@QueryParam(value = "sidx") String sidx, @QueryParam(value = "sord") String sord,
			@QueryParam(value = "cond") String cond) {
		// TODO auto-generated method stub
		// 创建分页对象
		Pageable pageable = new Pageable(page, rows);
		// 创建排序对象
		Sortable sortable = SortableUtil.getSortable(sidx, sord);
		/* 创建高级查询对象 */
		Conditions conditions = ConditionsUtil.getConditions(cond, "filters");
		/* 初始化实体对象 */
		ProjectPlan entity = StringUtils.isNotBlank(cond) ? JSON.parseObject(cond, ProjectPlan.class)
				: new ProjectPlan();

		return DaoFactory.create(ProjectPlan.class).selectPageByCondition(entity, conditions, pageable, sortable, true);

	}

	public List<ProjectPlan> getTreeNodes(ProjectPlan plan, List<ProjectPlan> list, List<ProjectPlan> childList) {
		if (list.size() > 0) {
			for (ProjectPlan mod : list) {
				if (mod.getParent().equals(plan.getId())) {
					childList.add(mod);
					getTreeNodes(mod, list, childList);
				}
			}
		}
		return childList;
	}
}