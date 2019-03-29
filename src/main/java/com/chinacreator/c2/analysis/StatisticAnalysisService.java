package com.chinacreator.c2.analysis;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.DaoService;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;

public class StatisticAnalysisService {
	
	public List<StatisticsAnalysisWidget> getStatisticAnalysisWidgets(){
		StatisticsAnalysisWidget entity = new StatisticsAnalysisWidget() ;
		entity.setEnabled(true) ;
		return DaoFactory.create(StatisticsAnalysisWidget.class).select(entity) ;
	}
	
	public List<StatisticAnalysis> getStatisticAnalysisMenu(){
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
		
		StatisticAnalysis entity = new StatisticAnalysis() ;
		entity.setCreateBy(uu.get("user_id").toString()) ;
		return DaoFactory.create(StatisticAnalysis.class).select(entity) ;
	}
	
	public StatisticAnalysis getStatisticAnalysis(int analysisID){
		return null ;
	}
	
	public void getStatisticAnalysis(StatisticAnalysis entity){
		DaoFactory.create(StatisticAnalysis.class).insert(entity) ;
	}
	
	@Transactional
	public void addStatisticAnalysis(StatisticAnalysis stat){
		DaoService dao = new DaoService() ;
		if(stat.getId()==null){
			dao.insertAllCascade(stat) ;
		}else{
			dao.deleteCascade(stat) ;
			dao.insertAllCascade(stat) ;
		}
	}
	
}
