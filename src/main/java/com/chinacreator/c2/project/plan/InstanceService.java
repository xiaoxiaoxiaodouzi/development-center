package com.chinacreator.c2.project.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.dao.mybatis.enhance.Pageable;
import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;
import com.chinacreator.c2.project.testcase.Case;
import com.chinacreator.c2.project.testcase.CaseStep;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;

public class InstanceService {

	/**
	 * 获取某计划下的实例总数
	 * @param planId
	 * @return
	 */
	public int selectInstanceCountById(Integer planId){
		Dao<Instance> instanceDao = DaoFactory.create(Instance.class);
		Instance instance = new Instance();
		instance.setPlanId(planId);
		return instanceDao.count(instance);
	}
	
	/**
	 * 根据选择的用例添加实例信息
	 * @param caseIdList
	 * @param planId
	 */
	public void addCaseInstanceInfo(String caseIdList,int planId){
		Dao<Instance> instanceDao = DaoFactory.create(Instance.class);
		Dao<Case> caseDao = DaoFactory.create(Case.class);
		Dao<InstanceResult> resultDao = DaoFactory.create(InstanceResult.class);
		Dao<InstanceStep> stepDao = DaoFactory.create(InstanceStep.class);
		Dao<StepResult> stepResultDao = DaoFactory.create(StepResult.class);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("caseIdList", caseIdList);
		params.put("planId", planId);
		//删除不再选择的记录，插入新选择的记录，其余保持不变
		stepResultDao.getSession().delete("deleteStepResultById", params);
		stepDao.getSession().delete("deleteStepInfoById", params);
		resultDao.getSession().delete("deleteResultInfoById", params);
		instanceDao.getSession().delete("deleteInstanceInfoById", params);
		//先删除后插入
		List<Case> caseList = caseDao.getSession().selectList("getCaseInfoById", params);
		for(Case map:caseList){
			Instance ins = insertInstanceInfo(planId, instanceDao, map);
			//生成实例id后插入实例步骤信息
			insertStepInfo(stepDao, (Integer) map.getId(), ins);
		}
		
	}
	
	//插入步骤信息
	private void insertStepInfo(Dao<InstanceStep> stepDao, int caseId, Instance ins) {
		List<Map<String,Object>> stepList = stepDao.getSession().selectList("getStepInfoByCaseId", caseId);
		List<InstanceStep> list = new ArrayList<InstanceStep>();
		for(Map<String,Object> ste:stepList){
			InstanceStep step = new InstanceStep();
			step.setExpectResult(ste.get("expect_result")!=null?ste.get("expect_result")+"":null);
			step.setStep(ste.get("step")+"");
			step.setInstanceId(ins.getId());
			step.setNo((Integer)ste.get("no_"));
			list.add(step);
		}
		stepDao.insertBatch(list);
	}
	
	//插入实例信息
	private Instance insertInstanceInfo(int planId,Dao<Instance> instanceDao, Case map) {
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Instance ins = new Instance();
		ins.setCaseId(map.getId());
		ins.setIsCommon(map.isIsCommon());
		ins.setLevel(map.getLevel());
		ins.setName(map.getName());
		ins.setNo(map.getNo());
		ins.setPlanId(planId);
		ins.setPrecondition(map.getPrecondition());
		ins.setStoryId(map.getStoryId());
		ins.setUserName(currentUserName);
		instanceDao.insert(ins);
		return ins;
	}
	
	/**
	 * 获取某计划的用例id数据集合
	 * @param planId
	 * @return
	 */
	public List<Map<String,Object>> getCaseIdsByPlanId(Map<String,Object> condition){
		Dao<Instance> instanceDao = DaoFactory.create(Instance.class);
		return instanceDao.getSession().selectList("getCaseIdsByPlanId", condition);
	}
	
	/**
	 * 通过计划id获取实例树信息
	 * @param planId
	 * @return
	 */
	public List<Map<String,Object>> getInstanceTreeById(int planId){
		Dao<Instance> instanceDao = DaoFactory.create(Instance.class);
		List<Map<String,Object>> list = instanceDao.getSession().selectList("getInstanceTreeById", planId);
		return list;
	}
	
	/**
	 * 通过id获取实例信息 包含实例步骤  设置结果 用例附件
	 * @param id
	 * @return
	 */
	public Map<String,Object> getCaseInstanceById(int id){
		InstanceStepService stepService = new InstanceStepService();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Dao<Instance> instanceDao = DaoFactory.create(Instance.class);
		Map<String,Object> instance = instanceDao.getSession().selectOne("getCaseInstanceById", id);
		resultMap.put("instance", instance);
		//实例步骤信息
		List<Map<String,Object>> stepList = stepService.getStepList(id);
		resultMap.put("stepList", stepList);
		return resultMap;
	}
	
	/**
	 * 更新实例的基本信息
	 * @param caseInstance
	 */
	public List<Map<String,Object>> updateInstanceBasicInfo(Instance caseInstance,List<InstanceStep> stepList){
		InstanceStepService stepService = new InstanceStepService();
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		caseInstance.setUserName(currentUserName);
		Dao<Instance> instanceDao = DaoFactory.create(Instance.class);
		instanceDao.update(caseInstance);
		if(caseInstance.getStoryId()==null){
			instanceDao.getSession().update("com.chinacreator.c2.project.plan.InstanceMapper.updateNull", caseInstance);
		}
		//删除之前的步骤，重新插入
		if(stepList.size()>0&&stepList!=null){
			stepService.insertBatchStep(caseInstance, stepList);
		}
		return stepService.getStepList(caseInstance.getId());
	}
	
	/**
	 * 更新实例信息并同步用例基本信息 包含步骤信息
	 * @param caseInstance
	 */
	public List<Map<String,Object>> updateAndSynInfo(Instance caseInstance,List<InstanceStep> stepList){
		List<Map<String, Object>> steps = this.updateInstanceBasicInfo(caseInstance,stepList);
		Case c = this.updateCaseInfoBySyn(caseInstance);
		//更新步骤信息
		Dao<CaseStep> stepDao = DaoFactory.create(CaseStep.class);
		stepDao.getSession().delete("com.chinacreator.c2.project.testcase.CaseStepMapper.deleteStepByCaseId", c.getId());
		List<CaseStep> list = new ArrayList<CaseStep>();
		for(InstanceStep step:stepList){
			CaseStep caseStep = new CaseStep();
			caseStep.setCaseId(c.getId());
			caseStep.setStep(step.getStep());
			caseStep.setExpectResult(step.getExpectResult());
			caseStep.setNo(step.getNo());
			list.add(caseStep);
		}
		stepDao.insertBatch(list);
		return steps;
	}

	//同步用例信息
	private Case updateCaseInfoBySyn(Instance caseInstance) {
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Dao<Case> caseDao = DaoFactory.create(Case.class);
		Case c = caseDao.selectByID(caseInstance.getCaseId());
		c.setIsCommon(caseInstance.isIsCommon());
		c.setLevel(caseInstance.getLevel());
		c.setName(caseInstance.getName());
		c.setNo(caseInstance.getNo());
		c.setPrecondition(caseInstance.getPrecondition());
		c.setStoryId(caseInstance.getStoryId());
		c.setUserName(currentUserName);
		caseDao.update(c);
		return c;
	}
	
	/**
	 * 搜索满足条件的实例信息
	 * @param name
	 * @param level
	 * @param result
	 * @return
	 */
	public List<String> searchInstanceInfo(String name,String level,String result,String planId){
		Dao<Instance> instanceDao = DaoFactory.create(Instance.class);
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("name", name);
		param.put("level", level);
		param.put("result", result);
		param.put("planId", planId);
		return instanceDao.getSession().selectList("searchInstanceInfo", param);
	}
	
	/**
	 * 获取bug单描述内容
	 * @param instance
	 * @param step
	 * @return
	 */
	public Map<String,Object> getBugSpec(Instance instance,InstanceStep step){
		Map<String,Object> result = new HashMap<String,Object>();
		//获取计划版本号作为bug的版本
		Dao<Plan> planDao = DaoFactory.create(Plan.class);
		Plan plan = planDao.selectByID(instance.getPlanId());
		result.put("version", plan.getVersion());
		result.put("milestone", plan.getMilestoneId());
		Dao<Case> caseDao = DaoFactory.create(Case.class);
		Case c = caseDao.selectByID(instance.getCaseId());
		StringBuffer spec = new StringBuffer();
		spec.append("Test Set:"+c.getName()+"  \n");
		spec.append("Test:"+instance.getName()+"  \n");
		if(step!=null){//测试步骤生成缺陷
			spec.append("Description:  \n"+step.getStep()+"  \n");
			spec.append("Expected:  \n"+(step.getExpectResult()==null?"":step.getExpectResult())+"  \nActual:  \n");
			Object res = getActualStepResult(step);
			spec.append(res!=null?res:"");
		}else{//用例生成缺陷
			getInstanceSpec(instance, spec);
		}
		result.put("spec", spec.toString());
		return result;
	}

	private void getInstanceSpec(Instance instance, StringBuffer spec) {
		Dao<InstanceStep> stepDao = DaoFactory.create(InstanceStep.class);
		List<Map<String,Object>> stepList = stepDao.getSession().selectList("getStepListByInstanceId", instance.getId());
		int i = 1,j = 1;
		spec.append("Description:  \n");
		for(Map<String,Object> st:stepList){
			if(i<stepList.size()){
				spec.append(i+"."+st.get("step")+";  \n");
			}else{
				spec.append(i+"."+st.get("step")+";");
			}
			i++;
		}
		spec.append("  \nExpected:  \n");
		for(Map<String,Object> st:stepList){
			if(st.get("expectResult")!=null&&!st.get("expectResult").equals("")){
				if(j<stepList.size()){
					spec.append(j+"."+st.get("expectResult")+";  \n");
				}else{
					spec.append(j+"."+st.get("expectResult")+";");
				}
				j++;
			}
		}
		spec.append("  \nActual:  \n");
		Object res = getActualInstanceResult(instance);
		spec.append(res!=null?res:"");
	}

	//获取实例实际结果
	private Object getActualInstanceResult(Instance instance) {
		Dao<InstanceResult> resultDao = DaoFactory.create(InstanceResult.class);
		Map<String,Object> result = resultDao.getSession().selectOne("getActualInstanceResult", instance.getId());
		return result!=null?result.get("result"):null;
	}

	//获取测试步骤的实际结果
	private Object getActualStepResult(InstanceStep step) {
		Dao<StepResult> resultDao = DaoFactory.create(StepResult.class);
		Map<String,Object> result = resultDao.getSession().selectOne("getActualStepResult", step.getId());
		return result!=null?result.get("result"):null;
	}
	
	/**
	 * 根据步骤的实际结果自动生成实例结果
	 * @param instanceId
	 * @return
	 */
	public String getInstanceResult(String instanceId){
		Dao<InstanceResult> resultDao = DaoFactory.create(InstanceResult.class);
		Map<String,Object> map = resultDao.getSession().selectOne("getInstanceResultByStep", instanceId);
		return map.get("count")+"";
	}
	
	/**
	 * 获取用例执行情况列表
	 * @param typeId
	 * @param planId
	 * @param pageable
	 * @return
	 */
	public Page<?> getInstanceListByTypeId(Map<String,Object> condition,Pageable pageable){
	  //Dao<Type> typeDao = DaoFactory.create(Type.class);
      //StringBuffer typeIds = new StringBuffer();
      String typeId = condition.get("typeId").toString();
      String planId = condition.get("planId").toString();
      //String state = condition.get("state").toString();
      String name = condition.get("name")==null?"":condition.get("name").toString();
      String level = condition.get("level").toString();
      String result = condition.get("result").toString();
      //getSubType(typeIds,typeId,typeDao,planId);
      Dao<Instance> instanceDao = DaoFactory.create(Instance.class);
      RowBounds4Page page = new RowBounds4Page(pageable, null, null, true) ;
      //String ids = typeIds.append("-1").toString();
      Map<String,Object> params = new HashMap<String,Object>();
      params.put("typeId", typeId);
      params.put("planId", planId);
      //params.put("state", state);
      params.put("name", name);
      params.put("level", level);
      params.put("result", result);
      List<Map<String,Object>> instanceList = instanceDao.getSession().selectList("com.chinacreator.c2.project.plan.InstanceMapper.getInstanceListByTypeId",params, page) ;
      for(Map<String,Object> map:instanceList){
          map.put("count", getTotalCount(map));
          map.put("result", map.get("result")==null?"待测试":map.get("result"));
      }
      return new Page<Map<String,Object>>(pageable.getPageIndex(),pageable.getPageSize(),page.getTotalSize(),instanceList) ;
	}
	
	//执行次数
	private Object getTotalCount(Map<String, Object> map) {
		Dao<InstanceResult> resultDao = DaoFactory.create(InstanceResult.class);
		InstanceResult instanceResult = new InstanceResult();
		instanceResult.setInstanceId((Integer)map.get("id"));
		return resultDao.count(instanceResult);
	}

	/*private void getSubType(StringBuffer typeIds,String id, Dao<Type> typeDao,String planId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id", id);
		params.put("planId", planId);
		List<Type> typeList = typeDao.getSession().selectList("selectSubTypeList", params);
		for(Type p:typeList){
			getSubType(typeIds,p.getId()+"",typeDao,planId);
		}
		typeIds.append(id+",");
	}*/
	
	/**
	 * 删除某实例信息 包含步骤信息、步骤结果、实例结果信息
	 * @param id
	 */
	public void delInstanceInfo(int instanceId){
		Dao<StepResult> stepResultDao = DaoFactory.create(StepResult.class);
		stepResultDao.getSession().delete("deleteStepResultByInstanceId", instanceId);//删除实例步骤结果
		Dao<InstanceStep> instanceStepDao = DaoFactory.create(InstanceStep.class);
		instanceStepDao.getSession().delete("deleteInstanceStepByInstanceId", instanceId);//删除实例步骤
		Dao<InstanceResult> resultDao = DaoFactory.create(InstanceResult.class);
		resultDao.getSession().delete("deleteInstanceResultByInstanceId", instanceId);//删除实例结果
		Dao<Instance> instanceDao = DaoFactory.create(Instance.class);
		instanceDao.getSession().delete("deleteInstanceById", instanceId);//删除实例
	}
	
	/**
	 * 重新导入实例信息  将用例信息同步至实例
	 * @param caseId
	 * @param instanceId
	 */
	public void reImportInstanceInfo(int caseId,int instanceId){
		Dao<StepResult> stepResultDao = DaoFactory.create(StepResult.class);
		stepResultDao.getSession().delete("deleteStepResultByInstanceId", instanceId);//删除实例步骤结果
		Dao<InstanceStep> instanceStepDao = DaoFactory.create(InstanceStep.class);
		instanceStepDao.getSession().delete("deleteInstanceStepByInstanceId", instanceId);//删除实例步骤
		Dao<InstanceResult> resultDao = DaoFactory.create(InstanceResult.class);
		resultDao.getSession().delete("deleteInstanceResultByInstanceId", instanceId);//删除实例结果
		Dao<Case> caseDao = DaoFactory.create(Case.class);
		Dao<Instance> instanceDao = DaoFactory.create(Instance.class);
		Case ca = caseDao.selectByID(caseId);
		Instance instance = instanceDao.selectByID(instanceId);//替换实例基本信息
		instance.setName(ca.getName());
		instance.setIsCommon(ca.isIsCommon());
		instance.setLevel(ca.getLevel());
		instance.setNo(ca.getNo());
		instance.setPrecondition(ca.getPrecondition());
		instance.setStoryId(ca.getStoryId());
		instance.setUserName(ca.getUserName());
		instanceDao.update(instance);
		if(ca.getStoryId()==null){
			instanceDao.getSession().update("com.chinacreator.c2.project.plan.InstanceMapper.updateNull", instance);
		}
		Dao<CaseStep> caseStepDao = DaoFactory.create(CaseStep.class);//替换实例步骤信息
		List<Map<String,Object>> stepList = caseStepDao.getSession().selectList("getStepListById", caseId);
		List<InstanceStep> list = new ArrayList<InstanceStep>();
		for(Map<String,Object> step:stepList){
			InstanceStep caseStep = new InstanceStep();
			caseStep.setInstanceId(instanceId);
			caseStep.setExpectResult(step.get("expectResult")==null?null:step.get("expectResult")+"");
			caseStep.setStep(step.get("step")+"");
			caseStep.setNo((Integer) step.get("no"));
			list.add(caseStep);
		}
		instanceStepDao.insertBatch(list);
	}
}
