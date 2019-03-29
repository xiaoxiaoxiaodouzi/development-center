package com.chinacreator.c2.project.testcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;

public class TypeService {

	/**
	 * 获取某项目下的用例树
	 * @param condition
	 * @return
	 */
	public List<Object> getCaseTypeById(Map<String,Object> condition){
		Dao<Object> ObjectDao = DaoFactory.create(Object.class);
		List<Object> list = ObjectDao.getSession().selectList("com.chinacreator.c2.project.testcase.TypeMapper.getCaseTypeById", condition);
		return list;
	}
	
	/**
	 * 新增用例分类
	 * @param pid
	 * @param projectId
	 * @return
	 */
	public Type addCaseType(int pid,int projectId){
		Dao<Type> typeDao = DaoFactory.create(Type.class);
		Type type = new Type();
		type.setPid(pid);
		type.setProjectId(projectId);
		type.setName("new type");
		type.setNo(getMaxNoByProjectId(projectId));
		typeDao.insert(type);
		return type;
	}
	
	//获取某项目下最大的分类排序号
	private Integer getMaxNoByProjectId(int projectId) {
		Dao<Type> typeDao = DaoFactory.create(Type.class);
		Map<String,Object> map = typeDao.getSession().selectOne("getMaxNoByProjectId", projectId);
		return map==null?1:(Integer)map.get("no")+1;
	}

	/**
	 * 修改分类名称
	 * @param id
	 * @param name
	 */
	public void editCaseType(int id,String name){
		Dao<Type> typeDao  = DaoFactory.create(Type.class);
		Type type = typeDao.selectByID(id);
		type.setName(name);
		typeDao.update(type);
	}
	
	/**
	 * 移除用例分类
	 * 该分类下可能拥有子分类以及用例信息
	 * @param id
	 */
	public void removeCaseType(int id){
		Dao<Type> typeDao  = DaoFactory.create(Type.class);
		//递归删除子分类
		deleteSubType(id, typeDao);
	}

	private void deleteSubType(int id, Dao<Type> typeDao) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id", id);
		List<Type> typeList = typeDao.getSession().selectList("selectSubTypeList", params);
		CaseService caseService = new CaseService();
		for(Type p:typeList){
			deleteSubType(p.getId(),typeDao);
		}
		//删除用例
		List<Map<String,Object>> list = typeDao.getSession().selectList("getCaseListById", params);
		for(Map<String,Object> map:list){
			caseService.removeCase((Integer)map.get("id"));
		}
		Type type = typeDao.selectByID(id);
		typeDao.delete(type);
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("no", type.getNo());
		param.put("projectId", type.getProjectId());
		typeDao.getSession().update("updateTypeNoAfterDel", param);
	}
	
	/**
	 * 某分类被拖拽至另一分类下时
	 * @param typeId
	 * @param pid
	 */
	public void updateTypeNoByInner(int typeId,int pid){
		Dao<Type> typeDao  = DaoFactory.create(Type.class);
		Type type = typeDao.selectByID(typeId);
		type.setPid(pid);
		typeDao.update(type);
	}
	
	/**
	 * 某分类被拖拽至另一分类前或后时
	 * @param ids
	 * @param nos
	 */
	public void updateTypeNoByPrev(String ids,String nos,int pid){
		String[] typeIds = ids.split(",");
		String[] numbers = nos.split(",");
		Dao<Type> typeDao  = DaoFactory.create(Type.class);
		List<Type> list = new ArrayList<Type>();
		for(int i = 0;i<typeIds.length;i++){
			Type type = typeDao.selectByID(typeIds[i]);
			type.setNo(Integer.valueOf(numbers[i]));
			type.setPid(pid);
			list.add(type);
		}
		typeDao.updateBatch(list);
	}
	
	/**
	 * 某计划下的所有分类树
	 * @param projectId
	 * @param planId
	 * @return
	 */
	public List<Map<String,Object>> getTypeListByProject(String projectId,String planId){
		Dao<Type> typeDao  = DaoFactory.create(Type.class);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("projectId", projectId);
		map.put("planId", planId);
		List<Map<String,Object>> list = typeDao.getSession().selectList("getTypeListByProject", map);
		return list;
	}
	
}
