package com.chinacreator.c2.support;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chinacreator.asp.comp.sys.oauth2.common.CredentialStore;
import com.chinacreator.c2.config.ConfigManager;
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
import com.chinacreator.c2.uop.Organization;
import com.chinacreator.c2.uop.User;
import com.chinacreator.c2.web.ds.impl.DynamicTreeNode;

public class SupportService {
	private static List<DynamicTreeNode> chinacreatorDepartment;
	
	public Page<Map<String,Object>> supportList(Map<String,Object> search,Pageable pageable){
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		
		search.put("username", currentUserName);
		Dao<Support> dao = DaoFactory.create(Support.class);
		RowBounds4Page page = new RowBounds4Page(pageable, null, null, true) ;
		List<Map<String,Object>> list = dao.getSession().selectList("supportList",search, page);
		
		for(Map<String,Object> o:list){
			Organization org = OrgUserService.getOrgInfoById(o.get("orgId").toString());
			o.put("fromOrg", org.getName());
			Organization doOrg = OrgUserService.getOrgInfoById(o.get("doOrgId").toString());
			o.put("toOrg", doOrg.getName());
		}
		
		Page<Map<String,Object>> supportList = new Page<Map<String,Object>>(pageable.getPageIndex(), 
				 pageable.getPageSize(), 
				 page.getTotalSize(), 
				 list) ;
		
		return supportList;
	}
	public Map<String,Object> supportInfo(Integer id){
		Dao<Support> dao = DaoFactory.create(Support.class);
		Map<String,Object> supportInfo = dao.getSession().selectOne("supportInfo",id);
		return supportInfo;
	}
	
	public List<DynamicTreeNode> allDepartment(){
		
	  String pid = ConfigManager.getInstance().getConfig("c2_org_pid");
		if(chinacreatorDepartment==null){
			chinacreatorDepartment = OrgUserService.queryChildOrgs(pid, -1);
			Organization org = OrgUserService.getOrgInfoById(pid);
	        DynamicTreeNode dt = new DynamicTreeNode("0",pid,org.getName(),true);
	        chinacreatorDepartment.add(dt);
	        for (DynamicTreeNode dynamicTreeNode : chinacreatorDepartment) {
	            if(dynamicTreeNode.getPid()==null){
	                dynamicTreeNode.put("pId", pid) ;
	            }else{
	                dynamicTreeNode.put("pId", dynamicTreeNode.getPid()) ;
	            }
	        }
		}
		
		return chinacreatorDepartment;
	}
	
	public Support createSupportNote(Support support){
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		
		support.setCreatTime(new Timestamp(System.currentTimeMillis()));
		support.setUser(currentUserName);
		support.setState(0);
		support.setDoOrgId(ConfigManager.getInstance().getConfig("c2_sso_orgId"));//执行部门id
		
		Dao<Support> dao = DaoFactory.create(Support.class);
		dao.insert(support);
		return support;
	}
	
	public String findUserBelongDepartment(String userId){
		String belongOrgId = null;
		
		userId = CredentialStore.getCurrCredential().getUserInfo().getId();
		List<Organization> orgs =  OrgUserService.queryOrgs(userId);
		List<DynamicTreeNode> chinacreatorSonDepartmentMap = allDepartment();
		for (Organization orgDTO : orgs) {
			String orgId = orgDTO.getId();
			for (DynamicTreeNode entry : chinacreatorSonDepartmentMap) {
				if(entry.getId().contains(orgId)){
					belongOrgId = orgId;
					break;
				}
			}
		}
		
		return belongOrgId; 
	}
	
	public List<Support> getSupportList(){
	  return DaoFactory.create(Support.class).selectAll(new Sortable(new Order("creatTime", Order.DIRECTION_DESC)));
	}

}
