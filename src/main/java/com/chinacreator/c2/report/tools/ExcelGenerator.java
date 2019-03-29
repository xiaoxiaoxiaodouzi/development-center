package com.chinacreator.c2.report.tools;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *  Excel生成器
 * 
 * @author tyf
 *
 */
public class ExcelGenerator {
	
	/**
	 * @param results key为sheet名字,value为数据结果集
	 * @param cols key为sheet名字,value为excel的属性名(与results的结果集对应)
	 * @return
	 */
	public static HSSFWorkbook doGenerate(Map<String, List<Map<String, Object>>> results,Map<String, Set<String>> cols){
		 HSSFWorkbook wb = new HSSFWorkbook();  
		 
		 for (Entry<String, List<Map<String, Object>>> result : results.entrySet()) {
			 String sheetName = result.getKey() ;
			 HSSFSheet sheet = wb.createSheet(sheetName);
			 Set<String> columns = null ;
			 if(cols.containsKey(sheetName)){
				 columns = cols.get(sheetName) ;
			 }else{
				 if(result.getValue().size()==0) continue ;
				 columns = result.getValue().get(0).keySet() ;
			 }
			 
			//初始化第一行
		     int index = 0 ;
		     HSSFRow row = sheet.createRow(0);  
		     for(String key:columns){
		    	 HSSFCell cell = row.createCell(index) ;
			     cell.setCellValue(key);
			     index ++ ;
		     }
		     //初始化内容
		     for(int i = 0; i < result.getValue().size(); i++){
		    	 row = sheet.createRow(i + 1);  
		    	 Map<String,Object> map = (Map<String, Object>) result.getValue().get(i) ;
		    	 int columnIndex = 0 ;
		    	 for(String columnName : columns){
		    		 if(map.containsKey(columnName)){
		    			 if(map.get(columnName) instanceof Date){
			    			 row.createCell(columnIndex).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format((Date)map.get(columnName))) ;
			    		 }else{
			    			 row.createCell(columnIndex).setCellValue(map.get(columnName)==null?"":map.get(columnName).toString()) ;
			    		 }
		    		 }else{
		    			 row.createCell(columnIndex).setCellValue("") ;
		    		 }
		    		 columnIndex ++ ;
		    	 }
		     }
		     
			
		}
	     return wb ;
	}
	
	public static HSSFWorkbook doGenerate(List<Map<String, Object>> result) {
		if(result.size()==0) return new HSSFWorkbook(); 
		Map<String, List<Map<String, Object>>> input = new HashMap<String,List<Map<String,Object>>>() ;
		input.put("Sheet1", result) ;
		Map<String, Set<String>> cols = new HashMap<String,Set<String>>() ;
		cols.put("Sheet1", result.get(0).keySet()) ;
		return doGenerate(input,cols);
	}

	public static HSSFWorkbook doGenerate(List<Map<String, Object>> result,Set<String> columns) {
		Map<String, List<Map<String, Object>>> input = new HashMap<String,List<Map<String,Object>>>() ;
		input.put("Sheet1", result) ;
		Map<String, Set<String>> cols = new HashMap<String,Set<String>>() ;
		cols.put("Sheet1", columns) ;
		return doGenerate(input,cols);
	}
}
