package com.chinacreator.c2.project.plan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Order;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.dao.mybatis.enhance.Pageable;
import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;
import com.chinacreator.c2.dao.mybatis.enhance.Sortable;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;

public class InstanceResultService {

	/**
	 * 获取实例结果列表信息
	 * @param condition
	 * @param pageable
	 * @return
	 */
	public Page<?> getInstanceResultList(String instanceId, Pageable pageable){
		Dao<InstanceResult> resultDao = DaoFactory.create(InstanceResult.class);
		Sortable sb=new Sortable(new Order("ctime",Order.DIRECTION_DESC));
		RowBounds4Page page = new RowBounds4Page(pageable, sb, null, true) ;

		List<Map<String,Object>> resultList = resultDao.getSession().selectList("com.chinacreator.c2.project.plan.InstanceResultMapper.selectResultListByPage",instanceId, page) ;
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
		
		return new Page<Map<String,Object>>(pageable.getPageIndex(),pageable.getPageSize(),page.getTotalSize(),resultList) ;
	}
	
	/**
	 * 添加结果记录信息
	 * @param result
	 */
	public void addResultInfo(Map<String,Object> result){
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Dao<InstanceResult> resultDao = DaoFactory.create(InstanceResult.class);
		InstanceResult res = new InstanceResult();
		res.setInstanceId((Integer)result.get("instanceId"));
		res.setRemark(result.get("remark")!=null?result.get("remark")+"":null);
		res.setResult(result.get("result")+"");
		res.setUserName(currentUserName);
		resultDao.insert(res);
	}
}
