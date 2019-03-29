package com.chinacreator.c2.project.info;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.DaoService;
import com.chinacreator.c2.project.milestone.Milestone;
import com.chinacreator.c2.project.story.StoryService;
import com.chinacreator.c2.project.weeklyReport.TaskSnapshotEx;
import com.google.common.collect.Lists;

/**
 * 模块服务
 * 
 * @author tbw
 * 
 */
@Service
public class ModuleService {
	DaoService daoService = new DaoService();

	public List<Module> getModuleByprojectId(Integer projectId, Integer parent) {
		Dao<Module> modDao = DaoFactory.create(Module.class);
		Module module = new Module();
		Project project = new Project();
		project.setId(projectId);
		module.setProjectId(project);
		module.setParent(parent);
		List<Module> modList = modDao.select(module);
		return modList;
	}

	@SuppressWarnings("unchecked")
	public List<Module> getModuleByProject(Integer projectId, Integer parent, Map<String, Object> search) {
		Dao<Module> modDao = DaoFactory.create(Module.class);
		Module module = new Module();
		Project project = new Project();
		project.setId(projectId);
		module.setProjectId(project);
		module.setParent(parent);
		List<Module> modList = modDao.select(module);
		// if (!CollectionUtils.isEmpty(modList)) {
		// modList = selectRecursive(modList);
		// }

		StoryService ss = new StoryService();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> p = new HashMap<>();
		p.put("id", projectId.toString());
		map.put("project", p);
		List<Map<String, Object>> storyList = ss.getStoryListByProject(map);
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(2);
		// 将需求放入对应的模块里面
		for (Module mods : modList) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> s : storyList) {
				if (null != s.get("module")) {
					Module m = (Module) s.get("module");
					// 找到对应的模块
					if (m.getId().equals(mods.getId())) {
						s.put("parent", m.getId());
						list.add(s);
					}
				}
			}
			mods.setStoryList(list);
		}
		for (Module mods : modList) {
			// 循环遍历找出所有的子数据
			List<Module> childList = getTreeNodes(mods, modList, new ArrayList<Module>());
			childList.add(mods);
			// 然后循环所有子模块 算出当前模块的完成度
			int done = 0;
			int undone = 0;
			int storyNum = 0;
			for (Module mod : childList) {
				if (null != mod.getStoryList()) {
					storyNum += mod.getStoryList().size();
					if (mod.getStoryList().size() > 0) {
						for (Map<String, Object> story : mod.getStoryList()) {
							if (null != story.get("taskInfo")) {
								for (Entry<String, Integer> entry : ((Map<String, Integer>) story.get("taskInfo"))
										.entrySet()) {
									if (entry.getKey().equals("doneNum")) {
										done += Integer.parseInt(String.valueOf(entry.getValue()));
									} else if (entry.getKey().equals("undoneNum")) {
										undone += Integer.parseInt(String.valueOf(entry.getValue()));
									}
								}
							}
						}
					}
				}
			}

			mods.setTaskNum(done + undone);
			mods.setStoryNum(storyNum);

		}
		if (null != search)
			return selectStory(modList, search);
		else
			return modList;
	}

	/*
	 * 
	 * 过滤需求模块
	 */
	public List<Module> selectStory(List<Module> modList, Map<String, Object> search) {
		// List<Module> result = new ArrayList<Module>();
		String status = null;
		if (null != search.get("status")) {
			Map<String, String> storyStatus = (Map<String, String>) search.get("status");
			status = storyStatus.get("id");
		}

		String title = null;
		if (null != search.get("title")) {
			title = search.get("title").toString();
		}

		String mileStoneId = null;
		if (null != search.get("milestone")) {
			Map<String, String> milestone = (Map<String, String>) search.get("milestone");
			mileStoneId = String.valueOf(milestone.get("id"));
		}
		String pri = null;
		if (null != search.get("pri")) {
			Map<String, String> pris = (Map<String, String>) search.get("pri");
			pri = pris.get("id");
		}

		List<String> userIds = Lists.newArrayList();
		if (null != search.get("assignedToList")) {
			List<Map<String, Object>> userList = (List<Map<String, Object>>) search.get("assignedToList");
			for (Map<String, Object> map : userList) {
				if (null != map.get("userId")) {
					userIds.add(map.get("userId").toString());
				}
			}
		}

		String completes = null;
		if (null != search.get("completes")) {
			Map<String, String> storyCompletes = (Map<String, String>) search.get("completes");
			completes = String.valueOf(storyCompletes.get("value"));
		}

		for (Module m : modList) {
			if (m.getStoryList().size() > 0) {
				List<Map<String, Object>> storys = new ArrayList<>();
				for (Map<String, Object> s : m.getStoryList()) {
					Boolean flag = true;
					if (null != status && !s.get("status").toString().equals(status)) {
						flag = false;
					}
					if (null != title && "" != title && !s.get("title").toString().contains(title))
						flag = false;
					if (null != mileStoneId) {
						if (null != s.get("milestone")) {
							Milestone mileston = (Milestone) s.get("milestone");
							if (!mileston.getId().toString().equals(mileStoneId)) {
								flag = false;
							}
						} else {
							flag = false;
						}
					}
					if (null != pri && !pri.equals(s.get("pri").toString())) {
						flag = false;
					}

					if (null != completes && !s.get("completes").equals(completes)) {
						flag = false;
					}
					if (!userIds.isEmpty()) {
						if (null != s.get("assignedTo")) {
							Map<String, String> assignedTo = (Map<String, String>) s.get("assignedTo");
							if (!userIds.contains(assignedTo.get("userId"))) {
								flag = false;
							}
						} else {
							flag = false;
						}
					}
					if (flag)
						storys.add(s);
				}
				m.setStoryList(storys);
			}
		}
		return modList;
	}

	/**
	 * 周报模块树
	 * 
	 * @param projectId
	 * @param reportId
	 * @return
	 */
	public List<Module> getWeekReportModule(Integer projectId, Integer parent, Integer reportId) {
		Dao<Module> modDao = DaoFactory.create(Module.class);
		Module module = new Module();
		Project project = new Project();
		project.setId(projectId);
		module.setProjectId(project);
		module.setParent(parent);
		List<Module> modList = modDao.select(module);

		// 周报下的任务
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("belongToNextWeek", "false");
		condition.put("reportId", reportId);
		List<TaskSnapshotEx> taskSnapshots = DaoFactory.create(TaskSnapshotEx.class).getSession()
				.selectList("selectWeeklyReportTaskSnapshots", condition);

		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(2);
		// 将任务放入对应的模块里面
		for (Module mods : modList) {
			List<TaskSnapshotEx> list = new ArrayList<TaskSnapshotEx>();
			Map<Integer, Integer> srotyIdMap = new HashMap<Integer, Integer>();
			for (TaskSnapshotEx tex : taskSnapshots) {
				if (mods.getId().equals(tex.getModuleId())) {
					list.add(tex);
					if (!srotyIdMap.containsKey(tex.getStoryId())) {
						srotyIdMap.put(tex.getStoryId(), tex.getStoryId());
					}
				}
			}
			mods.setStoryNum(srotyIdMap.keySet().size());
			mods.setTaskNum(list.size());
		}

		// 通过任务数查找关联的需求数
		for (Module mods : modList) {
			// 循环遍历找出所有的子数据
			List<Module> childList = getTreeNodes(mods, modList, new ArrayList<Module>());
			childList.add(mods);
			// 然后循环所有子模块 算出需求数
			int storyNum = 0;
			int taskNum = 0;
			for (Module mod : childList) {
				storyNum += mod.getStoryNum();
				taskNum += mod.getTaskNum();
			}
			mods.setStoryNum(storyNum);
			mods.setTaskNum(taskNum);
		}
		return modList;
	}

	public List<Module> getTreeNodes(Module module, List<Module> list, List<Module> childList) {
		if (list.size() > 0) {
			for (Module mod : list) {
				if (mod.getParent().equals(module.getId())) {
					childList.add(mod);
					getTreeNodes(mod, list, childList);
				}
			}
		}
		return childList;
	}

	private List selectRecursive(Collection modList) {
		List rList = daoService.selectListCascade(new ArrayList(modList));
		if (!CollectionUtils.isEmpty(rList)) {
			for (Object object : rList) {
				Module module2 = (Module) object;
				Collection<Module> modules = module2.getModules();

				if (!CollectionUtils.isEmpty(modules)) {
					module2.setModules(selectRecursive(modules));
				}
			}
		}
		return rList;
	}

	public Module saveModule(Module module) {
		Dao<Module> modDao = DaoFactory.create(Module.class);
		if (module.getId() != null) {
			modDao.update(module);
		} else {
			module.setOrder(getMaxOrder(module.getParent(), module.getProjectId().getId().toString()));
			modDao.insert(module);
			module.setModules(new ArrayList<Module>());
		}

		return module;
	}

	// 模块序号更改
	public List<Module> orderModule(List<Module> list) {
		List<Integer> orderList = new ArrayList<Integer>();
		if (list.size() > 0) {
			for (Module module : list) {
				orderList.add(module.getOrder());
			}
		}
		Object[] orders = orderList.toArray();
		Arrays.sort(orders);
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setOrder(Integer.parseInt(orders[i].toString()));
		}
		DaoFactory.create(Module.class).updateBatch(list);
		return list;
	}

	public List<Module> saveModule(List<Module> list) {
		Dao<Module> modDao = DaoFactory.create(Module.class);
		if (!CollectionUtils.isEmpty(list)) {
			for (Module module : list) {
				saveModule(module);
			}
		}
		return list;
	}

	@Transactional
	public void delModuleById(Module module) {
		Dao<Module> dao = DaoFactory.create(Module.class);
		if (!CollectionUtils.isEmpty(module.getModules())) {
			for (Module module2 : module.getModules()) {
				delModuleById(module2);
			}
		}
		dao.delete(module);
	}

	public Module saveModule(int parent, String projectId) {
		Dao<Module> dao = DaoFactory.create(Module.class);
		Module m = new Module();
		m.setName("新增模块");
		m.setParent(parent);
		m.setProjectId(DaoFactory.create(Project.class).selectByID(projectId));
		m.setOrder(getMaxOrder(parent, projectId));
		dao.insert(m);
		return m;
	}

	public void updateModuleName(int id, String name) {
		Dao<Module> dao = DaoFactory.create(Module.class);
		Module m = dao.selectByID(id);
		m.setName(name);
		dao.update(m);
	}

	private Integer getMaxOrder(int parent, String projectId) {
		Dao<Module> dao = DaoFactory.create(Module.class);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("parent", parent);
		param.put("projectId", projectId);
		Module map = dao.getSession().selectOne("com.chinacreator.c2.project.info.ModuleMapper.getMaxOrder", param);
		return map != null ? map.getOrder() + 1 : 1;
	}

	/**
	 * 
	 * 查询模块详情 projectId moduleId
	 */
	public Module getModuleById(String projectId, String moduleId) {
		Dao<Module> dao = DaoFactory.create(Module.class);
		return dao.selectByID(moduleId);

	}

	/**
	 * 
	 * 批量修改模块排序号
	 */
	public List<Module> orderAllModule() {
		Module mm = new Module();
		mm.setC2LogicdeleteFlag(false);
		List<Module> list = DaoFactory.create(Module.class).select(mm);
		if (list.size() > 0) {
			List<Module> rootModule = new ArrayList<>();
			int j = 0;
			for (Module module : list) {
				// 要 将根模块单独处理下
				if (module.getParent().equals(0)) {
					rootModule.add(module);
				} else {
					int i = 0;
					Module m = new Module();
					m.setParent(module.getId());
					m.setProjectId(module.getProjectId());
					List<Module> modList = DaoFactory.create(Module.class).select(m);
					for (Module module2 : modList) {
						module2.setOrder(i);
						i++;
					}
					DaoFactory.create(Module.class).updateBatch(modList);
				}
			}
			for (Module module : rootModule) {
				module.setOrder(j);
				j++;
			}
			DaoFactory.create(Module.class).updateBatch(rootModule);
		}
		return list;
	}
}
