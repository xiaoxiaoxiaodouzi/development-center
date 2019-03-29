package com.chinacreator.c2.project.weeklyReport;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.ioc.ApplicationContextManager;
import com.chinacreator.c2.project.preference.Preference;
import com.chinacreator.c2.project.preference.PreferenceConstants;
import com.chinacreator.c2.project.preference.ProjectPreferenceService;

public class WeekReportPersisTool {
	
	@SuppressWarnings("unused")
	private static Logger LOGGER = LoggerFactory.getLogger(WeekReportPersisTool.class) ;
	
	private static WeekReportPersisTool INSTANCE ;
	
	@SuppressWarnings("unused")
	private ProjectPreferenceService projectPreferService = new ProjectPreferenceService() ;
	
	public static WeekReportPersisTool getINSTANCE() {
		if(INSTANCE==null){
			INSTANCE = ApplicationContextManager.getContext().getBean(WeekReportPersisTool.class);
		}
		return INSTANCE;
	}
	
	
	@Transactional
	public void submitWeeklyReport(WeeklyReport report,List<TaskSnapshot> tasks){
		WeeklyStatistics statistics=new WeeklyStatistics();
		
		double estimate=0;
		double consumed=0;
		int total=0;
		int completed=0;
		int canceled=0;
		double worked=0;
		int delay = 0 ;
		int crossWeek = 0 ;
		
		Set<String> members=new HashSet<String>();
		
		for(TaskSnapshot snapshot:tasks){
			if(snapshot.isBelongToNextWeek() || snapshot.isCrossProject()) continue ;
			total++ ;
			snapshot.setReportId(report.getId()) ;
			members.add(snapshot.getAssignedTo());
			estimate+=(double)snapshot.getEstimate();
			consumed+=(double)snapshot.getConsumed();
			worked+=(double)snapshot.getWorked();
			if(snapshot.isIsDone()){
				completed++;
			}
			//逾期任务
			if(snapshot.getStatus().equals("3")){
				delay++ ;
			}
			//跨周任务
			if(snapshot.getStatus().equals("2")){
				crossWeek++ ;
			}
		}
		
		statistics.setDelayed(delay) ;
		statistics.setCrossWeek(crossWeek) ;
		statistics.setCanceled(canceled);
		statistics.setCompleted(completed);
		statistics.setConsumed(consumed);
		statistics.setWorked(worked);
		statistics.setEstimate(estimate);
		statistics.setMember(members.size());
		statistics.setTotal(total);
		//项目周报可见范围偏好
		if(StringUtils.isNotBlank(report.getVisibleRange())){
			Preference projectPrefer = new Preference() ;
			projectPrefer.setProjectId(report.getProjectId()) ;
			projectPrefer.setPreferName(PreferenceConstants.WEEKLYREPORT_VISIBLE_RANGE) ;
			projectPrefer.setPreferContent(report.getVisibleRange()) ;
			new ProjectPreferenceService().put(projectPrefer) ;
		}
		
		Dao<WeeklyStatistics> statisticDao=DaoFactory.create(WeeklyStatistics.class);
		statisticDao.insert(statistics);
		
		report.setStatistics(statistics);
		Dao<WeeklyReport> reportDao=DaoFactory.create(WeeklyReport.class);
		reportDao.update(report);
		
		Dao<TaskSnapshot> snapshotDao=DaoFactory.create(TaskSnapshot.class);
		snapshotDao.insertBatch(tasks) ;
	}

	@Transactional
	public void createLackWeeklyReport(List<WeeklyReport> reportArr) {
		DaoFactory.create(WeeklyReport.class).insertBatch(reportArr) ;
	}

	@Transactional
	public void addWeekReportSend2(List<WeeklyReportSubmit2> weeklyReportSubmit2, Integer projectId) {
		Dao<WeeklyReportSubmit2> dao = DaoFactory.create(WeeklyReportSubmit2.class) ;
		dao.getSession().delete("com.chinacreator.c2.project.weeklyReport.WeeklyReportSubmit2Mapper.deleteByProjectId", projectId) ;
		
		DaoFactory.create(WeeklyReportSubmit2.class).insertBatch(weeklyReportSubmit2) ;
	}
}
