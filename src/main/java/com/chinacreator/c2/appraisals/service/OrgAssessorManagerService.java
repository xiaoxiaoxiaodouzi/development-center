package com.chinacreator.c2.appraisals.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
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
import com.chinacreator.c2.web.util.ConditionsUtil;
import com.chinacreator.c2.web.util.SortableUtil;
import com.google.common.collect.Lists;

@Service
public class OrgAssessorManagerService {

	public OrgAssessorManager update(OrgAssessorManager entity) {
		//TODO auto-generated method stub	
		DaoFactory.create(OrgAssessorManager.class).update(entity);
		return entity;
	}

	@Transactional
	public List<OrgAssessorManager> insert(List<OrgAssessorManager> entity) {
		//TODO auto-generated method stub	
		//移除用户的考核复核设置
		removeAssessor(entity);
		//移除子机构的管理考核人
		removeChildOrgManage(entity);
		DaoFactory.create(OrgAssessorManager.class).insertBatch(entity);
		return entity;
	}

	private void removeChildOrgManage(List<OrgAssessorManager> entity) {
		String pid = entity.get(0).getManagedOrgId();
		List<Organization> childOrgs = OrgUserService.queryChildOrgs(pid, true);//所有子机构
		Dao<OrgAssessorManager> dao = DaoFactory.create(OrgAssessorManager.class);
		List<String> orgIds = new ArrayList<String>() ;
		orgIds.add(pid);
		if(childOrgs.size()!=0){
			for (Organization org: childOrgs) {
				orgIds.add(org.getId());
			}
		}
		List<String> userIds = entity.stream().map((manager)->{
			return manager.getUserId();
		}).collect(Collectors.toList());
		
		Conditions cond = new Conditions("AND").append(new Rule("userId","in",userIds)).append(new Rule("managedOrgId","in",orgIds));
		List<OrgAssessorManager> list = dao.selectByCondition(cond, null);
		if(list.size()!=0)
			dao.deleteBatch(list);
	}

	private void removeAssessor(List<OrgAssessorManager> entity) {
		Conditions cond = new Conditions("OR");
		List<Rule> rules = Lists.newArrayList();
		for(OrgAssessorManager ma:entity){
			Rule rule = new Rule("assessorId","eq",ma.getUserId());
			rules.add(rule);
			rule = new Rule("reviewerId","eq",ma.getUserId());
			rules.add(rule);
		}
		cond.setRules(rules);
		List<Assessor> list = DaoFactory.create(Assessor.class).selectByCondition(cond, null);
		if(list.size()>0){
			for(Assessor ass:list){
				for(OrgAssessorManager ma:entity){
					if(ma.getUserId().equals(ass.getAssessorId())){
						ass.setAssessorId("");
						ass.setAssessorName("");
						ass.setAssessorRealname("");
					}
					if(ma.getUserId().equals(ass.getReviewerId())){
						ass.setReviewerId("");
						ass.setReviewerName("");
						ass.setReviewerRealname("");
					}
				}
			}
			DaoFactory.create(Assessor.class).updateBatch(list);
		}
	}

	public OrgAssessorManager get(java.lang.String id) {
		//TODO auto-generated method stub
		return DaoFactory.create(OrgAssessorManager.class).selectByID(id);
	}

	public void deleteBatch(List<java.lang.String> ids) {
		//TODO auto-generated method stub
		DaoFactory.create(OrgAssessorManager.class).deleteBatch(ids);
	}

	public void delete(java.lang.String id) {
		//TODO auto-generated method stub
		OrgAssessorManager entity = new OrgAssessorManager();
		entity.setId(id);
		DaoFactory.create(OrgAssessorManager.class).delete(entity);
	}

	public Page<OrgAssessorManager> getListByPage(int page, int rows, String sidx, String sord, String cond) {
		//TODO auto-generated method stub
		//创建分页对象
		Pageable pageable = new Pageable(page, rows);
		//创建排序对象
		Sortable sortable = SortableUtil.getSortable(sidx, sord);
		/*创建高级查询对象*/
		Conditions conditions = ConditionsUtil.getConditions(cond, "filters");
		/*初始化实体对象*/
		OrgAssessorManager entity = StringUtils.isNotBlank(cond) ? JSON.parseObject(cond, OrgAssessorManager.class)
				: new OrgAssessorManager();

		return DaoFactory.create(OrgAssessorManager.class).selectPageByCondition(entity, conditions, pageable, sortable,
				true);

	}


	@Transactional
	public int updateBatchByOrgId(String orgId, List<OrgAssessorManager> list) {
		//移除用户的考核复核设置
		removeAssessor(list);
		//移除子机构的管理考核人
		removeChildOrgManage(list);
		Dao<OrgAssessorManager> dao = DaoFactory.create(OrgAssessorManager.class);
		OrgAssessorManager entity = new OrgAssessorManager();
		entity.setManagedOrgId(orgId);
		List<OrgAssessorManager> tempList = dao.select(entity);
		dao.deleteBatch(tempList);
		//插入新的管理数据
		return dao.insertBatch(list);
	}
}
