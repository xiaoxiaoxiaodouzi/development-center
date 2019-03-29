package com.chinacreator.c2.project.plan;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;

public class StepResultService {

	/**
	 * 添加测试步骤实际结果
	 * @param result
	 */
	public Map<String,Object> saveStepResult(Map<String,Object> result){
		Dao<StepResult> resultDao = DaoFactory.create(StepResult.class);
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		StepResult step = new StepResult();
		step.setInstanceId((Integer)result.get("instanceId"));
		step.setStepId((Integer)result.get("id"));
		step.setResult(result.get("result")+"");
		step.setRemark(result.get("remark")==null?null:result.get("remark")+"");
		step.setUserName(currentUserName);
		resultDao.insert(step);
		List<String> remarkArr = new ArrayList<String>();
		String[] results = result.get("remark")==null?null:(result.get("remark")+"").split("\n");
		if(results!=null){
			for(String s:results){
				remarkArr.add(s);
			}
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("result", step.getResult());
		map.put("userName", currentUserName);
		map.put("ctime", new Timestamp(System.currentTimeMillis()));
		map.put("remarkArr", remarkArr);
		map.put("remark", step.getRemark());
		return map;
	}
	
}
