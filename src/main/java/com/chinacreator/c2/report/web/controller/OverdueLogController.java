package com.chinacreator.c2.report.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.chinacreator.c2.config.ConfigManager;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.erp.ErpProject;
import com.chinacreator.c2.report.tools.ExcelGenerator;
import com.chinacreator.c2.report.workLog.WorkLog;
import com.chinacreator.c2.uop.OrgUserConstant;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.Organization;
import com.chinacreator.c2.web.controller.ResponseFactory;
import com.chinacreator.c2.workbench.TeamGroupService;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("report/overdueLog")
public class OverdueLogController {
	
	private static Map<String, Set<String>> workLogCols;
	private static Set<String> workLackCols;

	private static Dao<ErpProject> erpDao = DaoFactory.create(ErpProject.class);

	private static Dao<WorkLog> workLogDao = DaoFactory.create(WorkLog.class);

	private List<String> isERPProject = null;

	static {
		workLogCols = new HashMap<String, Set<String>>();
		Set<String> cols = new LinkedHashSet<String>();
		cols.add("日志ID");
		cols.add("机构ID");
		cols.add("机构名");
		cols.add("合同编号");
		cols.add("合同名称");
		cols.add("用户工号");
		cols.add("用户名");
		cols.add("工作时长");
		cols.add("工作日期");
		cols.add("实际填写日期");
		cols.add("逾期天数");
		cols.add("工作内容");
		Set<String> errorCols = new LinkedHashSet<String>();
		errorCols.add("日志ID");
		errorCols.add("机构ID");
		errorCols.add("机构名");
		errorCols.add("合同编号");
		errorCols.add("合同名称");
		errorCols.add("用户工号");
		errorCols.add("用户名");
		errorCols.add("工作时长");
		errorCols.add("工作日期");
		errorCols.add("实际填写日期");
		errorCols.add("逾期天数");
		errorCols.add("工作内容");
		errorCols.add("错误信息");
		workLogCols.put("错误日志", errorCols);
		workLogCols.put("正确日志", cols);

		workLackCols = new LinkedHashSet<String>();
		workLackCols.add("缺失工时");
		workLackCols.add("成员");
		workLackCols.add("缺失日期");

	}

	@RequestMapping(value = "/{start}/{end}/{deptId}", method = RequestMethod.GET)
	public Object generateWorkLogFile(@PathVariable Long start, @PathVariable Long end, @PathVariable String deptId,
			HttpServletResponse response) throws IOException {

		try {
			// 获取全部机构信息
			Map<String, Organization> deptsMap = new HashMap<String, Organization>();
			List<Organization> orgs = OrgUserService.queryChildOrgs(ConfigManager.getInstance().getConfig("c2_org_pid"),
					true);
			for (Organization o : orgs) {
				deptsMap.put(o.getId(), o);
			}
			Map<String, Object> conditions = new HashMap<String, Object>();
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> errorResult = new ArrayList<Map<String, Object>>();
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

			// 过滤实习人员的日志
			for (int i = 0; i < members.size(); i++) {
				if (members.get(i).get("workno") != null
						&& (members.get(i).get("workno").toString().toUpperCase().contains("SX"))) {
					members.remove(i);
					i--;
				}
			}

			if (members.size() >= 1000) {
				int form = 0, to = 1000;
                while (form < members.size()) {
                    List<Map<String, Object>> subList = Lists.newArrayList();
                        
                    for(int i=form;i<Math.min(members.size(), to);i++){
                        subList.add(members.get(i));
                    }
                    Map<String, List<Map<String, Object>>> logs = getWorkLogs(conditions, subList, deptsMap);
                    result.addAll(logs.get("正确日志"));
                    errorResult.addAll(logs.get("错误日志"));
                    
                    form = to;
                    to += 1000;
                }
			} else {
				Map<String, List<Map<String, Object>>> logs = getWorkLogs(conditions, members, deptsMap);
				result.addAll(logs.get("正确日志"));
				errorResult.addAll(logs.get("错误日志"));
			}
			results.put("正确日志", result);
			results.put("错误日志", errorResult);

			response.setHeader("Content-Disposition",
					"attachment;filename=OverdueLog" + startDate + "-" + endDate + ".xls");
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

	@SuppressWarnings("unchecked")
	private Map<String, List<Map<String, Object>>> getWorkLogs(Map<String, Object> conditions,
			List<Map<String, Object>> members, Map<String, Organization> deptsMap) {

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> errorResult = new ArrayList<Map<String, Object>>();

		Map<String, List<Organization>> userCache = new HashMap<String, List<Organization>>();// 机构信息
		Map<String, Map<String, Object>> memberCache = new HashMap<String, Map<String, Object>>();
		conditions.put("members", members);
		for (Map<String, Object> userDTO : members) {
			memberCache.put(userDTO.get(OrgUserConstant.USER_NAME).toString(), userDTO);
			List<String> orgIds = (List<String>) userDTO.get("orgIds");
			List<Organization> org = Lists.newArrayList();
			for (String orgId : orgIds) {
				org.add(deptsMap.get(orgId));
			}
			userCache.put(userDTO.get(OrgUserConstant.USER_NAME).toString(), org);
		}
		Map<String, List<Map<String, Object>>> results = new LinkedHashMap<String, List<Map<String, Object>>>();
		List<Map<String, Object>> logs = workLogDao.getSession().selectList("selectOverdueLogs4ERP", conditions);
		isERPProject = new ArrayList<String>();
		
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
				worklog.put("逾期天数", getTimeDistance((Date)worklog.get("工作日期"),new Date(((Timestamp)worklog.get("实际填写日期")).getTime())));
				worklog.remove("USER_NAME");

				if (logCompletelyCheck(worklog)) {// 工号为空或者项目编号无法匹配的放入错误日志
					result.add(worklog);
				} else {
					errorResult.add(worklog);
				}
				// 又要验证编号正确性，又要导出整个编号
				if (worklog.containsKey("PROJECT_CODE"))
					worklog.remove("PROJECT_CODE");
			} catch (Exception e) {
			  e.printStackTrace();
				continue;
			}
		}

		results.put("正确日志", result);
		results.put("错误日志", errorResult);
		return results;
	}
	

  private int getTimeDistance(Date beginDate , Date endDate ) {
      Calendar beginCalendar = Calendar.getInstance();
      beginCalendar.setTime(beginDate);
      Calendar endCalendar = Calendar.getInstance();
      endCalendar.setTime(endDate);
      long beginTime = beginCalendar.getTime().getTime();
      long endTime = endCalendar.getTime().getTime();
      int betweenDays = (int)((endTime - beginTime) / (1000 * 60 * 60 *24));//先算出两时间的毫秒数之差大于一天的天数
      
      endCalendar.add(Calendar.DAY_OF_MONTH, -betweenDays);//使endCalendar减去这些天数，将问题转换为两时间的毫秒数之差不足一天的情况
      endCalendar.add(Calendar.DAY_OF_MONTH, -1);//再使endCalendar减去1天
      if(beginCalendar.get(Calendar.DAY_OF_MONTH)==endCalendar.get(Calendar.DAY_OF_MONTH))//比较两日期的DAY_OF_MONTH是否相等
          return betweenDays + 1; //相等说明确实跨天了
      else
          return betweenDays + 0; //不相等说明确实未跨天
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

	private boolean logCompletelyCheck(Map<String, Object> worklog) {

		if (String.valueOf(worklog.get("工作时长")).equals("0.0")) {
			worklog.put("错误信息", "工时为0");
			return false;
		} else if (worklog.get("用户工号") == null) {
			worklog.put("错误信息", "工号不存在");
			return false;
		} else if (worklog.get("工作内容") == null) {
			worklog.put("错误信息", "工作内容为空");
			return false;
		} else if (isERPProject.contains(worklog.get("合同编号"))) {
			return true;
		} else {
			int count = erpDao.getSession().selectOne("selectByCode", worklog.get("合同编号"));
			if (count == 0) {
				worklog.put("错误信息", "编号无法匹配");
				return false;
			} else {
				isERPProject.add((String) worklog.get("合同编号"));
				return true;
			}
		}
	}

}