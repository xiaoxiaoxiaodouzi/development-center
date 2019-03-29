package com.chinacreator.c2.project.task;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.DaoService;
import com.chinacreator.c2.ioc.ApplicationContextManager;
import com.chinacreator.c2.project.label.LabelTask;
import com.chinacreator.c2.project.stage.Stage;

public class TaskServiceTool {
	
	@SuppressWarnings("unused")
	private static Logger LOGGER = LoggerFactory.getLogger(TaskServiceTool.class) ;
	
	private static TaskServiceTool INSTANCE ;
	
	public static TaskServiceTool getINSTANCE() {
		if(INSTANCE==null){
			INSTANCE = ApplicationContextManager.getContext().getBean(TaskServiceTool.class);
		}
		return INSTANCE;
	}
	
	@Transactional
	public void createTask(Task task){
		DaoService daoService = new DaoService() ;
		daoService.insertAllCascade(task) ;
	}
	
	public Integer projectTaskMaxOrder(int projectId){
		Object maxOrder;
		try {
			maxOrder = DaoFactory.create(Task.class).getSession().selectOne("projectTaskMaxOrder",projectId);
		} catch (Exception e) {
			maxOrder=0;
		}
		if(maxOrder==null)maxOrder=0;
		return (Integer)maxOrder;
	}
	public Integer myTaskMaxOrder(String username){
		Object maxOrder;
		try {
			maxOrder = DaoFactory.create(Task.class).getSession().selectOne("myTaskMaxOrder",username);
		} catch (Exception e) {
			maxOrder=0;
		}
		if(maxOrder==null)maxOrder=0;
		return (Integer)maxOrder;
	}
	
	@Transactional
	public Task saveEstimate(Estimate estimate,Task oldTask){
		Timestamp recordTimestamp = new Timestamp(System.currentTimeMillis()) ;
		estimate.setRecordDate(recordTimestamp) ;
		
		Dao<Task> taskDao = DaoFactory.create(Task.class);
		oldTask = taskDao.selectByID(estimate.getTaskId()) ;
		//更新工时
		estimate.setAccount(oldTask.getAssignedTo()) ;
		if(StringUtils.isBlank(estimate.getDescription())){
			estimate.setDescription(oldTask.getName()) ;
		}
		DaoFactory.create(Estimate.class).insert(estimate) ;
		//同步任务工时 
		Task task = new Task() ;
		task.setId(estimate.getTaskId()) ;
		task.setStageId(oldTask.getStageId());//任务阶段id
		task.setConsumed(oldTask.getConsumed()+estimate.getConsumed()) ;
		task.setIsConfirm(oldTask.getIsConfirm());//任务确认状态
		//如果起始日期为空,则用工时的工作日期初始化,截止日期为空的情况则放在任务完成时处理
		/*if(oldTask.getEstStartDate()==null){
			task.setEstStartDate(estimate.getWorkDate()) ;
			if(oldTask.getDeadline()==null){
				int dayNum = (int) (oldTask.getEstimate()>0?Math.ceil(oldTask.getEstimate()/8-1):0);
				Date date = estimate.getWorkDate() ;
				GregorianCalendar calendar = new GregorianCalendar(date.getYear()+1900,date.getMonth(),date.getDate()) ;
				calendar.add(GregorianCalendar.DATE,dayNum) ; 
				task.setDeadline(new Date(calendar.getTimeInMillis())) ;
			}
		}*/
		//消耗数大于工时数的情况下,剩余始终为0
		if(oldTask.getEstimate()<=task.getConsumed()){
			task.setLeft(0.0) ;
		}else{
			task.setLeft(oldTask.getEstimate()-task.getConsumed()) ;
		}
		DaoFactory.create(Task.class).update(task) ;
		
		return task ;
	}

	@Transactional
	public Task updateEstimate(Estimate estimate,Task oldTask) {
		oldTask = DaoFactory.create(Task.class).selectByID(estimate.getTaskId()) ;
		
		Estimate oldEst = (Estimate) DaoFactory.create(Estimate.class).selectByID(estimate.getId()) ;
		DaoFactory.create(Estimate.class).update(estimate) ;
		
		Task task = new Task() ;
		task.setId(estimate.getTaskId()) ;
		task.setStageId(oldTask.getStageId());//任务阶段id
		task.setConsumed(oldTask.getConsumed()-oldEst.getConsumed()+estimate.getConsumed()) ;
		task.setIsConfirm(oldTask.getIsConfirm());//任务确认状态
		//消耗数大于工时数的情况下,剩余始终为0
		if(oldTask.getEstimate()<=task.getConsumed()){
			task.setLeft(0.0) ;
		}else{
			task.setLeft(oldTask.getEstimate()-task.getConsumed()) ;
		}
		DaoFactory.create(Task.class).update(task) ;
		
		return task ;
	}
	
	@Transactional
	public Task deleteEstimate(Estimate estimate, Task oldTask) {
		oldTask = DaoFactory.create(Task.class).selectByID(estimate.getTaskId()) ;
		
		DaoFactory.create(Estimate.class).delete(estimate) ;
		
		Task task = new Task() ;
		task.setId(estimate.getTaskId()) ;
		task.setStageId(oldTask.getStageId());//任务阶段id
		task.setIsConfirm(oldTask.getIsConfirm());//任务确认状态
		task.setConsumed(oldTask.getConsumed()-estimate.getConsumed()) ;
		//消耗数大于工时数的情况下,剩余始终为0
		if(oldTask.getEstimate()>task.getConsumed())
			task.setLeft(oldTask.getEstimate()-task.getConsumed()) ;
		DaoFactory.create(Task.class).update(task) ;
		
		return task ;
	}
	
	@Transactional
	public void updateTaskLabels(int taskId, List<LabelTask> labels) {
		LabelTask condition = new LabelTask() ;
		condition.setTaskId(taskId) ;
		
		Dao<LabelTask> dao = DaoFactory.create(LabelTask.class) ;
		dao.getSession().delete("deleteByTaskId", taskId) ;
		
		if(labels.size()!=0)
			dao.insertBatch(labels) ;
	}
	
	@Transactional
	public void deleteTask(Task task) {
		DaoFactory.create(Task.class).delete(task) ;
		DaoFactory.create(Estimate.class).getSession().delete("deleteEstimateByTaskId", task.getId()) ;
		DaoFactory.create(History.class).getSession().delete("deleteActionByTaskId", task.getId()) ;
	}

	public Integer getStageId(Integer projectId, Integer milestoneId,String name) {
		Dao<Stage> dao = DaoFactory.create(Stage.class);
		Stage s = new Stage();
		s.setProjectId(projectId);
		if(milestoneId==null) milestoneId=0;
		s.setMilestoneId(milestoneId);
		s.setName(name);
		s = dao.selectOne(s);
		return s.getId();
	}

}
