package com.chinacreator.c2.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.module.echart.AreaStyle;
import com.chinacreator.c2.module.echart.BarSerie;
import com.chinacreator.c2.module.echart.DataItem;
import com.chinacreator.c2.module.echart.EChartsData;
import com.chinacreator.c2.module.echart.ItemStyle;
import com.chinacreator.c2.module.echart.LineSerie;
import com.chinacreator.c2.module.echart.PieSerie;
import com.chinacreator.c2.module.echart.Serie;
import com.chinacreator.c2.module.echart.Style;
import com.chinacreator.c2.module.echart.Tooltip;
import com.chinacreator.c2.project.bug.Bug;
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.project.service.ProjectService;
import com.chinacreator.c2.project.service.ProjectSimpleStatistics;
import com.chinacreator.c2.report.bean.EchartQueryParams;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;

public class ReportServices {
	
	private static Map<String,String> bugStatus ;
	
	static{
		bugStatus = new HashMap<String,String>() ;
		bugStatus.put("0", "关闭") ;
		bugStatus.put("1", "创建") ;
		bugStatus.put("2", "打开") ;
		bugStatus.put("3", "重新打开") ;
		bugStatus.put("4", "延后") ;
		bugStatus.put("5", "拒绝") ;
		bugStatus.put("6", "完成") ;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getProjectEchartDatas(EchartQueryParams params){
		Dao<Project> dao = DaoFactory.create(Project.class) ;
		LabelSon bugPieLabel =new LabelSon();
		bugPieLabel.setFormatter("{b} : {c}个 (占{d}%)");
		ItemStyle bugPieStyle = new ItemStyle(new Style().setLabel(bugPieLabel)) ;
		
		LabelSon estPieLabel =new LabelSon();
		estPieLabel.setFormatter("{b} : {c}h (占{d}%)");
		ItemStyle estPieStyle = new ItemStyle(new Style().setLabel(estPieLabel)) ;
		
		PieSerie bugStatusPie = new PieSerie("BUG状态分布图")
				.setTooltip(new Tooltip("item", "{a} <br/>{b} : {c}个 (占{d}%)"))
				.setCenter(new String[]{"50%","50%"}) 
				.setRadius(new String[]{"0","120"})
				.setItemStyle(bugPieStyle);
	
		List<Map<String,Object>> statusRatioResult = DaoFactory.create(Bug.class).getSession().selectList("getBugStatusRatio", params.getProjectId()) ;

		for (Map<String,Object> ratioResult:statusRatioResult) {
			bugStatusPie.addData(new DataItem(bugStatus.get(ratioResult.get("status").toString()),Integer.parseInt(ratioResult.get("count").toString()))) ;
		}
		
		List<Map<String,Object>> result = dao.getSession().selectList("getAllProjectEcharts", params) ;
		
		BarSerie createTaskBar = new BarSerie("新增任务").setBarWidth(20).setStack("新增") ;
		BarSerie createBugBar = new BarSerie("新增Bug").setBarWidth(20).setStack("新增") ;
		
		LineSerie unfinishedTaskLine = new LineSerie("未完成任务") ;
		LineSerie unclosedBugLine = new LineSerie("未关闭BUG") ;
		
		LineSerie allConsumedLine = new LineSerie("总消耗工时") ;
		LineSerie bugfixedLine = new LineSerie("BUG总消耗工时") ;
		
		LineSerie weekConsumedLine = new LineSerie("周消耗工时") ;
		LineSerie weekBugfixedLine = new LineSerie("周Bug消耗工时") ;
		ItemStyle style = new ItemStyle(new Style().setAreaStyle(new AreaStyle().setType("default"))) ;
		weekConsumedLine.setItemStyle(style) ;
		weekBugfixedLine.setItemStyle(style) ;
		
		
		int taskBugStatusDataMaxY = 0 ;
		int estiamteMaxY = 0 ;
		int weekEstimateMaxY = 0 ;
		
		Map<String,Double[]> memberBar = new HashMap<String,Double[]>() ;
		List<String> xAxis = new ArrayList<String>() ;
		List<Serie> estimateList = new ArrayList<Serie>() ;
		
		
		
		Map<String,String> nameMapper = new HashMap<String,String>() ;
		
		Double[] d = new Double[result.size()] ;
		for (int i = 0; i < d.length; i++) {
			d[i] = 0.0 ;
		}
		
		List<String> members = dao.getSession().selectList("getMemberOfContainEstimated", params) ;
		
		
		PieSerie estimatePie = new PieSerie("工作量统计")
		.setTooltip(new Tooltip("item", "{a} <br/>{b} : {c}小时 (占{d}%)"))
		.setCenter(new String[]{"50%","50%"}) 
		.setRadius(new String[]{"0","120"})
		.setItemStyle(estPieStyle);
		
	
	
		
	
		for (String userName : members) {
			Double[] temp1 = new Double[result.size()];
			System.arraycopy(d, 0, temp1 ,0 , result.size()) ;
			memberBar.put(userName, temp1) ;
		}
		
		
		Double allAlreadyConsumed = 0.0 ;
		Double bugAlreadyConsumed = 0.0 ;
		int taskConsumed = 0 ;
		int bugConsumed = 0 ;
		
		for (int index=0;index<result.size() ;index++) {
			int finishedTaskNum = 0 ;
			int closedBugNum = 0;
			int createTaskNum = 0;
			int createBugNum = 0 ;
			
			Map<String, Object> weekResult = result.get(index);
			xAxis.add("第"+weekResult.get("week").toString()+"周") ;
			if(weekResult.containsKey("finishedTaskNum"))
				finishedTaskNum = Integer.parseInt(weekResult.get("finishedTaskNum").toString()) ;
			if(weekResult.containsKey("closedBugNum"))
				closedBugNum = Integer.parseInt(weekResult.get("closedBugNum").toString()) ;
			if(weekResult.containsKey("createTaskNum")){
				createTaskNum = Integer.parseInt(weekResult.get("createTaskNum").toString()) ;
			}
			if(weekResult.containsKey("createBugNum")){
				createBugNum = Integer.parseInt(weekResult.get("createBugNum").toString()) ;
			}
			if(weekResult.containsKey("allConsumed")){
				Double allConsumed = Double.parseDouble(weekResult.get("allConsumed").toString()) ;
				allAlreadyConsumed += allConsumed ;
				weekConsumedLine.addData(allConsumed) ;
				if(weekEstimateMaxY<allConsumed) weekEstimateMaxY = allConsumed.intValue() ;
			}else{
				weekConsumedLine.addData(0.0) ;
			}
			if(weekResult.containsKey("bugFixConsumed")){
				Double bugFixConsumed = Double.parseDouble(weekResult.get("bugFixConsumed").toString()) ;
				bugAlreadyConsumed += bugFixConsumed ;
				weekBugfixedLine.addData(bugFixConsumed) ;
				if(weekEstimateMaxY<bugFixConsumed) weekEstimateMaxY = bugFixConsumed.intValue() ;
			}else{
				weekBugfixedLine.addData(0.0) ;
			}
			
			createTaskBar.addData(createTaskNum) ;
			if(taskBugStatusDataMaxY<createTaskNum) taskBugStatusDataMaxY = createTaskNum ;
			
			createBugBar.addData(createBugNum) ; 
			if(taskBugStatusDataMaxY<createBugNum) taskBugStatusDataMaxY = createBugNum ;
			
			taskConsumed +=  (createTaskNum - finishedTaskNum) ;
			if(taskBugStatusDataMaxY<taskConsumed) taskBugStatusDataMaxY = taskConsumed ;
			
			bugConsumed += (createBugNum - closedBugNum) ;
			if(taskBugStatusDataMaxY<bugConsumed) taskBugStatusDataMaxY = bugConsumed ;
			
			unfinishedTaskLine.addData(taskConsumed) ;
			unclosedBugLine.addData(bugConsumed) ;
			allConsumedLine.addData(allAlreadyConsumed) ;
			if(allAlreadyConsumed>estiamteMaxY){
				estiamteMaxY = allAlreadyConsumed.intValue() ;
			} 
			bugfixedLine.addData(bugAlreadyConsumed);
			
			
			if(weekResult.containsKey("consumedList")){
					for(Map<String,Object> consumed : (List<Map<String,Object>>)weekResult.get("consumedList")){
						nameMapper.put(consumed.get("account").toString(), consumed.get("userRealname").toString()) ;
						if(index ==0){
							memberBar.get(consumed.get("account").toString())[index] +=
									Double.parseDouble(consumed.get("consumed").toString()) ;
						}else{
							memberBar.get(consumed.get("account").toString())[index] = memberBar.get(consumed.get("account").toString())[index-1] +
									Double.parseDouble(consumed.get("consumed").toString()) ;
						}
					}
				
					if(index>0){
						for (Entry<String,Double[]> entry : memberBar.entrySet()) {
							if(entry.getValue()[index]==0.0){
								entry.getValue()[index] = entry.getValue()[index-1] ;
							}
						}
					}
			}
		}

	
		//项目工作量统计,只统计最新一周，饼图
		for(Entry<String, Double[]> entry :memberBar.entrySet()){ 
			int i =entry.getValue().length-1;
			estimatePie.addData(new DataItem( nameMapper.get(entry.getKey())==null?entry.getKey():nameMapper.get(entry.getKey()).toString(), entry.getValue()[i].intValue()));
			
		}
		//旧的项目工作量统计
		for(Entry<String, Double[]> entry :memberBar.entrySet()){ 
			estimateList.add(new BarSerie(entry.getKey())
			                .setData(entry.getValue())
							.setStack("成员工时")
							.setBarWidth(20)
							.setName(nameMapper.get(entry.getKey())==null?entry.getKey():nameMapper.get(entry.getKey()).toString())) ;
		}
		
		estimateList.add(allConsumedLine) ;
		estimateList.add(bugfixedLine) ;
		EChartsData taskBugStatusData = new EChartsData(xAxis.toArray(new Object[0]),new Serie[]{bugStatusPie}) ;
		//EChartsData estimateData = new EChartsData(xAxis.toArray(new String[0]),estimateList.toArray(new Serie[0])) ;
		EChartsData estimateData = new EChartsData(xAxis.toArray(new String[0]),new Serie[]{estimatePie}) ;
		EChartsData weekEstimateData = new EChartsData(xAxis.toArray(new String[0]),new Serie[]{weekBugfixedLine,weekConsumedLine}) ;
		
		Map<String,Object> resultMap = new HashMap<String,Object>() ;
		resultMap.put("taskBugStatusData", taskBugStatusData) ;
		resultMap.put("estimateData", estimateData) ;
		resultMap.put("weekEstimateData", weekEstimateData) ;
		resultMap.put("taskBugStatusDataMaxY", getProperYAxisNum(taskBugStatusDataMaxY)) ;
		resultMap.put("estiamteMaxY", getProperYAxisNum(estiamteMaxY)) ;
		resultMap.put("estiamteCount", estiamteMaxY) ;
		resultMap.put("weekEstimateMaxY", getProperYAxisNum(weekEstimateMaxY)) ;
		resultMap.put("statistics", new ProjectService().getProjectStatistics(Arrays.asList(new Integer[]{params.getProjectId()}))) ;
		return resultMap;
	}
	
	public Integer getProperYAxisNum(Integer num){
		num += (int)(num*0.5) ;
		if(num<10){
			return 10 ;
		}else if(num<100){
			return ((num/10)+2)*10;
		}else{
			String numStr = num + "" ;
			int length = numStr.length() ;
			return (Integer.parseInt(numStr.charAt(0)+"") * (int)Math.pow(10, length-1)) + 
					(Integer.parseInt(numStr.charAt(1)+"")* (int)Math.pow(10, length-2)) ;
		}
		
	}
	
	public List<ProjectSimpleStatistics> getProjectStatisticsOfCurrentUser(){
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
		Dao<Project> dao = DaoFactory.create(Project.class) ;
		List<Integer> projectIds = dao.getSession().selectList("getCurrentUsersProjectIds", uu.get("user_id").toString()) ;
		Map<String,Object> conditions = new HashMap<String,Object>() ;
		conditions.put("projectIds", projectIds) ;
		List<ProjectSimpleStatistics> result = dao.getSession().selectList("getProjectStatistics",conditions) ;
		return result ;
	} 
	
	
	
	
}
