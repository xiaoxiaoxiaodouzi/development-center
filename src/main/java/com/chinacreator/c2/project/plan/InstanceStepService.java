package com.chinacreator.c2.project.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;

public class InstanceStepService {

	private void changeStepInfo(List<Map<String, Object>> stepList) {
		for(Map<String,Object> x:stepList){
			List<String> testStepArr = new ArrayList<String>();
			String[] steps = (x.get("step")+"").split("\n");
			for(String s:steps){
				testStepArr.add(s);
			}
			List<String> expectResultArr = new ArrayList<String>();
			String[] results = x.get("expectResult")==null?null:(x.get("expectResult")+"").split("\n");
			if(results!=null){
				for(String s:results){
					expectResultArr.add(s);
				}
			}
			x.put("testStepArr", testStepArr);
			x.put("expectResultArr", expectResultArr);
		}
	}

	private void getStepResultList(List<Map<String, Object>> stepList) {
		Dao<StepResult> resultDao = DaoFactory.create(StepResult.class);
		//每步测试结果包含的实际结果集
		for(Map<String,Object> step:stepList){
			List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
			resultList = resultDao.getSession().selectList("getStepResultListByStepId", step.get("id"));
			step.put("resultList", resultList);
			for(Map<String,Object> x:resultList){
				List<String> remarkArr = new ArrayList<String>();
				String[] results = x.get("remark")==null?null:(x.get("remark")+"").split("\n");
				if(results!=null){
					for(String s:results){
						remarkArr.add(s);
					}
				}
				x.put("remarkArr", remarkArr);
			}
			step.put("result", resultList.size()>0?resultList.get(0).get("result"):"");
		}
	}
	
	/**
	 * 批量插入步骤信息
	 * id为0表示插入，其余为更新，不存在的删除
	 * @param caseInstance
	 * @param stepList
	 */
	public void insertBatchStep(Instance caseInstance,List<InstanceStep> stepList) {
		Dao<InstanceStep> stepDao = DaoFactory.create(InstanceStep.class);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("list", stepList);
		map.put("id", caseInstance.getId());
		stepDao.getSession().delete("deleteInstanceStepNoExist", map);
		List<InstanceStep> updateList = new ArrayList<InstanceStep>();
		List<InstanceStep> insertList = new ArrayList<InstanceStep>();
		for(int i=0;i<stepList.size();i++){
			if(stepList.get(i).getId()<=0){
				InstanceStep step = new InstanceStep();
				step.setInstanceId(caseInstance.getId());
				step.setExpectResult(stepList.get(i).getExpectResult());
				step.setStep(stepList.get(i).getStep());
				step.setNo((i+1));
				insertList.add(step);
			}else{
				stepList.get(i).setExpectResult(stepList.get(i).getExpectResult());
				stepList.get(i).setStep(stepList.get(i).getStep());
				stepList.get(i).setNo((i+1));
				updateList.add(stepList.get(i));
			}
		}
		if(updateList.size()>0) stepDao.updateBatch(updateList);
		if(insertList.size()>0) stepDao.insertBatch(insertList);
	}

	/**
	 * 查询某实例下的步骤信息 包含步骤结果
	 * @param id
	 * @return
	 */
	public List<Map<String,Object>> getStepList(Integer id) {
		Dao<InstanceStep> stepDao = DaoFactory.create(InstanceStep.class);
		List<Map<String,Object>> stepList = stepDao.getSession().selectList("getStepListByInstanceId", id);
		getStepResultList(stepList);
		changeStepInfo(stepList);
		return stepList;
	}
}
