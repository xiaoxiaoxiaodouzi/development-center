package com.chinacreator.c2.appraisals.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.chinacreator.c2.appraisals.Assessor;
import com.chinacreator.c2.appraisals.OrgAssessorManager;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.dao.mybatis.enhance.Rule;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;
import com.chinacreator.c2.web.ds.impl.DynamicTreeNode;
import com.google.common.collect.Lists;

@Service
public class AssessorService {

	@Autowired
	private AppraisalWitelistService whitelistService;
	
	public Assessor update(Assessor entity) {
		//TODO auto-generated method stub	
		DaoFactory.create(Assessor.class).update(entity);
		return entity;
	}

	public Assessor insert(Assessor entity) {
		//TODO auto-generated method stub	
		DaoFactory.create(Assessor.class).insert(entity);
		return entity;
	}

	public Assessor get(java.lang.String id) {
		//TODO auto-generated method stub
		return DaoFactory.create(Assessor.class).selectByID(id);
	}
	
	/**
	 * 查询某用户的考核人与复核人信息
	 * 机构负责人也拥有考核和复核权限
	 * @param userId
	 * @return
	 */
	public List<Assessor> getAssessorByuserId(java.lang.String userId){
		Assessor assessor=new Assessor();
		assessor.setUserId(userId);
		List<Assessor> list = DaoFactory.create(Assessor.class).select(assessor);
		AppraisalsService service = new AppraisalsService();
		List<DynamicTreeNode> orgs = service.getAppraisalsOrgs();//当前登录者考核负责机构
		Map<String,Object> filters = new HashMap<String,Object>();
		filters.put("groupOp", "OR");
		List<Rule> rules = Lists.newArrayList();
		for(DynamicTreeNode org:orgs){//父机构具有管理权限，子机构也具有管理权限
		  if((boolean) org.get("isManagedOrg")){
		    Rule rule = new Rule("managedOrgId", "eq", org.getId());
		    rules.add(rule);
		  }
		}
		if(rules.size()>0){
          filters.put("rules", rules);
          OrgAssessorManagerService maService = new OrgAssessorManagerService();
          Map<String, Object> map = new HashMap<String, Object>();
    
          map.put("filters", filters);
          Object cond = JSONObject.toJSON(map);
          List<OrgAssessorManager> orgManages =
              maService.getListByPage(1, 9999, null, null, cond.toString()).getContents();
          for (OrgAssessorManager ma : orgManages) {
            assessor = new Assessor();
            assessor.setUserId(userId);
            assessor.setAssessorId(ma.getUserId());
            assessor.setAssessorName(ma.getUserName());
            assessor.setAssessorRealname(ma.getUserRealname());
            assessor.setReviewerId(ma.getUserId());
            assessor.setReviewerName(ma.getUserName());
            assessor.setReviewerRealname(ma.getUserRealname());
            list.add(assessor);
          }
		}
		return list;
	}

	public void deleteBatch(List<java.lang.String> ids) {
		//TODO auto-generated method stub
		DaoFactory.create(Assessor.class).deleteBatch(ids);
	}

	public void delete(java.lang.String id) {
		//TODO auto-generated method stub
		Assessor entity = new Assessor();
		entity.setId(id);
		DaoFactory.create(Assessor.class).delete(entity);
	}

	public Page<Assessor> getListByPage(int page, int rows, String orgId,String name) {
		List<User> deptUsers = getDeptUsers(orgId);
		List<String> whitelists = whitelistService.queryWhiteListUserIds();//考核白名单
		
		for(int i=0;i<deptUsers.size();i++){
			for(String white:whitelists){
				if(deptUsers.get(i).getId().contains(white)){
					deptUsers.remove(i);
					i--;
				}
			}
			
		}
		
		if(StringUtils.isNotBlank(name)){
			for(int i=0;i<deptUsers.size();i++){
				if(!deptUsers.get(i).getName().contains(name)){
					deptUsers.remove(i);
					i--;
				}
			}
		}
		List<User> users = OrgUserService.spiltPage(deptUsers, page, rows).getContents();
		List<Assessor> result = Lists.newArrayList();
		if(users!=null && users.size()>0){
			List<Assessor> assessor = DaoFactory.create(Assessor.class).getSession().selectList("getAssessorByNames", users);
			for(User user:users){
				Assessor a = new Assessor();
				for(Assessor ass:assessor){
					if(user.get("id").equals(ass.getUserId())){
						a = ass;
						break;
					}
				}
				if(a.getId()==null){
					a.setUserId(user.get("id").toString());
					a.setUserName(user.getEmail().substring(0, user.getEmail().indexOf("@chinacreator.com")));
					a.setUserRealname(user.getName());
				}
				result.add(a);
			}
		}
		//分页处理
		return new Page<Assessor>(page, rows, deptUsers.size(), result);
	}

	public List<User> getDeptUsers(String orgId) {
		List<User> result = Lists.newArrayList();
		List<User> users = OrgUserService.queryOrgUsers(orgId,"1", true);
		for(User u:users){
			if(u.getEmail()!=null && u.getEmail().contains("@")){
				u.put("userName", u.getEmail().substring(0, u.getEmail().indexOf("@")));
				result.add(u);
			}
		}
		return result;
	}
}
