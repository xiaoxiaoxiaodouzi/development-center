package com.chinacreator.c2.project.log;

import java.sql.Timestamp;
import java.util.Map;

import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.project.task.Action;
import com.chinacreator.c2.project.task.Task;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;

public class ActionBuilder {
	
	private Action action ;
	
	public ActionBuilder(Action action) {
		super();
		this.action = action;
	}
	
	public static ActionBuilder Default(){
		Action	action = new Action() ;
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		action.setActor(currentUserName);
		return new ActionBuilder(action) ;
	}
	
	/**
	 *  生成任务类Action(type、Comment、extra属性)
	 * 
	 * @param taskID
	 * @return
	 */
	public static ActionBuilder taskDefault(Integer taskId,Timestamp recordTimestamp){
		Dao<Task> taskDao = DaoFactory.create(Task.class) ;
		Task task = taskDao.selectByID(taskId) ;
		
		return ActionBuilder
				.Default()
				.recordTimestamp(recordTimestamp)
				.target("task", task.getId())
				.project(task.getProjectId()) ; 
	}
	
	public ActionBuilder recordTimestamp(Timestamp recordTimestamp){
		this.action.setDate(recordTimestamp) ;
		return this ;
	}
	
	public ActionBuilder target(String targetType,Integer targetId){
		this.action.setTargetType(targetType) ;
		this.action.setTargetId(targetId) ;
		return this;
	}
	
	public ActionBuilder project(Integer projectId){
		this.action.setProjectId(projectId) ;
		return this;
	}
	
	public ActionBuilder actor(String actor){
		this.action.setActor(actor) ;
		return this;
	}
	
	public ActionBuilder type(String type){
		this.action.setType(type) ;
		return this;
	}
	
	public ActionBuilder comment(String comment){
		this.action.setComment(comment) ;
		return this;
	}
	
	public ActionBuilder extra(String extra){
		this.action.setExtra(extra) ;
		return this;
	}
	
	public ActionBuilder extra1(String extra1){
		this.action.setExtra1(extra1) ;
		return this;
	}

	public Action build() {
		return this.action;
	}

}
