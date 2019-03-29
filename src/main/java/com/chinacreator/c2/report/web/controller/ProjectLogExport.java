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

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.erp.ErpProject;
import com.chinacreator.c2.project.teamLog.ProjectLogService;
import com.chinacreator.c2.report.tools.ExcelGenerator;
import com.chinacreator.c2.report.workLog.WorkLog;
import com.chinacreator.c2.uop.OrgUserConstant;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.Organization;
import com.chinacreator.c2.uop.User;
import com.chinacreator.c2.web.controller.ResponseFactory;

@Controller
@RequestMapping("report")
public class ProjectLogExport {
    
        private static Map<String,Set<String>> workLogCols ;
        
        private static Map<String,Set<String>> projectWorkLogCols ;
    
        private static Dao<ErpProject> erpDao = DaoFactory.create(ErpProject.class) ;
        
        private static Dao<WorkLog> workLogDao = DaoFactory.create(WorkLog.class) ; 
        
        private List<String> isERPProject = null ;
        
        static {
            workLogCols = new HashMap<String,Set<String>>() ;
            Set<String> cols = new LinkedHashSet<String>() ;
            cols.add("日志ID") ;
            cols.add("机构ID") ;
            cols.add("机构名") ;
            cols.add("合同编号") ;
            cols.add("合同名称") ;
            cols.add("用户工号") ;
            cols.add("用户名") ;
            cols.add("工作时长") ;
            cols.add("日志日期") ;
            cols.add("工作内容") ;
            Set<String> errorCols = new LinkedHashSet<String>() ;
            errorCols.add("日志ID") ;
            errorCols.add("机构ID") ;
            errorCols.add("机构名") ;
            errorCols.add("合同编号") ;
            errorCols.add("合同名称") ;
            errorCols.add("用户工号") ;
            errorCols.add("用户名") ;
            errorCols.add("工作时长") ;
            errorCols.add("日志日期") ;
            errorCols.add("工作内容") ;
            errorCols.add("错误信息") ;
            workLogCols.put("错误日志", errorCols) ;
            workLogCols.put("正确日志", cols) ;
            
            projectWorkLogCols = new HashMap<String,Set<String>>() ;
            Set<String> projectCols = new LinkedHashSet<String>() ;
            projectCols.add("项目名称") ;
            projectCols.add("任务名称") ;
            projectCols.add("任务创建人") ;
            projectCols.add("执行人") ;
            projectCols.add("执行时间") ;
            projectCols.add("执行日志") ;
            projectCols.add("执行工时数") ;
            projectWorkLogCols.put("项目日志", projectCols) ;
        }
        
        @RequestMapping(value = "/projectWorkLog/{start}/{end}/{deptId}", method = RequestMethod.GET)
        public Object generateProjectWorkLogFile(@PathVariable Long start,@PathVariable Long end,
            @PathVariable String deptId, HttpServletResponse response)
                   throws IOException { 
          List<User> users = OrgUserService.queryOrgUsers(deptId,"-1",true);
          Map<String,String> userNames2userRealnames = new HashMap<String,String>() ;
          for (User user : users) {
              if(!user.getEmail().contains("@")) continue ;
              String userName = user.getEmail().substring(0, user.getEmail().indexOf("@"));
              if(StringUtils.isNotBlank(userName)) userNames2userRealnames.put(userName,user.getName());
          }
          
          Map<String,Object> conditions = new HashMap<String,Object>() ;
          Map<String,List<Map<String, Object>>> results = new LinkedHashMap<String,List<Map<String,Object>>>() ;
          conditions.put("members",userNames2userRealnames.keySet());
          
          String startDate = "" ;
          String endDate = "" ;
          SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd") ;
          
          conditions.put("start",new Date(start)) ;
          startDate = df.format(new Date(start)) ;
          
          conditions.put("end",new Date(end)) ;
          endDate = df.format(new Date(end)) ;
          
          List<Map<String, Object>> logs = workLogDao.getSession().selectList("selectProjectWorkLogs", conditions) ;
          
          for (Map<String, Object> map : logs) {
            String creator = (String) map.get("任务创建人");
            String activator = (String) map.get("任务执行人");
            if(userNames2userRealnames.containsKey(creator)){
              map.put("任务创建人", userNames2userRealnames.get(creator)==null?creator:userNames2userRealnames.get(creator));
            }
            if(userNames2userRealnames.containsKey(activator)){
              map.put("任务执行人", userNames2userRealnames.get(activator)==null?creator:userNames2userRealnames.get(activator));
            }
          }
          
          results.put("工作日志", logs) ;
          
          response.setHeader("Content-Disposition","attachment;filename=ProjectLog"+startDate+"-"+endDate+".xls");
          response.setContentType("application/msexcel");
          
          OutputStream output = response.getOutputStream();
          HSSFWorkbook ws = ExcelGenerator.doGenerate(results,projectWorkLogCols) ;
          if(ws == null){
              return null ;
          }
          ws.write(output) ;
          output.close() ;
          
          return null;
        }
    
        @RequestMapping(value = "/projectLog/{start}/{end}/{projectId}/{deptId}", method = RequestMethod.GET)
         public Object generateWorkLogFile(@PathVariable Long start,@PathVariable Long end,@PathVariable String projectId,@PathVariable String deptId, HttpServletResponse response)
                    throws IOException {
                try{
                    Map<String,Object> conditions = new HashMap<String,Object>() ;
                    List<Map<String,Object>> result = new ArrayList<Map<String,Object>>() ;
                    List<Map<String,Object>> errorResult = new ArrayList<Map<String,Object>>() ;
                    Map<String,List<Map<String, Object>>> results = new LinkedHashMap<String,List<Map<String,Object>>>() ;
                    
                    String startDate = "" ;
                    String endDate = "" ;
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd") ;
                    
                    conditions.put("start",new Date(start)) ;
                    startDate = df.format(new Date(start)) ;
                    
                    conditions.put("end",new Date(end)) ;
                    endDate = df.format(new Date(end)) ;
                    
                    conditions.put("projectId", projectId);
                    
                    List<User> members = (List<User>) new ProjectLogService().getProjectUsers(projectId, new Date(start),new Date(end)).get("users");
                    if(!deptId.equals("0")){
                      for(int i=0;i<members.size();i++){
                        List<String> orgIds = (List<String>) members.get(i).get("orgIds");
                        if(!orgIds.contains(deptId)){
                          members.remove(i);
                          i--;
                        }
                      }
                    }
                    
                    if(members.size()>=1000){
                        int i = (members.size()/1000) ;
                        if(members.size()%1000!=0) i++ ;
                        
                        for(;i>0;i--){
                            List<User> subList = members.subList((i-1)*1000, 1000*i) ;
                            if(subList.size()==0) continue ;
                            Map<String, List<Map<String, Object>>> logs = getWorkLogs(conditions, subList);
                            result.addAll(logs.get("正确日志"));
                            errorResult.addAll(logs.get("错误日志")) ;
                        }
                    }else{
                        Map<String, List<Map<String, Object>>> logs = getWorkLogs(conditions, members);
                        result.addAll(logs.get("正确日志"));
                        errorResult.addAll(logs.get("错误日志")) ;
                    }
                    results.put("正确日志", result) ;
                    results.put("错误日志", errorResult) ;
                    
                    response.setHeader("Content-Disposition","attachment;filename=ProjectLog"+startDate+"-"+endDate+".xls");
                    response.setContentType("application/msexcel");
                    
                    OutputStream output = response.getOutputStream();
                    HSSFWorkbook ws = ExcelGenerator.doGenerate(results,workLogCols) ;
                    if(ws == null){
                        return null ;
                    }
                    ws.write(output) ;
                    output.close() ;
                    return null;
                }catch(Exception e){
                    e.printStackTrace();
                    return new ResponseFactory().createResponseBodyHtml("<script type=\"text/javascript\">alert('"+e.getMessage()+"');</script>");
                }
            }

        private Map<String,List<Map<String, Object>>> getWorkLogs(Map<String, Object> conditions, List<User> members) {
            
            List<Map<String, Object>> result = new ArrayList<Map<String,Object>>() ;
            List<Map<String, Object>> errorResult = new ArrayList<Map<String,Object>>();
            
            Map<String,List<Organization>> userCache = new HashMap<String,List<Organization>>() ;//机构信息
            Map<String,Map<String,Object>> memberCache = new HashMap<String,Map<String,Object>>() ;
            conditions.put("members", members) ;
            for (Map<String,Object> userDTO : members) {
                memberCache.put(userDTO.get(OrgUserConstant.USER_NAME).toString(), userDTO) ;
                List<Organization> org = OrgUserService.queryOrgByEmail(userDTO.get("email").toString());
                userCache.put(userDTO.get(OrgUserConstant.USER_NAME).toString(), org);
            }
            
            
            List<Map<String, Object>> logs = workLogDao.getSession().selectList("selectWorkLogs4ERP", conditions) ;
            isERPProject = new ArrayList<String>() ;
            
            for (Map<String,Object> worklog : logs) {
                try {
                    String userName = (String) worklog.get("USER_NAME") ;
                    
                    List<Organization> temp = userCache.get(userName) ;
                    Map<String,Object> user = memberCache.get(userName);
                    
                    for(Organization o:temp){
                    	if(o.get("outId")!=null && !o.get("outId").toString().equals("")){
                    		String outId = o.get("outId").toString();
                    		if(o.containsKey("type") && o.get("type").equals("4")){
                    			worklog.put("机构ID", outId.substring(4, outId.length())) ;//erp系统机构id 截除imp_
                    		}else{
                    			worklog.put("机构ID", outId) ;
                    		}
                    		worklog.put("机构名", o.getName()) ;
                    		break;
                    	}
                    }
                    
                    worklog.put("用户工号", user.get("workno")) ;
                    worklog.put("用户名", worklog.get("USER_REALNAME")) ;
                    worklog.remove("USER_NAME") ;
                    
                    if(logCompletelyCheck(worklog)){
                        result.add(worklog) ;
                    }else{
                        errorResult.add(worklog) ;
                    }
                    //又要验证编号正确性，又要导出整个编号
                    if(worklog.containsKey("PROJECT_CODE")) worklog.remove("PROJECT_CODE") ;
                } catch (Exception e) {
                    continue;
                }
            }
            Map<String,List<Map<String, Object>>> results = new HashMap<String,List<Map<String,Object>>>() ;
            results.put("正确日志", result) ;
            results.put("错误日志", errorResult) ;
            
            return results;
        }
        
         private boolean logCompletelyCheck(Map<String, Object> worklog) {
             
                if(String.valueOf(worklog.get("工作时长")).equals("0.0")){
                    worklog.put("错误信息", "工时为0") ;
                    return false ;
                }else if(isERPProject.contains(worklog.get("合同编号"))){
                    return true ;
                }else{
                    int count = erpDao.getSession().selectOne("selectByCode",worklog.get("合同编号"));
                    if(count==0){
                        worklog.put("错误信息", "编号无法匹配") ;
                        return false ;
                    }else{
                        isERPProject.add((String) worklog.get("合同编号")) ;
                        return true ;
                    }
                }
        }

    }