package com.chinacreator.c2.report.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.alibaba.fastjson.JSON;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.report.tools.ExcelGenerator;
import com.chinacreator.c2.report.workLog.WorkLog;
import com.chinacreator.c2.web.controller.ResponseFactory;
import com.chinacreator.c2.workbench.TeamGroupService;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("report/projecttask")
public class ProjectTaskController {

  private static Map<String, Set<String>> workLogCols;

  private static Dao<WorkLog> workLogDao = DaoFactory.create(WorkLog.class);

  static {
    workLogCols = new HashMap<String, Set<String>>();
    Set<String> cols = new LinkedHashSet<String>();
    cols.add("项目名称");
    cols.add("模块");
    cols.add("标签");
    cols.add("任务名称");
    cols.add("里程碑");
    cols.add("项目计划");
    cols.add("任务创建人");
    cols.add("计划截止时间");
    cols.add("任务执行人");
    cols.add("任务开始时间");
    cols.add("任务预计完成时间");
    cols.add("任务结束时间");
    cols.add("任务实际开始时间");
    cols.add("任务实际结束时间");
    cols.add("预估工时");
    cols.add("实际工时");
    workLogCols.put("Sheet1", cols);

  }

  @RequestMapping(value = "/{start}/{end}/{deptId}/{projectIds:.+}", method = RequestMethod.GET)
  public Object generateWorkLogFile(@PathVariable Long start, @PathVariable Long end,
      @PathVariable String deptId, @PathVariable String projectIds, HttpServletResponse response)
      throws IOException {

    try {
      List<String> projects = JSON.parseArray(projectIds, String.class);// 选择的项目id集合
      Map<String, Object> conditions = new HashMap<String, Object>();
      List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
      Map<String, List<Map<String, Object>>> results =
          new LinkedHashMap<String, List<Map<String, Object>>>();

      String startDate = "";
      String endDate = "";
      SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

      conditions.put("start", new Date(start));
      startDate = df.format(new Date(start));

      conditions.put("end", new Date(end));
      endDate = df.format(new Date(end));
      
      conditions.put("projects", projects);

      List<Map<String, Object>> members =
          new TeamGroupService().getTeamGroupUsers(deptId, new Date(start), new Date(end));

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

          for (int i = form; i < Math.min(members.size(), to); i++) {
            subList.add(members.get(i));
          }
          Map<String, List<Map<String, Object>>> logs = getWorkLogs(conditions, subList);
          result.addAll(logs.get("Sheet1"));

          form = to;
          to += 1000;
        }

      } else {
        Map<String, List<Map<String, Object>>> logs = getWorkLogs(conditions, members);
        result.addAll(logs.get("Sheet1"));
      }
      results.put("Sheet1", result);

      response.setHeader("Content-Disposition",
          "attachment;filename="+URLEncoder.encode("项目任务" + startDate + "-" + endDate + ".xls", "UTF-8"));
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

    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

    conditions.put("members", members);
    Map<String, List<Map<String, Object>>> results = new LinkedHashMap<String, List<Map<String, Object>>>();
    List<Map<String, Object>> logs = workLogDao.getSession().selectList("selectProjectTasks", conditions);

    for (Map<String, Object> worklog : logs) {
      try {
        List<Map<String,Object>> labels = (List<Map<String,Object>>) worklog.get("labels");
        if(labels.size()>0){
          String label = "";
          for(Map<String,Object> l:labels){
            label += l.get("name")+",";
          }
          worklog.put("标签", label.substring(0, label.length()-1));
        }
        List<Map<String,Object>> times = (List<Map<String,Object>>)worklog.get("times");
        if(times.size()>0){
          worklog.put("任务实际开始时间", times.get(0).get("firstTime"));
          if(worklog.get("任务结束时间")!=null)
            worklog.put("任务实际结束时间", times.get(0).get("lastTime")); 
        }
        result.add(worklog);

      } catch (Exception e) {
        continue;
      }
    }

    results.put("Sheet1", result);
    return results;
  }

}
