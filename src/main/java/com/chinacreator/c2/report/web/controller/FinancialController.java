package com.chinacreator.c2.report.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.project.service.WorkLogService;
import com.chinacreator.c2.report.constant.ReportColumnNames;
import com.chinacreator.c2.report.tools.ExcelGenerator;
import com.chinacreator.c2.report.workLog.WorkLog;
import com.chinacreator.c2.web.controller.ResponseFactory;

@Controller
@RequestMapping("report/finance")
public class FinancialController {
	
	@RequestMapping(value = "/{start}/{end}/{deptIds:.+}", method = RequestMethod.GET)
	 public Object generateWorkLogFile(@PathVariable Long start,@PathVariable Long end,@PathVariable String deptIds, HttpServletResponse response)
	            throws IOException {
		try{
    		Dao<WorkLog> dao = DaoFactory.create(WorkLog.class) ;
    		Map<String,Object> conditions = new HashMap<String,Object>() ;
    		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>() ;
    		
    		String startDate = "" ;
    		String endDate = "" ;
    		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd") ;
    		
    		conditions.put("start",new Date(start)) ;
    		startDate = df.format(new Date(start)) ;
    		
    		
    		conditions.put("end",new Date(end)) ;
    		endDate = df.format(new Date(end)) ;
    		
    		List<String> deptIDs = new ArrayList<String>() ;
    		if(StringUtils.isNotBlank(deptIds))
    			deptIDs = JSON.parseArray(deptIds, String.class) ;
    		
    		conditions.put("memberInfos", WorkLogService.getMembersByDeptIds(deptIDs)) ;
    		
    		result = dao.getSession().selectList("selectFinancialReport", conditions) ;
    		
	        response.setHeader("Content-Disposition","attachment;filename=FinancialLog"+startDate+"-"+endDate+".xls");
	        response.setContentType("application/msexcel");
	        
	        OutputStream output = response.getOutputStream();
	        
	        HSSFWorkbook ws = ExcelGenerator.doGenerate(result,ReportColumnNames.FINANCIAL_REPORT) ;
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

}
