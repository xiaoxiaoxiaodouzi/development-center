package com.chinacreator.c2.project.service;

import java.sql.Date;
import java.util.List;

import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;
import com.chinacreator.c2.project.task.Task;

public interface TaskReportService {
	
	/** 
	 *  获取某个时间范围内的任务,只要任务的"起始-截止"与入参"起始-截止"有交集都会包含在内
	 * 
	 * @param start 起始时间(必填)
	 * @param end 结束时间(必填)
	 * @param projectId 项目ID(可选)
	 * @return
	 */
	public List<Task> getTasksInPeriod(Date start,Date end,Integer projectId) ;
	
	/** 
	 *  获取某个时间范围内的任务,只要任务的"起始-截止"与入参"起始-截止"有交集都会包含在内
	 * 
	 * @param start 起始时间(必填)
	 * @param end 结束时间(必填)
	 * @param projectId 项目ID(可选)
	 * @param rowBounds 动态参数(可选)
	 * @return
	 */
	public List<Task> getTasksInPeriod(Date start,Date end,Integer projectId,RowBounds4Page rowBounds) ;
	
}
