package com.chinacreator.c2.appraisals.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.chinacreator.c2.appraisals.AppraisalWitelist;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Conditions;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.dao.mybatis.enhance.Pageable;
import com.chinacreator.c2.dao.mybatis.enhance.Sortable;
import com.chinacreator.c2.web.util.ConditionsUtil;
import com.chinacreator.c2.web.util.SortableUtil;

@Service
public class AppraisalWitelistService {

	public AppraisalWitelist update(AppraisalWitelist entity) {
		//TODO auto-generated method stub	
		DaoFactory.create(AppraisalWitelist.class).update(entity);
		return entity;
	}

	public AppraisalWitelist insert(AppraisalWitelist entity) {
		//TODO auto-generated method stub	
		DaoFactory.create(AppraisalWitelist.class).insert(entity);
		return entity;
	}

	public AppraisalWitelist get(java.lang.String id) {
		//TODO auto-generated method stub
		return DaoFactory.create(AppraisalWitelist.class).selectByID(id);
	}

	public void deleteBatch(List<java.lang.String> ids) {
		//TODO auto-generated method stub
		DaoFactory.create(AppraisalWitelist.class).deleteBatch(ids);
	}

	public void delete(java.lang.String id) {
		//TODO auto-generated method stub
		AppraisalWitelist entity = new AppraisalWitelist();
		entity.setId(id);
		DaoFactory.create(AppraisalWitelist.class).delete(entity);
	}
	
	/**
	 * @return
	 */
	public List<String> queryWhiteListUserIds() {
		List<AppraisalWitelist> allWhiteList = DaoFactory.create(AppraisalWitelist.class).selectAll();
		if(allWhiteList.size()!=0){
			return allWhiteList.stream().map((whiltelist)->{
				return whiltelist.getUserId();
			}).collect(Collectors.toList());
		}
		return new ArrayList<String>();
	}

	public Page<AppraisalWitelist> getListByPage(int page, int rows, String sidx, String sord, String cond) {
		//TODO auto-generated method stub
		//创建分页对象
		Pageable pageable = new Pageable(page, rows);
		//创建排序对象
		Sortable sortable = SortableUtil.getSortable(sidx, sord);
		/*创建高级查询对象*/
		Conditions conditions = ConditionsUtil.getConditions(cond, "filters");
		/*初始化实体对象*/
		AppraisalWitelist entity = StringUtils.isNotBlank(cond) ? JSON.parseObject(cond, AppraisalWitelist.class)
				: new AppraisalWitelist();

		return DaoFactory.create(AppraisalWitelist.class).selectPageByCondition(entity, conditions, pageable, sortable,
				true);

	}
}
