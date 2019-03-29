package com.chinacreator.c2.project.testcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;

public class CaseStepService {
	
	/**
	 * 批量插入测试步骤信息
	 * id为0表示新增，其余修改，删除多余的步骤
	 * @param testcase
	 * @param stepList
	 */
	public void insertBatchStep(Case testcase,List<CaseStep> stepList){
		Dao<CaseStep> stepDao = DaoFactory.create(CaseStep.class);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("list", stepList);
		map.put("caseId", testcase.getId());
		stepDao.getSession().delete("deleteStepNoExist", map);
		List<CaseStep> updateList = new ArrayList<CaseStep>();
		List<CaseStep> insertList = new ArrayList<CaseStep>();
		for(int i=0;i<stepList.size();i++){
			if(stepList.get(i).getId().equals(0)){
				CaseStep step = new CaseStep();
				step.setCaseId(testcase.getId());
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
	 * 查询某用例的测试步骤信息
	 * @param testcase
	 * @return
	 */
	public List<Map<String,Object>> getStepList(int caseId){
		Dao<CaseStep> stepDao = DaoFactory.create(CaseStep.class);
		List<Map<String,Object>> stepList = stepDao.getSession().selectList("getStepListById", caseId);
		changeStepInfo(stepList);
		return stepList;
	}
	
	public void deleteBatchStep(int caseId){
		Dao<CaseStep> stepDao = DaoFactory.create(CaseStep.class);
		List<Map<String,Object>> stepList = stepDao.getSession().selectList("getStepListById", caseId);
		stepDao.deleteBatch(stepList);
	}
	
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

}
