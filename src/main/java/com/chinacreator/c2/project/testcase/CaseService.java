package com.chinacreator.c2.project.testcase;

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
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.project.milestone.Milestone;
import com.chinacreator.c2.project.story.Story;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;

public class CaseService {
	
	/**
	 * 移除用例
	 * 用例下的测试步骤以及标签信息同时删除
	 * @param id
	 */
	public void removeCase(int id){
		Dao<CaseStep> stepDao = DaoFactory.create(CaseStep.class);
		stepDao.getSession().delete("deleteStepByCaseId", id);//删除用例下的测试步骤
		Dao<Case> caseDao  = DaoFactory.create(Case.class);
		Case cas = caseDao.selectByID(id);
		caseDao.delete(cas);//删除用例
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("typeId", cas.getTypeId());
		param.put("no", cas.getOrderNo());
		caseDao.getSession().update("updateCaseNoAfterDel", param);
	}
	
	/**
	 * 修改用例名称
	 * @param id
	 * @param name
	 */
	public void editCaseName(int id,String name){
		Dao<Case> caseDao  = DaoFactory.create(Case.class);
		Case cas = caseDao.selectByID(id);
		cas.setName(name);
		caseDao.update(cas);
	}
	
	/**
	 * 查询用例信息
	 * 包含需求、标签、步骤、附件信息
	 * @param id
	 * @param projectId
	 * @return
	 */
	public Map<String,Object> getCaseById(int id,int projectId){
		CaseStepService stepService = new CaseStepService();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Dao<Object> baseDao = DaoFactory.create(Object.class);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id", id);
		params.put("projectId", projectId);
		Map<String,Object> caseMap = baseDao.getSession().selectOne("com.chinacreator.c2.project.testcase.CaseMapper.getCaseById", params);
		resultMap.put("testCase", caseMap);
		List<Map<String,Object>> stepList = stepService.getStepList((Integer) caseMap.get("id"));
		resultMap.put("stepList", stepList);
		return resultMap;
	}
	
	/**
	 * 根据项目查询所有的需求
	 * @param projectId
	 * @return
	 */
	public List<Story> getAllStory(Integer projectId,Integer milestoneId){
		Dao<Story> storyDao = DaoFactory.create(Story.class);
		Story story = new Story();
		Project project = new Project();
		project.setId(projectId);
		story.setProject(project);
		Milestone milestone = new Milestone();
		if(milestoneId!=null){
		  milestone.setId(milestoneId);
		}else{
		  milestone.setId(0);
		}
		story.setMilestone(milestone);
		List<Story> list = storyDao.select(story);//打开或者逾期状态的需求
		for(int i=0;i<list.size();i++){
		  if(list.get(i).getStatus().equals("0")){
		    list.remove(i);
		    i--;
		  }
		}
		return list;
	}
	
	/**
	 * 添加用例基本信息
	 * @param testcase
	 * @param labelList
	 */
	public Case addCaseBasicInfo(Case testcase,List<CaseStep> stepList){
		CaseStepService stepService = new CaseStepService();
		Dao<Case> caseDao  = DaoFactory.create(Case.class);
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		testcase.setUserName(currentUserName);
		testcase.setOrderNo(getMaxNoByTypeId(testcase.getTypeId()));
		caseDao.insert(testcase);
		//删除之前的步骤，重新插入
		if(stepList.size()>0&&stepList!=null){
			stepService.insertBatchStep(testcase, stepList);
		}
		return testcase;
	}

	//获取最大的排序号
	private Integer getMaxNoByTypeId(int typeId) {
		Dao<Case> caseDao = DaoFactory.create(Case.class);
		Map<String,Object> map = caseDao.getSession().selectOne("getMaxNoByTypeId", typeId);
		return map==null?1:(Integer)map.get("no")+1;
	}

	/**
	 * 编辑用例基本信息
	 * @param testcase
	 * @param labelList
	 */
	public List<Map<String,Object>> updateCaseBasicInfo(Case testcase,List<CaseStep> stepList){
		CaseStepService stepService = new CaseStepService();
		Dao<Case> caseDao  = DaoFactory.create(Case.class);
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		testcase.setUserName(currentUserName);
		caseDao.update(testcase);
		if(testcase.getStoryId()==null){
			caseDao.getSession().update("com.chinacreator.c2.project.testcase.CaseMapper.updateNull", testcase);
		}
		//删除之前的步骤，重新插入
		stepService.insertBatchStep(testcase, stepList);
		return stepService.getStepList(testcase.getId());
		
	}
	
	/**
	 * 根据名字和级别模糊查询用例信息
	 * @param name
	 * @param level
	 * @return
	 */
	public List<String> searchCaseInfo(String name,String level,String isCommon,String projectId){
		Dao<Case> caseDao  = DaoFactory.create(Case.class);
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("name", name);
		param.put("level", level);
		param.put("isCommon", isCommon);
		param.put("projectId", projectId);
		List<String> idList= caseDao.getSession().selectList("searchCaseInfo", param);
		return idList;
	}
	
	/**
	 * 查找某分类下的所有用例信息
	 * @param typeId
	 * @param pageable
	 * @return
	 */
	public Page<?> getCaseListByTypeId(Map<String,Object> condition,Pageable pageable){
	  //Dao<Type> typeDao = DaoFactory.create(Type.class);
      //StringBuffer typeIds = new StringBuffer();
      String typeId = condition.get("typeId").toString();
      String projectId = condition.get("projectId").toString();
      //getSubType(typeIds,typeId,typeDao,projectId);
      Dao<Case> caseDao = DaoFactory.create(Case.class);
      RowBounds4Page page = new RowBounds4Page(pageable, null, null, true) ;
      //String ids = typeIds.append("-1").toString();
      Map<String,Object> params = new HashMap<String,Object>();
      params.put("typeId", typeId);
      params.put("projectId", projectId);
      params.put("name", condition.get("name"));
      params.put("level", condition.get("level"));
      List<Map<String,Object>> caseList = caseDao.getSession().selectList("com.chinacreator.c2.project.testcase.CaseMapper.getCaseListByTypeId",params, page) ;
      return new Page<Map<String,Object>>(pageable.getPageIndex(),pageable.getPageSize(),page.getTotalSize(),caseList) ;
	}
	
	/*private void getSubType(StringBuffer typeIds,String id, Dao<Type> typeDao,String projectId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id", id);
		params.put("projectId", projectId);
		List<Type> typeList = typeDao.getSession().selectList("selectSubTypeList", params);
		for(Type p:typeList){
			getSubType(typeIds,p.getId()+"",typeDao,projectId);
		}
		typeIds.append(id+",");
	}*/
	
	/**
	 * 获取所有的公共用例树
	 * @return
	 */
	public List<Map<String,Object>> getAllCommonCase(){
		Dao<Case> caseDao = DaoFactory.create(Case.class);
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		List<Map<String,Object>> idList = caseDao.getSession().selectList("getOwerProjectId", currentUserName);
		StringBuffer ids = new StringBuffer();
		for(Map<String,Object> i:idList){
			ids.append(i.get("id")+",");
		}
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("id", ids.append("-1").toString());
		return caseDao.getSession().selectList("getAllCommonCase",param);
	}
	
	/**
	 * 将选择的公共用例添加至某分类下
	 * @param ids
	 * @param projectId
	 * @param typeId
	 */
	public List<Case> addCommonCase(String ids,String projectId,String typeId){
		List<Case> list = new ArrayList<Case>();
		Dao<Case> caseDao = DaoFactory.create(Case.class);
		Dao<CaseStep> caseStepDao = DaoFactory.create(CaseStep.class);
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("ids", ids);
		List<Case> caseList = caseDao.getSession().selectList("getCommonCaseById", param);
		for(Case c:caseList){
			Case ca = insertCaseInfo(projectId, typeId, c);
			list.add(ca);
			List<Map<String,Object>> stepList = caseStepDao.getSession().selectList("getStepListById", c.getId());
			for(Map<String,Object> step:stepList){
				CaseStep caseStep = new CaseStep();
				caseStep.setCaseId(ca.getId());
				caseStep.setExpectResult(step.get("expectResult")==null?null:step.get("expectResult")+"");
				caseStep.setStep(step.get("step")+"");
				caseStep.setNo(getMaxNoByCaseId(ca.getId()));
				caseStepDao.insert(caseStep);
			}
		}
		return list;
	}
	
	//获取步骤最大的排序号
	private Integer getMaxNoByCaseId(int caseId) {
		Dao<CaseStep> stepDao = DaoFactory.create(CaseStep.class);
		Map<String,Object> map = stepDao.getSession().selectOne("getMaxNoByCaseId", caseId);
		return map==null?1:(Integer)map.get("no")+1;
	}

	private Case insertCaseInfo(String projectId, String typeId, Case c) {
		Dao<Case> caseDao = DaoFactory.create(Case.class);
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Case ca = new Case();
		ca.setIsCommon(false);
		ca.setLevel(c.getLevel());
		ca.setName(c.getName());
		ca.setNo(c.getNo());
		ca.setPrecondition(c.getPrecondition());
		ca.setProjectId(Integer.valueOf(projectId));
		ca.setStoryId(c.getStoryId());
		ca.setTypeId(Integer.valueOf(typeId));
		ca.setUserName(currentUserName);
		ca.setOrderNo(getMaxNoByTypeId(Integer.valueOf(typeId)));
		caseDao.insert(ca);
		return ca;
	}
	
	/**
	 * 用例被拖拽至某分类下时更新排序号
	 * @param caseId
	 * @param typeId
	 */
	public void updateCaseNoByInner(String caseId,String typeId){
		Dao<Case> caseDao = DaoFactory.create(Case.class);
		Case ca = caseDao.selectByID(caseId);
		ca.setTypeId(Integer.valueOf(typeId));
		ca.setOrderNo(getMaxNoByTypeId(Integer.valueOf(typeId)));
		caseDao.update(ca);
	}
	
	/**
	 * 某用例被拖拽至另一用例前或后时
	 * @param ids
	 * @param nos
	 */
	public void updateCaseNoByPrev(String ids,String nos,int typeId){
		String[] caseIds = ids.split(",");
		String[] numbers = nos.split(",");
		Dao<Case> caseDao  = DaoFactory.create(Case.class);
		List<Case> list = new ArrayList<Case>();
		for(int i = 0;i<caseIds.length;i++){
			Case ca = caseDao.selectByID(caseIds[i]);
			if(ca!=null){
				ca.setOrderNo(Integer.valueOf(numbers[i]));
				ca.setTypeId(typeId);
				list.add(ca);
			}
		}
		caseDao.updateBatch(list);
	}
	
	/**
	 * 复制用例
	 * @param caseId
	 * @return
	 */
	public Case copyCaseInfo(String caseId,String typeId){
		Dao<Case> caseDao  = DaoFactory.create(Case.class);
		Case oldcase = caseDao.selectByID(caseId);
		Case testcase = insertCaseInfo(oldcase.getProjectId().toString(), typeId, oldcase);
		Dao<CaseStep> caseStepDao = DaoFactory.create(CaseStep.class);
		List<Map<String,Object>> stepList = caseStepDao.getSession().selectList("getStepListById", caseId);
		List<CaseStep> list = new ArrayList<CaseStep>();
		for(Map<String,Object> step:stepList){//复制用例测试步骤
			CaseStep caseStep = new CaseStep();
			caseStep.setCaseId(testcase.getId());
			caseStep.setExpectResult(step.get("expectResult")==null?null:step.get("expectResult")+"");
			caseStep.setStep(step.get("step")+"");
			caseStep.setNo(Integer.valueOf(step.get("no")+""));
			list.add(caseStep);
		}
		caseStepDao.insertBatch(list);
		return testcase;
	}

}
