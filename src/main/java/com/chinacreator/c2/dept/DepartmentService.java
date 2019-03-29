package com.chinacreator.c2.dept;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chinacreator.c2.config.ConfigManager;
import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.uop.OrgUserConstant;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.Organization;
import com.chinacreator.c2.uop.User;
import com.chinacreator.c2.web.ds.impl.DynamicTreeNode;
import com.chinacreator.c2.web.exception.EntityBusinessException;

public class DepartmentService {
	
	public List<DynamicTreeNode> getDepartMentTree(String orgId, int deepth){
		String pid = ConfigManager.getInstance().getConfig("c2_org_pid");
		List<DynamicTreeNode> orgs = OrgUserService.queryChildOrgs(pid, deepth);
		Organization org = OrgUserService.getOrgInfoById(pid);
		DynamicTreeNode dt = new DynamicTreeNode("0",pid,org.getName(),true);
		orgs.add(dt);
		for (DynamicTreeNode dynamicTreeNode : orgs) {
			if(dynamicTreeNode.getPid()==null){
				dynamicTreeNode.put("pId", pid) ;
			}else{
				dynamicTreeNode.put("pId", dynamicTreeNode.getPid()) ;
			}
		}
		return orgs;
	}
	
	public List<Map<String,Object>> getUserByDeptId(String orgId){
		return simplifyUserDto(OrgUserService.queryUserByUserNames()) ;
	}
	
	public List<DynamicTreeNode> getCurrentUserDeptTree(){
		WebOperationContext context = (WebOperationContext) OperationContextHolder.getContext();
		String userId = context.getUser().getId();
		List<DynamicTreeNode> orgs;
		try {
			orgs = OrgUserService.queryOrgsByUserName(userId);
		} catch (Exception e) {
		    e.printStackTrace();
			throw new EntityBusinessException("机构树调用失败！");
		}
		for (DynamicTreeNode dynamicTreeNode : orgs) {
			if(dynamicTreeNode.getPid()==null){
				dynamicTreeNode.put("pId", "0") ;
			}else{
				dynamicTreeNode.put("pId", dynamicTreeNode.getPid()) ;
			}
		}
		return orgs;
	}
	
	private List<Map<String,Object>> simplifyUserDto(List<User> userDtos){
		List<Map<String,Object>> resultDto = new ArrayList<Map<String,Object>>() ;
		for (User user : userDtos) {
			if(user.getEmail()!=null){
				Map<String,Object> map = OrgUserService.getUserByName(user.getEmail().substring(0, user.getEmail().indexOf("@")));
				if(map!=null)resultDto.add(map) ;
			}
		}
		return resultDto ;
	}

	/**
	 * 得到组织机构用户树
	 * 
	 * @param userName 用户名
	 * @return
	 */
	public List<DynamicTreeNode> getOrgUserTree(String userName){
		List<DynamicTreeNode> orgUsers; 
		try {
			orgUsers = OrgUserService.queryOrgUsersByUserName(userName);
		} catch (Exception e) {
			throw new EntityBusinessException("-----机构用户树调用失败！！");
		}
		
		List<String> nameList = new ArrayList<String>();
		for (DynamicTreeNode dynamicTreeNode : orgUsers) {
			if(dynamicTreeNode.getPid() == null){
				dynamicTreeNode.put("pId", "0") ;
			}else{
				dynamicTreeNode.put("pId", dynamicTreeNode.getPid());
			}
			
			if(null == dynamicTreeNode.get("userName")) {
				dynamicTreeNode.put("isParent", true);//使无子节点的分组点样式也改变
				dynamicTreeNode.put("nocheck", true);//使父节点不可选
			} else {
				nameList.add((String) dynamicTreeNode.get("userName"));
			}
		}
		
		Map<String,Map<String,String>> developUsers = DaoFactory.create(Object.class).getSession()
				.selectMap("com.chinacreator.c2.project.info.MemberMapper.queryUsersByUsernames", nameList, "userName");
		
		for (DynamicTreeNode dynamicTreeNode : orgUsers) {
			Map<String,String> developUserBean = developUsers.get(dynamicTreeNode.get("userName"));
			if(null != developUserBean){
				dynamicTreeNode.put("remark1", developUserBean.get("icon"));
			} else {
				dynamicTreeNode.put("remark1", null);
			}
		}
		return orgUsers;
	}
	
}
