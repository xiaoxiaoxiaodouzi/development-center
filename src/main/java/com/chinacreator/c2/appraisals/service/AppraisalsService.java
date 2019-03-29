package com.chinacreator.c2.appraisals.service;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.chinacreator.asp.comp.sys.oauth2.common.CredentialStore;
import com.chinacreator.c2.appraisals.Appraisals;
import com.chinacreator.c2.appraisals.AppraisalsStatus;
import com.chinacreator.c2.appraisals.Assessor;
import com.chinacreator.c2.appraisals.OrgAssessorManager;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Conditions;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.dao.mybatis.enhance.Pageable;
import com.chinacreator.c2.dao.mybatis.enhance.Rule;
import com.chinacreator.c2.dao.mybatis.enhance.Sortable;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.Organization;
import com.chinacreator.c2.uop.User;
import com.chinacreator.c2.web.ds.impl.DynamicTreeNode;
import com.chinacreator.c2.web.exception.InvalidRestParamException;
import com.google.common.collect.Lists;

@Service
public class AppraisalsService {
	
	@Autowired
	private AppraisalWitelistService whitelistService;

	public Appraisals update(Appraisals entity) {
		//TODO auto-generated method stub	
		DaoFactory.create(Appraisals.class).update(entity);
		return entity;
	}

	public Appraisals insert(Appraisals entity) {
		DaoFactory.create(Appraisals.class).insert(entity);
		return entity;
	}

	public Appraisals get(java.lang.String id) {
		return DaoFactory.create(Appraisals.class).selectByID(id);
	}

	public void deleteBatch(List<java.lang.String> ids) {
		//TODO auto-generated method stub
		DaoFactory.create(Appraisals.class).deleteBatch(ids);
	}

	public void delete(java.lang.String id) {
		//TODO auto-generated method stub
		Appraisals entity = new Appraisals();
		entity.setId(id);
		DaoFactory.create(Appraisals.class).delete(entity);
	}
	
	public List<Appraisals> queryAppraisalUsers( 
			int startYear, int endYear, int startMonth, int endMonth, String status, String orgId, boolean isManagedOrg) throws ParseException {
		if(startYear>endYear) throw new InvalidRestParamException("考核周期日期范围选择错误,截止年份不能比开始年份要小！");
		if(startYear==endYear && endMonth<startMonth) throw new InvalidRestParamException("考核周期日期范围选择错误,截止月份不能比开始年份要小！");
		
		//查询机构下所有在职用户
		List<User> users = OrgUserService.queryUserByOrg("1",orgId);
		// 白名单用户
        List<String> whitelists = whitelistService.queryWhiteListUserIds();
        
		Map<String, User> id2user = users.stream().filter(user->
			!whitelists.contains(user.getId())
		).collect(Collectors.toMap(User::getId,user->user));
		
		List<String> able2AssessUserIds = new ArrayList<String>() ;
		List<String> able2ReviewUserIds = new ArrayList<String>() ;
		SimpleDateFormat format = new SimpleDateFormat("y-M");
		Date startDate = new Date(format.parse(startYear+"-"+startMonth).getTime());
		Date endDate = new Date(format.parse(endYear+"-"+endMonth).getTime());

		String userId = CredentialStore.getCurrCredential().getUserInfo().getId();
		//查询机构下具有考核管理权限的机构及其子机构id,当前用户对具有考核管理权限的机构下的所有用户都具有考核和复核权限
		if(!isManagedOrg){
			List<String> able2ManagedOrgIds = queryAble2ManagedOrgIds(orgId,userId);
			for(User user:users){
				//判断用户是否在可考核管理机构下
				if(!Collections.disjoint(able2ManagedOrgIds, user.getOrgIds())){
					able2AssessUserIds.add(user.getId());
					able2ReviewUserIds.add(user.getId());
				}
			}
			//查询具有考核或者复核权限的用户
			List<Assessor> assessors = DaoFactory.create(Assessor.class)
					.selectByCondition(new Conditions("OR").append(new Rule("assessorId","eq",userId))
							.append(new Rule("reviewerId","eq",userId)), null);
			for (Assessor assessor : assessors) {
				if(id2user.containsKey(assessor.getUserId())){
					if(userId.equals(assessor.getAssessorId())){
						able2AssessUserIds.add(assessor.getUserId());
					}
					if(userId.equals(assessor.getReviewerId())){
						able2ReviewUserIds.add(assessor.getUserId());
					}
				}
			}

			//剔除掉不具有考核和复核权限的用户
			id2user = users.stream().filter(
					user->able2AssessUserIds.contains(user.getId())||able2ReviewUserIds.contains(user.getId()))
			        .collect(Collectors.toMap(User::getId,user->user));
			
			//所属机构下添加自己
			List<Organization> orgs = OrgUserService.queryChildOrgs(orgId, true);
			List<String> orgIds = new ArrayList<String>();
			for(Organization org:orgs){
				orgIds.add(org.getId());
			}
			orgIds.add(orgId);//当前机构的所有子集
			List<Organization> userOrgs = OrgUserService.queryOrgs(userId);//当前用户所在的机构
			long c = userOrgs.stream().filter(org->orgIds.contains(org.getId())).count();
			if(c>0){
				id2user.put(userId, OrgUserService.getUserInfoById(userId));
			}
		}
		
		if(id2user.size()>0){
			Conditions cond = new Conditions("AND").append(new Rule("date","ge",startDate))
					.append(new Rule("date","le",endDate))
			.append(new Rule("userId","in",id2user.keySet().toArray(new String[0])));
			
			if(StringUtils.isNotBlank(status)){
				cond.append(new Rule("status","eq",status));
			}
					
			Dao<Appraisals> dao = DaoFactory.create(Appraisals.class);
			List<Appraisals> result = dao.selectByCondition(cond, null);
			for(Appraisals appraisal:result){
				appraisal.setAssessable(able2AssessUserIds.contains(appraisal.getUserId()));
				appraisal.setReviewable(able2ReviewUserIds.contains(appraisal.getUserId()));
			}
			return result;
		}else{
			return Lists.newArrayList();
		}
		
	}

	/**
	 *  查询考核用户列表 <br/>
	 *  若起止时间范围内,机构下存在用户在某些月份未创建考核记录,则创建之 <br/>
	 *  用户的考核记录起始时间不早于该用户的注册时间(入职时间)
	 * 
	 * @param page
	 * @param rows
	 * @param sidx
	 * @param sord
	 * @param startYear 必填
	 * @param endYear 必填
	 * @param startMonth 必填
	 * @param endMonth 必填
	 * @param status 必填
	 * @param orgId 必填
	 * @return
	 * @throws ParseException 
	 */
	@Transactional
	public Page<Appraisals> getListByPage(int page, int rows, String order, 
			int startYear, int endYear, int startMonth, int endMonth, String status, String orgId, boolean isManagedOrg) throws ParseException {
		if(startYear>endYear) throw new InvalidRestParamException("考核周期日期范围选择错误,截止年份不能比开始年份要小！");
		if(startYear==endYear && endMonth<startMonth) throw new InvalidRestParamException("考核周期日期范围选择错误,截止月份不能比开始年份要小！");
		//创建分页对象
		Pageable pageable = new Pageable(page, rows);
		//创建排序对象
		Sortable sortable = SortableUtil.buildSortable(order);
		//查询机构下所有在职用户
		List<User> users = OrgUserService.queryUserByOrg("1",orgId);
		// 白名单用户
        List<String> whitelists = whitelistService.queryWhiteListUserIds();
		SimpleDateFormat format = new SimpleDateFormat("y-M");
		Date startDate = new Date(format.parse(startYear+"-"+startMonth).getTime());
		Date endDate = new Date(format.parse(endYear+"-"+endMonth).getTime());
        
		Map<String, User> id2user = users.stream().filter(user->
			!whitelists.contains(user.getId())
		).collect(Collectors.toMap(User::getId,user->user));
		
		List<String> able2AssessUserIds = new ArrayList<String>() ;
		List<String> able2ReviewUserIds = new ArrayList<String>() ;
		String userId = CredentialStore.getCurrCredential().getUserInfo().getId();
		//查询机构下具有考核管理权限的机构及其子机构id,当前用户对具有考核管理权限的机构下的所有用户都具有考核和复核权限
		if(isManagedOrg){
			able2AssessUserIds.addAll(id2user.keySet());
			able2ReviewUserIds.addAll(id2user.keySet());
		}else{
			List<String> able2ManagedOrgIds = queryAble2ManagedOrgIds(orgId,userId);
			for(User user:users){
				//判断用户是否在可考核管理机构下
				if(!Collections.disjoint(able2ManagedOrgIds, user.getOrgIds())){
					able2AssessUserIds.add(user.getId());
					able2ReviewUserIds.add(user.getId());
				}
			}
			//查询具有考核或者复核权限的用户
			List<Assessor> assessors = DaoFactory.create(Assessor.class)
					.selectByCondition(new Conditions("OR").append(new Rule("assessorId","eq",userId))
							.append(new Rule("reviewerId","eq",userId)), null);
			for (Assessor assessor : assessors) {
				if(id2user.containsKey(assessor.getUserId())){
					if(userId.equals(assessor.getAssessorId())){
						able2AssessUserIds.add(assessor.getUserId());
					}
					if(userId.equals(assessor.getReviewerId())){
						able2ReviewUserIds.add(assessor.getUserId());
					}
				}
			}

			//剔除掉不具有考核和复核权限的用户
			id2user = users.stream().filter(
					user->able2AssessUserIds.contains(user.getId())||able2ReviewUserIds.contains(user.getId()))
			        .collect(Collectors.toMap(User::getId,user->user));
			
			//所属机构下添加自己
			List<Organization> orgs = OrgUserService.queryChildOrgs(orgId, true);
			List<String> orgIds = new ArrayList<String>();
			for(Organization org:orgs){
				orgIds.add(org.getId());
			}
			orgIds.add(orgId);//当前机构的所有子集
			List<Organization> userOrgs = OrgUserService.queryOrgs(userId);//当前用户所在的机构
			long c = userOrgs.stream().filter(org->orgIds.contains(org.getId())).count();
			if(c>0){
				id2user.put(userId, OrgUserService.getUserInfoById(userId));
			}
		}
		
		if(id2user.size()>0){
			/*创建高级查询对象*/
			Conditions cond = new Conditions("AND").append(new Rule("date","ge",startDate))
					.append(new Rule("date","le",endDate))
			.append(new Rule("userId","in",id2user.keySet().toArray(new String[0])));
					
			Dao<Appraisals> dao = DaoFactory.create(Appraisals.class);
			List<Appraisals> results = dao.selectByCondition(cond, null);
			//查询起止时间范围内的所有年月记录,结果格式：["2018-1","2018-2"]
			List<String> betweenMonths = getMonth(startYear,startMonth,endYear,endMonth);
			//通过数据库已有的考核记录结合查询起止时间和用户的入职时间,整理出用户需要创建的考核记录
			List<Appraisals> addList = checkIfNeedAddAppraisals(results,betweenMonths,id2user);
			if(addList.size()!=0){
				dao.insertBatch(addList);
			}
			cond = new Conditions("AND").append(new Rule("date","ge",startDate))
					.append(new Rule("date","le",endDate))
			//.append(new Rule("status","eq",status))
			.append(new Rule("userId","in",id2user.keySet().toArray(new String[0])));
			
			if(StringUtils.isNotBlank(status)){
				cond.append(new Rule("status","eq",status));
			}
			
			Page<Appraisals> result = dao.selectPageByCondition(new Appraisals(), cond, pageable, sortable, true);
			for(Appraisals appraisal:result.getContents()){
				appraisal.setAssessable(able2AssessUserIds.contains(appraisal.getUserId()));
				appraisal.setReviewable(able2ReviewUserIds.contains(appraisal.getUserId()));
			}
			return result;
		}else{
			return new Page<>(pageable, Lists.newArrayList());
		}
		
	}
	
	private List<String> queryAble2ManagedOrgIds(String orgId,String userId) {
		List<String> managedOrgIds = new ArrayList<String>();
		// 检查所有子机构中，是否存在当前用户具有管理权限的机构
		List<Organization> childOrgs = OrgUserService.queryChildOrgs(orgId, true);
		List<String> subOrgIds = new ArrayList<String>();
		if(childOrgs.size()!=0){
			subOrgIds = childOrgs.stream().map((org->{
				return org.getId();
			})).collect(Collectors.toList());
		}
		subOrgIds.add(orgId);
		
		List<OrgAssessorManager> orgManagers = DaoFactory.create(OrgAssessorManager.class)
				.selectByCondition(new Conditions("AND").append(new Rule("managedOrgId","in",subOrgIds)).append(new Rule("userId","eq",userId)), null);
		if(orgManagers.size()!=0){
			for(OrgAssessorManager manage: orgManagers){
				managedOrgIds.add(manage.getManagedOrgId());
			}
		}
		return managedOrgIds;
	}

	private List<Appraisals> checkIfNeedAddAppraisals(List<Appraisals> contents, List<String> betweenMonths,
			Map<String, User> id2user) throws ParseException {
		List<Appraisals> addList = new ArrayList<Appraisals>();
		Map<String,Map<String,Appraisals>> userId2Date2Obj = new HashMap<String,Map<String,Appraisals>>();
		for (Entry<String, User> entries:id2user.entrySet()) {
			Map<String,Appraisals> date2Obj = new HashMap<String,Appraisals>();
			userId2Date2Obj.put(entries.getKey(), date2Obj);
		}
		
		for (Appraisals appraisals : contents) {
			userId2Date2Obj.get(appraisals.getUserId()).put(appraisals.getYear()+"-"+appraisals.getMonth(), appraisals);
		}
		// 遍历用户数据,判断用户在所有时间节点(年月)中是否包含考核记录,若没有且用户在该时间已经入职,则创建
		for (Entry<String, Map<String, Appraisals>> entries : userId2Date2Obj.entrySet()) {
			for (String dateStr : betweenMonths) {
				if(!entries.getValue().containsKey(dateStr)){
					String[] split = dateStr.split("-");
					int year = Integer.parseInt(split[0]);
					int month = Integer.parseInt(split[1]);
					User user = id2user.get(entries.getKey());
					if(user.containsKey("regdate")){
						Calendar regdate = Calendar.getInstance();
						regdate.setTimeInMillis(Long.parseLong(user.get("regdate").toString()));
						int regisYear = regdate.get(Calendar.YEAR);
						int regisMonth = regdate.get(Calendar.MONTH)+1;
						//如果入职时间要晚于查询区间,则不生成考核记录
						if(regisYear>year || (regisYear==year && regisMonth>month)){
							continue;
						}
					}
					String userName = id2user.get(entries.getKey()).getEmail().substring(0, id2user.get(entries.getKey()).getEmail().indexOf("@"));
					addList.add(initAppraisals(entries.getKey(),id2user.get(entries.getKey()).getName() ,userName,year, month));
				}
			}
		}
		return addList;
	}
	
	private Appraisals initAppraisals(String userId, String name,String userName, int year, int month) throws ParseException {
		Appraisals app = new Appraisals();
		app.setYear(year);
		app.setMonth(month);
		app.setUserId(userId);
		app.setUserRealname(name);
		app.setUserName(userName);
		SimpleDateFormat format = new SimpleDateFormat("y-M");
		app.setDate(new Date(format.parse(year+"-"+month).getTime()));
		app.setStatus(AppraisalsStatus.待考核.toString());
		return app;
	}

	private List<String> getMonth(int startYear, int startMonth, int endYear, int endMonth) {
		int year = endYear - startYear;
		List<String> results = new ArrayList<String>();
		if(year==0){
			int month = endMonth-startMonth;
			for (int i = 0; i <= month; i++) {
				results.add(endYear+"-"+(startMonth+i));
			}
		}else{
			// startYear-startMonth到12月的时间范围
			for (int i = startMonth; i <= 12; i++) {
				results.add(startYear+"-"+i);
			}
			// 计算startYear-endYear之间年份的时间范围
			for (int i = 1; i < year; i++) {
				for (int j = 1; j <= 12; j++) {
					results.add(startYear+i+"-"+j);
				}
			}
			// 计算endYear的1月到endMonth的时间范围
			for (int i = 1; i <= endMonth; i++) {
				results.add(endYear+"-"+i);
			}
		}
		return results;
	}

	public List<DynamicTreeNode> getAppraisalsOrgs() {
		String userId = CredentialStore.getCurrCredential().getUserInfo().getId();
		List<DynamicTreeNode> orgs = OrgUserService.queryOrgsByUserName(userId);
		OrgAssessorManagerService service = new OrgAssessorManagerService();
		Map<String, String> map = new HashMap<String, String>();
		map.put("userId", userId);
		Object cond = JSONObject.toJSON(map);
		List<OrgAssessorManager> orgManages = service.getListByPage(1, 9999, null, null, cond.toString()).getContents();
		for(DynamicTreeNode org:orgs){//父机构具有管理权限，子机构也具有管理权限
			for(OrgAssessorManager om:orgManages){
				if(org.getId().equals(om.getManagedOrgId())){
					org.put("isManagedOrg", true);
				}
			}
		}
		for(DynamicTreeNode org:orgs){
			org.put("isManagedOrg", org.get("isManagedOrg")==null?false:true);
		}
		return orgs;
	}

	public Page<Appraisals> qeuryAppraisalsByUserIds(int page, int rows, String order, int startYear, int endYear,
			int startMonth, int endMonth, String status, String orgId, boolean isManagedOrg, List<String> userIds) throws ParseException {
		
		if(startYear>endYear) throw new InvalidRestParamException("考核周期日期范围选择错误,截止年份不能比开始年份要小！");
		if(startYear==endYear && endMonth<startMonth) throw new InvalidRestParamException("考核周期日期范围选择错误,截止月份不能比开始年份要小！");
		//创建分页对象
		Pageable pageable = new Pageable(page, rows);
		//创建排序对象
		Sortable sortable = SortableUtil.buildSortable(order);
		//查询机构下所有在职用户
		List<User> users = OrgUserService.queryUserByOrg("1",orgId);
		SimpleDateFormat format = new SimpleDateFormat("y-M");
		Date startDate = new Date(format.parse(startYear+"-"+startMonth).getTime());
		Date endDate = new Date(format.parse(endYear+"-"+endMonth).getTime());
        
		Map<String, User> id2user = users.stream().filter(user->
			userIds.contains(user.getId())
		).collect(Collectors.toMap(User::getId,user->user));
		
		List<String> able2AssessUserIds = new ArrayList<String>() ;
		List<String> able2ReviewUserIds = new ArrayList<String>() ;
		String userId = CredentialStore.getCurrCredential().getUserInfo().getId();
		//查询机构下具有考核管理权限的机构及其子机构id,当前用户对具有考核管理权限的机构下的所有用户都具有考核和复核权限
		if(isManagedOrg){
			able2AssessUserIds.addAll(id2user.keySet());
			able2ReviewUserIds.addAll(id2user.keySet());
		}else{
			List<String> able2ManagedOrgIds = queryAble2ManagedOrgIds(orgId,userId);
			for(User user:users){
				//判断用户是否在可考核管理机构下
				if(!Collections.disjoint(able2ManagedOrgIds, user.getOrgIds())){
					able2AssessUserIds.addAll(id2user.keySet());
					able2ReviewUserIds.addAll(id2user.keySet());
				}
			}
			
			//查询具有考核或者复核权限的用户
			List<Assessor> assessors = DaoFactory.create(Assessor.class)
					.selectByCondition(new Conditions("OR").append(new Rule("assessorId","eq",userId))
							.append(new Rule("reviewerId","eq",userId)), null);
			for (Assessor assessor : assessors) {
				if(id2user.containsKey(assessor.getUserId())){
					if(userId.equals(assessor.getAssessorId())){
						able2AssessUserIds.add(assessor.getUserId());
					}
					if(userId.equals(assessor.getReviewerId())){
						able2ReviewUserIds.add(assessor.getUserId());
					}
				}
			}
			
		}
		
		if(id2user.size()>0){
			/*创建高级查询对象*/
			Conditions cond = new Conditions("AND").append(new Rule("date","ge",startDate))
					.append(new Rule("date","le",endDate))
			//.append(new Rule("status","eq",status))
			.append(new Rule("userId","in",id2user.keySet().toArray(new String[0])));
			
			if(StringUtils.isNotBlank(status)){
				cond.append(new Rule("status","eq",status));
			}
					
			Dao<Appraisals> dao = DaoFactory.create(Appraisals.class);
			Page<Appraisals> result = dao.selectPageByCondition(new Appraisals(), cond, pageable, sortable, true);
			for(Appraisals appraisal:result.getContents()){
				appraisal.setAssessable(able2AssessUserIds.contains(appraisal.getUserId()));
				appraisal.setReviewable(able2ReviewUserIds.contains(appraisal.getUserId()));
			}
			return result;	
		}else{
			return new Page<>(pageable, Lists.newArrayList());
		}
		
	}

}
