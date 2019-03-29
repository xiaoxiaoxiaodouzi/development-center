package com.chinacreator.c2.project.plan;

import java.lang.reflect.InvocationTargetException;
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

public class PlanService {

	/**
	 * 分页获取计划列表
	 * @param condition
	 * @param pageable
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public Page<?> getPlanList(Map<String,Object> condition, Pageable pageable)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		Dao<Plan> planDao = DaoFactory.create(Plan.class);
		Sortable sb=new Sortable(new Order("ctime",Order.DIRECTION_DESC));
		RowBounds4Page page = new RowBounds4Page(pageable, sb, null, true) ;

		List<Map<String,Object>> planList = planDao.getSession().selectList("com.chinacreator.c2.project.plan.PlanMapper.selectPlanListByPage",condition, page) ;
		
		for(Map<String,Object> map:planList){
			map.put("totalCount", this.getTotalInstanceCount(map));
			map.put("result", "成功");
			map.put("successCount", this.getSuccessInstanceCount(map));
			map.put("result", "失败");
			map.put("failCount", this.getSuccessInstanceCount(map));
			map.put("result", "不可用");
			map.put("invalidCount", this.getSuccessInstanceCount(map));
			map.put("result", "阻塞");
			map.put("zsCount", this.getSuccessInstanceCount(map));
			map.put("result", "待核查");
			map.put("dhcCount", this.getSuccessInstanceCount(map));
			map.put("dcsCount", this.getTestInstanceCount(map));
		}
		
		return new Page<Map<String,Object>>(pageable.getPageIndex(),pageable.getPageSize(),page.getTotalSize(),planList) ;
	}
	
	//待测试数
	private Object getTestInstanceCount(Map<String, Object> map) {
		int sum = Integer.valueOf(map.get("totalCount")+"");
		int result = sum-(Integer.valueOf(map.get("successCount")+"")+Integer.valueOf(map.get("failCount")+"")+
				Integer.valueOf(map.get("invalidCount")+"")+Integer.valueOf(map.get("zsCount")+"")+Integer.valueOf(map.get("dhcCount")+""));
		return result;
	}

	//各状态数
	private Object getSuccessInstanceCount(Map<String, Object> map) {
		Dao<Plan> planDao = DaoFactory.create(Plan.class);
		Map<String,Object> result = planDao.getSession().selectOne("getSuccessInstanceCount",map);
		return result.get("count");
	}

	//实例总数
	private int getTotalInstanceCount(Map<String, Object> map) {
		Dao<Instance> instanceDao = DaoFactory.create(Instance.class);
		Instance instance = new Instance();
		instance.setPlanId((Integer)map.get("id"));
		return instanceDao.count(instance);
	}

	/**
	 * 创建测试计划
	 * @param plan
	 */
	public void addPlan(Plan plan){
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Dao<Plan> planDao = DaoFactory.create(Plan.class);
		if(plan!=null){
			plan.setUserName(currentUserName);
			planDao.insert(plan);
		}
	}
	
	/**
	 * 删除测试计划
	 * @param planId
	 */
	public void delPlanById(String planId){
		Dao<Plan> planDao = DaoFactory.create(Plan.class);
		Plan plan = planDao.selectByID(planId);
		planDao.delete(plan);
	}
	
	/**
	 * 编辑测试计划
	 * @param plan
	 */
	public void editPlan(Plan plan){
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Dao<Plan> planDao = DaoFactory.create(Plan.class);
		if(plan!=null){
			plan.setUserName(currentUserName);
			planDao.update(plan);
		}
	}
	
	/**
	 * 某项目下所有计划的版本号
	 * @param projectId
	 * @return
	 */
	public List<Map<String,Object>> getPlanVersionList(int projectId){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Dao<Plan> dao = DaoFactory.create(Plan.class);
		list = dao.getSession().selectList("getPlanVersionList", projectId);
		return list;
	}
}
