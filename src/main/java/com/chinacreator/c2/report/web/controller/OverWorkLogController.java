package com.chinacreator.c2.report.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.chinacreator.c2.config.ConfigManager;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.project.task.WeekDay;
import com.chinacreator.c2.report.tools.ExcelGenerator;
import com.chinacreator.c2.report.workLog.WorkLog;
import com.chinacreator.c2.uop.OrgUserConstant;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.Organization;
import com.chinacreator.c2.web.controller.ResponseFactory;
import com.chinacreator.c2.workbench.TeamGroupService;

@Controller
@RequestMapping("report/overworkLog")
@Consumes(MediaType.APPLICATION_JSON)
public class OverWorkLogController {

	private static Map<String, Set<String>> workLogCols;

	private static Dao<WorkLog> workLogDao = DaoFactory.create(WorkLog.class);

	static {
		workLogCols = new HashMap<String, Set<String>>();
		Set<String> cols = new LinkedHashSet<String>();
		cols.add("机构ID");
		cols.add("机构名");
		cols.add("用户工号");
		cols.add("用户名");
		cols.add("加班时长");
		cols.add("加班日期");
		workLogCols.put("正确日志", cols);

	}

	@RequestMapping(value = "/{start}/{end}/{deptId}", method = RequestMethod.GET)
	public Object generateWorkLogFile(@PathVariable Long start, @PathVariable Long end, @PathVariable String deptId,
			HttpServletResponse response) throws IOException {

		try {

			Map<String, Object> conditions = new HashMap<String, Object>();
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			Map<String, List<Map<String, Object>>> results = new LinkedHashMap<String, List<Map<String, Object>>>();

			String startDate = "";
			String endDate = "";
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

			conditions.put("start", new Date(start));
			startDate = df.format(new Date(start));

			conditions.put("end", new Date(end));
			endDate = df.format(new Date(end));

			List<Map<String, Object>> members = new TeamGroupService().getTeamGroupUsers(deptId, new Date(start),
					new Date(end));

			if (members.size() >= 1000) {
				int i = (members.size() / 1000);
				if (members.size() % 1000 != 0)
					i++;

				for (; i > 0; i--) {
					List<Map<String, Object>> subList = members.subList((i - 1) * 1000, 1000 * i);
					if (subList.size() == 0)
						continue;
					Map<String, List<Map<String, Object>>> logs = getWorkLogs(conditions, subList);
					result.addAll(logs.get("正确日志"));
				}
			} else {
				Map<String, List<Map<String, Object>>> logs = getWorkLogs(conditions, members);
				result.addAll(logs.get("正确日志"));
			}
			results.put("正确日志", result);

			response.setHeader("Content-Disposition",
					"attachment;filename=OverWorkLog" + startDate + "-" + endDate + ".xls");
			response.setContentType("application/msexcel");

			OutputStream output = response.getOutputStream();

			HSSFWorkbook ws = ExcelGenerator.doGenerate(results, workLogCols);
			if (ws == null) {
				return null;
			}
			ws.write(output);
			output.close();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseFactory().createResponseBodyHtml(
					"<script type=\"text/javascript\">alert('" + e.getMessage() + "');</script>");
		}
	}

	private Map<String, List<Map<String, Object>>> getWorkLogs(Map<String, Object> conditions,
			List<Map<String, Object>> members) {

		// 获取全部机构信息
		Map<String, Organization> deptsMap = new HashMap<String, Organization>();
		List<Organization> orgs = OrgUserService.queryChildOrgs(ConfigManager.getInstance().getConfig("c2_org_pid"),
				true);
		for (Organization o : orgs) {
			deptsMap.put(o.getId(), o);
		}

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		Map<String, List<Organization>> userCache = new HashMap<String, List<Organization>>();// 机构信息
		Map<String, Map<String, Object>> memberCache = new HashMap<String, Map<String, Object>>();
		conditions.put("members", members);
		for (Map<String, Object> userDTO : members) {
			memberCache.put(userDTO.get(OrgUserConstant.USER_NAME).toString(), userDTO);
			List<Organization> org = OrgUserService.queryOrgByEmail(userDTO.get("email").toString());
			userCache.put(userDTO.get(OrgUserConstant.USER_NAME).toString(), org);
		}

		List<Map<String, Object>> logs = workLogDao.getSession().selectList("selectOverWorkLogs4ERP", conditions);
		// 增加工作量为null但存在工时记录的情况
		addWeekDayNull(logs, conditions);
		for (Map<String, Object> worklog : logs) {
			try {
				String userName = (String) worklog.get("USER_NAME");
				List<Organization> temp = userCache.get(userName);
				Map<String, Object> user = memberCache.get(userName);

				for (Organization o : temp) {
					if (o.get("outId") != null && !o.get("outId").toString().equals("")) {
						String outId = o.get("outId").toString();// erp系统机构id
						if (o.containsKey("type") && o.get("type").equals("4")) {// 在事业部下
							worklog.put("机构ID", outId.substring(4, outId.length()));// 截除imp_
							worklog.put("机构名", o.getName());
						} else {// 在分组下
								// 递归获取事业部信息
							getParentOrgInfo(worklog, o, deptsMap);
						}
						break;
					}
				}
				worklog.put("用户工号", user.get("workno"));
				worklog.put("用户名", worklog.get("USER_REALNAME"));
				worklog.remove("USER_NAME");

				result.add(worklog);
			} catch (Exception e) {
				continue;
			}
		}
		Map<String, List<Map<String, Object>>> results = new HashMap<String, List<Map<String, Object>>>();
		results.put("正确日志", result);

		return results;
	}

	private void getParentOrgInfo(Map<String, Object> worklog, Organization o, Map<String, Organization> deptsMap) {
		Organization temp = deptsMap.get(deptsMap.get(o.getId()).getPid());
		String outId = temp.get("outId").toString();// erp系统机构id
		if (temp.containsKey("type") && temp.get("type").equals("4")) {
			worklog.put("机构ID", outId.substring(4, outId.length()));// 截除imp_
			worklog.put("机构名", temp.getName());
		} else {
			getParentOrgInfo(worklog, temp, deptsMap);
		}
	}

	private void addWeekDayNull(List<Map<String, Object>> logs, Map<String, Object> conditions) {
		List<Map<String, Object>> list = workLogDao.getSession().selectList("weekDayNullWorkLogs", conditions);
		for (Map<String, Object> m : list) {
			Dao<WeekDay> dao = DaoFactory.create(WeekDay.class);
			WeekDay w = new WeekDay();
			w.setWorkdate((Date) m.get("加班日期"));
			w = dao.selectOne(w);
			if (w == null) {// 工作日内无记录表示加班
				logs.add(m);
			}
		}
	}

}