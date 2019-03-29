package com.chinacreator.c2.project.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chinacreator.c2.config.ConfigManager;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.Organization;
import com.chinacreator.c2.uop.User;
import com.google.common.collect.Lists;

public class ProjectOrganizationService {

	/**
	 * 统计各部门创建的项目数据
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getProjectsByOrganization(int year) {
		List<Map<String, Object>> result = Lists.newArrayList();
		Dao<Project> dao = DaoFactory.create(Project.class);

		// 当年所有的进行中项目 过滤掉没有填写日志的项目
		Map<String, String> params = new HashMap<String, String>();
		params.put("st", year + "-01-01");
		params.put("et", (year + 1) + "-01-01");
		List<Project> projects = dao.getSession().selectList("getProjectsByOrganization", params);
		Map<String, List<Project>> userProjectCount = new HashMap<String, List<Project>>();// 用户所创建的项目集合
		List<String> userIds = Lists.newArrayList();
		String tempKey = "";
		List<Project> tempList = Lists.newArrayList();
		for (Project p : projects) {
			if (tempKey.equals("") || !tempKey.equals(p.getOwner())) {
				tempKey = p.getOwner();
				userIds.add(p.getOwner());
				tempList = new ArrayList<Project>();
				userProjectCount.put(tempKey, tempList);
			}
			tempList.add(p);
		}

		// 获取全部机构信息
		Map<String, Organization> deptsMap = new HashMap<String, Organization>();
		List<Organization> orgs = OrgUserService.queryChildOrgs(ConfigManager.getInstance().getConfig("c2_org_pid"),
				true);
		for (Organization o : orgs) {
			deptsMap.put(o.getId(), o);
		}

		// 根据用户查询所在部门
		List<Map<String, Object>> users = DaoFactory.create(Object.class).getSession()
				.selectList("com.chinacreator.c2.project.info.MemberMapper.queryUserByIds", userIds);
		List<String> userNames = Lists.newArrayList();
		for (Map<String, Object> u : users) {
			userNames.add(u.get("user_name").toString());
		}
		List<User> userList = OrgUserService.queryOrgUsers(ConfigManager.getInstance().getConfig("c2_org_pid"),"1", true);// 行政机构下的所有用户
		for (User user : userList) {
			for (Map<String, Object> u : users) {
				if (user.getEmail().contains(u.get("user_name").toString())) {// 用户存入机构信息
					List<String> orgIds = (List<String>) user.get("orgIds");
					List<Organization> org = Lists.newArrayList();
					for (String orgId : orgIds) {
						org.add(deptsMap.get(orgId));
					}
					u.put("orgs", org);
				}
			}
		}
		Map<String, Map<String, Object>> tempCount = new HashMap<String, Map<String, Object>>();
		// 根据机构信息计算出事业部门总和
		for (Map<String, Object> u : users) {
			List<Organization> temp = (List<Organization>) u.get("orgs");
			// System.out.println(temp.get(0).get("name")+"-----"+u.get("user_realname")+"-----"+userProjectCount.get(u.get("user_id")).size());
			for (Organization o : temp) {
				Map<String, Object> tempObject = new HashMap<String, Object>();
				if (o.get("outId") != null && !o.get("outId").toString().equals("")) {
					if (o.containsKey("type") && o.get("type").equals("4")) {// 在事业部下
						if (tempCount.containsKey(o.getId())) {
							if (null != tempCount.get(o.getId())) {
								tempObject = tempCount.get(o.getId());
							}

							int count = (int) tempCount.get(o.getId()).get("total")
									+ userProjectCount.get(u.get("user_id")).size();

							List<Project> list = (List<Project>) tempObject.get("projectList");
							tempObject.put("total", count);
							list.addAll(userProjectCount.get(u.get("user_id")));
							tempObject.put("projectList", list);
							tempCount.put(o.getId(), tempObject);
						} else {
							tempObject.put("total", userProjectCount.get(u.get("user_id")).size());
							tempObject.put("projectList", userProjectCount.get(u.get("user_id")));
							tempCount.put(o.getId(), tempObject);
						}
					} else {// 在分组下
							// 递归获取事业部信息
						getParentOrgInfo(u, o, deptsMap, userProjectCount, tempCount);
					}
					break;
				}
			}
		}

		for (String key : tempCount.keySet()) {
			Map<String, Object> map = deptsMap.get(key);
			map.put("count", tempCount.get(key));
			// System.out.println(map.get("name")+"----"+tempCount.get(key));
			result.add(map);
		}

		return result;
	}

	private void getParentOrgInfo(Map<String, Object> user, Organization o, Map<String, Organization> deptsMap,
			Map<String, List<Project>> userProjectCount, Map<String, Map<String, Object>> tempCount) {
		Organization temp = deptsMap.get(deptsMap.get(o.getId()).getPid());
		Map<String, Object> tempObject = new HashMap<String, Object>();
		if (null != tempCount.get(temp.getId())) {
			tempObject = tempCount.get(temp.getId());
		}
		;
		if (temp.containsKey("type") && temp.get("type").equals("4")) {
			if (tempCount.containsKey(temp.getId())) {
				int count = (int) tempCount.get(temp.getId()).get("total")
						+ userProjectCount.get(user.get("user_id")).size();
				List<Project> list = (List<Project>) tempObject.get("projectList");
				tempObject.put("total", count);
				list.addAll(userProjectCount.get(user.get("user_id")));
				tempObject.put("projectList", list);
				tempCount.put(temp.getId(), tempObject);
			} else {
				tempObject.put("total", userProjectCount.get(user.get("user_id")).size());
				tempObject.put("projectList", userProjectCount.get(user.get("user_id")));
				tempCount.put(temp.getId(), tempObject);
			}
		} else {
			getParentOrgInfo(user, temp, deptsMap, userProjectCount, tempCount);
		}

	}
}
