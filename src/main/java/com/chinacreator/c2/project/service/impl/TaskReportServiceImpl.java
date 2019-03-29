package com.chinacreator.c2.project.service.impl;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;
import com.chinacreator.c2.project.service.TaskReportService;
import com.chinacreator.c2.project.task.Task;
import com.chinacreator.c2.web.ds.exception.InvalidQueryException;

public class TaskReportServiceImpl implements TaskReportService{

	@Override
	public List<Task> getTasksInPeriod(Date start, Date end, Integer projectId) {
		if (start==null || end==null) {
			throw new InvalidQueryException("查询任务起止时间不能为空...");
		}
		Map<String,Object> condition = new HashMap<String,Object>() ;
		condition.put("start", start) ;
		condition.put("end", end) ;
		condition.put("projectId", projectId) ;
		
		return DaoFactory.create(Task.class).getSession().selectList(
				"selectTasksInPeriod", condition);
	}

	@Override
	public List<Task> getTasksInPeriod(Date start, Date end, Integer projectId,
			RowBounds4Page rowBounds) {
		if (start==null || end==null) {
			throw new InvalidQueryException("查询任务起止时间不能为空...");
		}
		Map<String,Object> condition = new HashMap<String,Object>() ;
		condition.put("start", start) ;
		condition.put("end", end) ;
		condition.put("projectId", projectId) ;
		
		return DaoFactory.create(Task.class).getSession().selectList(
				"selectTasksInPeriod", condition , rowBounds);
	}
	
}
