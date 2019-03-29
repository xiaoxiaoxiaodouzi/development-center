package com.chinacreator.c2.project.preference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.project.milestone.Milestone;
import com.chinacreator.c2.project.preference.exception.InvalidPreferenceParamsException;
import com.chinacreator.c2.project.stage.Stage;
import com.chinacreator.c2.project.task.Task;
import com.google.common.collect.Lists;

public class ProjectPreferenceService {

	/**
	 * 若偏好已存在则更新,若不存在则新增
	 * 
	 * @param prefer
	 *            偏好对象
	 */
  @Transactional
	public void put(Preference prefer) {
		Dao<Preference> dao = DaoFactory.create(Preference.class);
		Preference condition = new Preference();
		condition.setPreferId(prefer.getPreferId());
		condition.setProjectId(prefer.getProjectId());
		condition.setPreferName(prefer.getPreferName());
		List<Preference> result = dao.select(condition);

		if (result.size() > 1) {
			throw new InvalidPreferenceParamsException("查到多条偏好记录...");
		} else if (result.size() == 1) {
			if (prefer.getPreferId() == null)
				prefer.setPreferId(result.get(0).getPreferId());
			dao.update(prefer);
		} else {
			dao.insert(prefer);
		}
		if(prefer.getPreferName().equals("projectTaskConfirmState")){//任务强制确认
		  projectTaskConfirmChange(prefer.getProjectId(),prefer.getPreferContent());
		}
	}

  //项目任务强制确认修改
  private void projectTaskConfirmChange(Integer projectId, String preferContent) {
    if(preferContent.equals("true")){//任务需要确认，项目下的任务看板增加“待确认”
      Milestone milestone = new Milestone();
      milestone.setProjectId(projectId);
      List<Milestone> milestones = DaoFactory.create(Milestone.class).select(milestone);
      List<Stage> list = Lists.newArrayList();
      Stage stage = new Stage();
      stage.setProjectId(projectId);
      stage.setMilestoneId(null);
      stage.setName("待确认");
      stage.setOrderNo(0);
      list.add(stage);
      for(Milestone m:milestones){
        stage = new Stage();
        stage.setProjectId(projectId);
        stage.setMilestoneId(m.getId());
        stage.setName("待确认");
        stage.setOrderNo(0);
        list.add(stage);
      }
      DaoFactory.create(Stage.class).insertBatch(list);
    }else{//任务取消确认，项目下的任务看板删除“待确认”，待确认看板中的任务移动至“未开始”中
      Stage stage = new Stage();
      stage.setProjectId(projectId);
      stage.setName("待确认");
      List<Stage> stages = DaoFactory.create(Stage.class).select(stage);//待确认的看板集合
      List<Task> taskList = Lists.newArrayList();
      for(Stage s:stages){//所有待确认看板的任务
        Task task = new Task();
        task.setProjectId(projectId);
        task.setStageId(s.getId());
        List<Task> tasks = DaoFactory.create(Task.class).select(task);
        if(tasks.size()>0) taskList.addAll(tasks);
      }
      stage = new Stage();
      stage.setProjectId(projectId);
      stage.setName("未开始");
      List<Stage> unbeginList = DaoFactory.create(Stage.class).select(stage);//未开始的看板集合
      for(Stage s:unbeginList){
        for(Task t:taskList){
          if(s.getMilestoneId()==null&&t.getMilestoneId()==null){
            t.setStageId(s.getId());
            t.setIsConfirm(null);//去除确认状态
          }
          if(s.getMilestoneId()!=null&&t.getMilestoneId()!=null&&s.getMilestoneId().equals(t.getMilestoneId())){
            t.setStageId(s.getId());
            t.setIsConfirm(null);//去除确认状态
          }
        }
      }
      //已经确认的任务将isConfirm字段置空
      Task task = new Task();
      task.setProjectId(projectId);
      task.setIsConfirm("1");
      List<Task> tasks = DaoFactory.create(Task.class).select(task);
      if(tasks.size()>0){
        for(Task t:tasks){
          t.setIsConfirm(null);
        }
        DaoFactory.create(Task.class).updateBatch(tasks);
      }
      if(taskList.size()>0) DaoFactory.create(Task.class).updateBatch(taskList);//更新任务的阶段id
      DaoFactory.create(Stage.class).deleteBatch(stages);//删除待确认看板
    }
  }

  /**
	 * 获取项目偏好
	 * 
	 * @param projectId
	 *            项目主键(必填)
	 * @param preferName
	 *            偏好名(可选)
	 * @return map:(key:preferName,value:preferenceObj)
	 */
	public Map<String, Object> get(Integer projectId, String... preferNames) {
		if (projectId == null) {
			throw new InvalidPreferenceParamsException("偏好查询项目ID不能为空...");
		}
		Map<String, Object> result = new HashMap<String, Object>();
		// 初始化查询对象
		Preference entity = new Preference();
		entity.setProjectId(projectId);
		if(preferNames!=null && preferNames.length>0){
		  for(String preferName:preferNames){
	          result.put(preferName, null);
	          entity.setPreferName(preferName);
	          for (Preference pre : DaoFactory.create(Preference.class).select(entity)) {
	              result.put(pre.getPreferName(), pre);
	          }
	      }
		}else{
		  for (Preference pre : DaoFactory.create(Preference.class).select(entity)) {
            result.put(pre.getPreferName(), pre);
          }
		}
		
		return result;
	}

	/**
	 * 删除偏好
	 * 
	 * @param projectId
	 *            项目id(必填)
	 * @param preferName
	 *            偏好名
	 */
	public void remove(Integer projectId, String preferName) {
		if (projectId == null) {
			throw new InvalidPreferenceParamsException("偏好删除项目ID不能为空...");
		}
		// 初始化查询对象
		Preference entity = new Preference();
		entity.setProjectId(projectId);
		if (!StringUtils.isBlank(preferName)) {
			entity.setPreferName(preferName);
		}

		DaoFactory.create(Preference.class).delete(entity);
	}

	/**
	 * 删除项目所有偏好
	 * 
	 * @param projectId
	 *            项目id(必填)
	 */
	public void removeAll(Integer projectId) {
		remove(projectId, null);
	}

}
