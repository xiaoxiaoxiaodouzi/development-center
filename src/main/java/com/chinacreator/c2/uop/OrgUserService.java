package com.chinacreator.c2.uop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.chinacreator.asp.comp.sys.oauth2.common.CredentialStore;
import com.chinacreator.c2.config.ConfigManager;
import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.project.info.Member;
import com.chinacreator.c2.uop.Organization;
import com.chinacreator.c2.uop.User;
import com.chinacreator.c2.web.ds.impl.DynamicTreeNode;
import com.chinacreator.c2.web.util.RestUtils;
import com.google.common.collect.Lists;

public class OrgUserService {

	private static String UOP_REMOTE_URL ;
	
	private static String getUOPUrl(){
		if(UOP_REMOTE_URL==null){
			String apiSchema = ConfigManager.getInstance().getConfig("c2_sso_proxy_apigateway_schema") ;
			String apiHost = ConfigManager.getInstance().getConfig("c2_sso_proxy_apigateway_host") ;
			String apiPort = ConfigManager.getInstance().getConfig("c2_sso_proxy_apigateway_port") ;
			UOP_REMOTE_URL = apiSchema+"://"+apiHost;
			if(apiPort!=null){
			  UOP_REMOTE_URL = apiSchema+"://"+apiHost+":"+apiPort ;
			}
		}
		return UOP_REMOTE_URL ;
	}
	
	public static User getUserInfoById(String userId){
		ParameterizedTypeReference<User> userReference = new ParameterizedTypeReference<User>() {};
		ResponseEntity<User> responseUser = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/users/"+userId,HttpMethod.GET, null, userReference);
		User user = responseUser.getBody();
		return user;
	}
	
	public static Page<User> getManagerUser(String userName,int pageIndex,int pageSize){
		String url = "";
		if(userName!=null&&!userName.equals("")){
			url = "/uop/v1/users?name="+userName+"&page="+pageIndex+"&rows="+pageSize+"&categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId");
		}else{
			url = "/uop/v1/users?page="+pageIndex+"&rows="+pageSize+"&categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId");
		}
		ParameterizedTypeReference<Page<User>> userReference = new ParameterizedTypeReference<Page<User>>() {};
		ResponseEntity<Page<User>> responseUser = RestUtils.createRestTemplate().exchange(getUOPUrl()+url,HttpMethod.GET, null, userReference);
		Page<User> user = responseUser.getBody();
		for(User u:user.getContents()){
			u.put("userName", u.getEmail().substring(0, u.getEmail().indexOf("@")));
		}
		return user;
	}

	/**
	 * 查询某机构下的所有用户
	 * @param orgIds
	 * @return
	 */
	public static List<User> queryUserByOrg(String... orgIds) {
		List<User> list = new ArrayList<User>();
		for(String s:orgIds){
			ParameterizedTypeReference<List<User>> userReference = new ParameterizedTypeReference<List<User>>() {};
			ResponseEntity<List<User>> responseUser = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/orgs/"+s+"/users?state=-1&cascade=true&categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId"),HttpMethod.GET, null, userReference);
			List<User> user = responseUser.getBody();
			for(User u:user){
				if(StringUtils.isNotBlank(u.getEmail()) && u.getEmail().contains("@")) list.add(u);
			}
		}
		return list;
	}
	
	/**
	 * 查询某机构下的所有用户
	 * @param orgIds
	 * @return
	 */
	public static List<User> queryUserByOrg(String status, String orgId) {
	    ParameterizedTypeReference<List<User>> userReference = new ParameterizedTypeReference<List<User>>() {};
		ResponseEntity<List<User>> responseUser = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/orgs/"+orgId+"/users?state="+status+"&cascade=true&categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId"),HttpMethod.GET, null, userReference);
		return responseUser.getBody();
	}

	/**
	 * 查询机构下的子机构信息
	 * @param orgId
	 * @param deepth
	 * @return
	 */
	public static List<DynamicTreeNode> queryChildOrgs(String orgId, int deepth) {
		ParameterizedTypeReference<List<Organization>> orgReference = new ParameterizedTypeReference<List<Organization>>() {};
		ResponseEntity<List<Organization>> responseOrg = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/orgs/"+orgId+"/children?cascade=true&categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId"),HttpMethod.GET, null, orgReference);
		List<Organization> orgs = responseOrg.getBody();
		List<DynamicTreeNode> list = new ArrayList<DynamicTreeNode>();
		for(Organization org:orgs){
			DynamicTreeNode treeNode = new DynamicTreeNode(org.getPid(), org.getId(), org.getName(), false);
			if(treeNode.getPid()==null){
			  treeNode.put("pId", "0") ;
            }else{
              treeNode.put("pId", treeNode.getPid()) ;
            }
			list.add(treeNode);
		}
		return list;
	}
	
	/**
     * 查询机构下的子机构信息
     * @param orgId
     * @param deepth
     * @return
     */
    public static List<Organization> queryChildOrgs(String orgId,boolean cascade) {
        ParameterizedTypeReference<List<Organization>> orgReference = new ParameterizedTypeReference<List<Organization>>() {};
        ResponseEntity<List<Organization>> responseOrg = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/orgs/"+orgId+"/children?cascade="+cascade+"&categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId"),HttpMethod.GET, null, orgReference);
        return responseOrg.getBody();
    }

	/**
	 * 查询用户所属的机构树
	 * @param userName
	 * @return
	 */
	public static List<DynamicTreeNode> queryOrgsByUserName(String userId) {
		List<DynamicTreeNode> list = new ArrayList<DynamicTreeNode>();
		ParameterizedTypeReference<List<Organization>> orgReference = new ParameterizedTypeReference<List<Organization>>() {};
		ResponseEntity<List<Organization>> responseOrg = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/users/"+userId+"/orgs?categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId"),HttpMethod.GET, null, orgReference);
		//获取所有机构
		List<Organization> orgs = responseOrg.getBody();
		//判断用户是否直接挂载在事业部下
		for(Organization o:orgs){
			if(o.getPid()==null && o.containsKey("type") && o.get("type").equals("4")){
				DynamicTreeNode treeNode = new DynamicTreeNode(o.getPid(), o.getId(), o.getName(), true);
				list.add(treeNode);
				list.addAll(queryChildOrgs(o.getId(),-1));
				return list;
			}
		}
		//如果用户不是直接挂载在事业部下
		for(Organization o:orgs){
		  ResponseEntity<List<Organization>> response = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/orgs/"+o.getId()+"/parents?cascade=true&categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId"),HttpMethod.GET, null, orgReference);
		  List<Organization> parentOrgs = response.getBody();
		  for (Organization organization : parentOrgs) {
		    if(organization.containsKey("type") && organization.get("type").equals("4")){
              DynamicTreeNode treeNode = new DynamicTreeNode(organization.getPid(), organization.getId(), organization.getName(), true);
              list.add(treeNode);
              list.addAll(queryChildOrgs(organization.getId(),-1));
              return list;
            }
          }
        }
		return list;
	}
	
	/**
	 * 根据登录名查询多个用户
	 * @param names
	 * @return
	 */
	public static List<User> queryUserByUserNames() {
		List<User> list = new ArrayList<User>();
		ParameterizedTypeReference<List<User>> userReference = new ParameterizedTypeReference<List<User>>() {};
		ResponseEntity<List<User>> responseUser = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/users?categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId"),HttpMethod.GET, null, userReference);
		List<User> user = responseUser.getBody();
        for(User u:user){
            if(StringUtils.isNotBlank(u.getEmail()) && u.getEmail().contains("@")) list.add(u);
        }
		return list;
	}
	
	/**
	 * 根据登录名获取分页用户
	 * @param userId
	 * @param pageIndex
	 * @param pageSize
	 * @param object
	 * @return
	 */
	public static Page<User> queryByUser(int pageIndex, int pageSize) {
		ParameterizedTypeReference<Page<User>> userReference = new ParameterizedTypeReference<Page<User>>() {};
		ResponseEntity<Page<User>> responseUser = RestUtils.createRestTemplate().exchange(getUOPUrl()+
				"/uop/v1/users?categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId")+"&page="+pageIndex+"&rows="+pageSize,HttpMethod.GET, null, userReference);
		Page<User> user = responseUser.getBody();
		return user;
	}
	
	/**
	 * 查询机构用户树
	 * @return
	 */
	public static List<DynamicTreeNode> queryOrgUsers() {
		List<DynamicTreeNode> list = new ArrayList<DynamicTreeNode>();
		ParameterizedTypeReference<List<Organization>> orgReference = new ParameterizedTypeReference<List<Organization>>() {};
		ResponseEntity<List<Organization>> responseOrg = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/orgs/0/children?cascade=true&categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId"),HttpMethod.GET, null, orgReference);
		List<Organization> orgs = responseOrg.getBody();
		for(Organization o:orgs){
			DynamicTreeNode orgNode = new DynamicTreeNode(o.getPid(), o.getId(), o.getName(), false);
			orgNode.put("type", "org");
			list.add(orgNode);
			ParameterizedTypeReference<List<User>> userReference = new ParameterizedTypeReference<List<User>>() {};
			ResponseEntity<List<User>> responseUser = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/orgs/"+o.getId()+"/users",HttpMethod.GET, null, userReference);
			List<User> user = responseUser.getBody();
			for(User u:user){
				if(u.get("state").equals("1")&&StringUtils.isNotBlank(u.getEmail()) && u.getEmail().contains("@")){
					DynamicTreeNode userNode = new DynamicTreeNode(o.getId(), u.getId(), u.getName(), false);
					userNode.put("userName", u.getEmail().substring(0, u.getEmail().indexOf("@")));
					list.add(userNode);
				}
			}
		}
		return list;
	}
	
	public static List<User> queryOrgUsers(String orgId,String state, boolean cascade) {
      ParameterizedTypeReference<List<User>> userReference = new ParameterizedTypeReference<List<User>>() {};
      String url = "";
      if(orgId!=null&&!orgId.equals("")){
          url = getUOPUrl()+"/uop/v1/orgs/"+orgId+"/users?state="+state+"&categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId")+"&cascade="+cascade;
      }
      ResponseEntity<List<User>> responseUser = RestUtils.createRestTemplate().exchange(url,HttpMethod.GET, null, userReference);
      return responseUser.getBody();
  }

	/**
	 * 模糊搜索用户信息
	 * @param orgId 机构id
	 * @param search 姓名/工号/拼音
	 * @return
	 */
	public static List<User> queryUserByUserCondition(String orgId, String search) {
		List<User> userList = new ArrayList<User>();
		ParameterizedTypeReference<List<User>> userReference = new ParameterizedTypeReference<List<User>>() {};
		String url = "";
		if(orgId!=null&&!orgId.equals("")){
		  if(search!=null&&!search.equals("")){
		    url = getUOPUrl()+"/uop/v1/orgs/"+orgId+"/users?categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId")+"&name="+search;
		  }else{
		    url = getUOPUrl()+"/uop/v1/orgs/"+orgId+"/users?categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId");
		  }
		}else{
		  if(search!=null&&!search.equals("")){
		    url = getUOPUrl()+"/uop/v1/users?categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId")+"&name="+search;
		  }
		}
		ResponseEntity<List<User>> responseUser = RestUtils.createRestTemplate().exchange(url,HttpMethod.GET, null, userReference);
        List<User> users = responseUser.getBody();
        for(User user:users){
          if(StringUtils.isNotBlank(user.getEmail()) && user.getEmail().contains("@"))userList.add(user);
        }
		return userList;
	}

	/**
	 * 查询用户所在的机构信息
	 * @param userId
	 * @return
	 */
	public static List<Organization> queryOrgs(String userId) {
		ParameterizedTypeReference<List<Organization>> orgReference = new ParameterizedTypeReference<List<Organization>>() {};
		ResponseEntity<List<Organization>> responseOrg = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/users/"+userId+"/orgs?categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId"),HttpMethod.GET, null, orgReference);
		List<Organization> orgs = responseOrg.getBody();
		return orgs;
	}
	
	/**
	 * 查询用户所在的机构用户树
	 * @param userId
	 * @return
	 */
	public static List<DynamicTreeNode> queryOrgUsersByUserName(String userId) {
		List<DynamicTreeNode> list = new ArrayList<DynamicTreeNode>(); 
		ParameterizedTypeReference<List<Organization>> orgReference = new ParameterizedTypeReference<List<Organization>>() {};
		ResponseEntity<List<Organization>> responseOrg = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/users/"+userId+"/orgs?categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId"),HttpMethod.GET, null, orgReference);
		List<Organization> orgs = responseOrg.getBody();
		for(Organization o:orgs){
			DynamicTreeNode treeNode = new DynamicTreeNode(o.getPid(), o.getId(), o.getName(), false);
			list.add(treeNode);
			ParameterizedTypeReference<List<User>> userReference = new ParameterizedTypeReference<List<User>>() {};
			ResponseEntity<List<User>> responseUser = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/orgs/"+o.getId()+"/users?cascade=true&categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId"),HttpMethod.GET, null, userReference);
			List<User> user = responseUser.getBody();
			for(User u:user){
				if(u.get("state").equals("1")&&StringUtils.isNotBlank(u.getEmail()) && u.getEmail().contains("@")){
					DynamicTreeNode userNode = new DynamicTreeNode(o.getId(), u.getId(), u.getName(), false);
					userNode.put("userName", u.getEmail().substring(0, u.getEmail().indexOf("@")));
					list.add(userNode);
				}
			}
		}
		return list;
	}
	
	//查询当前用户在某项目中的是否具有资源
	public static boolean isPermitted(int projectId, String perm) {
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
		Dao<Member> dao = DaoFactory.create(Member.class);
		Member member = new Member();
		member.setProjectId(projectId);
		member.setUserId(uu.get("user_id").toString());
		List<Member> list = dao.select(member);
		if(list.size()>0){
			String roleIds = "";
			for(Member m:list){
				roleIds += m.getJobId()+",";
			}
			String	role = roleIds.length()>0?roleIds.substring(0, roleIds.length()-1):"";
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("roleIds", role);
			if(perm.indexOf("##")>-1){
				map.put("perm", perm.substring(perm.indexOf("##")+2, perm.length()));
			}else{
				map.put("perm", perm);
			}
			List<Map<String,Object>> perList = dao.getSession().selectList("isPermitted", map);
			return perList!=null&&perList.size()>0?true:false;
		}
		return false;
	}

	public static boolean isPermitted(String perm) {
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		Object[] roleCodes = (Object[]) CredentialStore.getCurrCredential().getUserInfo().get("roles") ;
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
		Dao<Member> dao = DaoFactory.create(Member.class);
		String role = "";
		for(Object r:roleCodes){
			if(r.toString().equals(OrgUserConstant.ADMIN)){
				return true;
			}
			continue;
		}
		Member member = new Member();
		member.setUserId(uu.get("user_id").toString());
		List<Member> list = dao.select(member);
		if(list.size()>0){
			String roleIds = "";
			for(Member m:list){
				roleIds += m.getJobId()+",";
			}
			role = roleIds.length()>0?roleIds.substring(0, roleIds.length()-1):"";
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("roleIds", role);
			map.put("perm", perm);
			List<Map<String,Object>> perList = dao.getSession().selectList("isPermitted", map);
			return perList!=null&&perList.size()>0?true:false;
		}
		return false;
		
	}
	
	public static Map<String,Object> getUserByName(String userName){
		Dao<Object> dao = DaoFactory.create(Object.class);
		Map<String,Object> user = dao.getSession().selectOne("com.chinacreator.c2.project.info.MemberMapper.existsByUserName", userName);
		return user;
	}

	public static User queryUserByEmail(String email){
		ParameterizedTypeReference<List<User>> userReference = new ParameterizedTypeReference<List<User>>() {};
		ResponseEntity<List<User>> responseUser = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/users?categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId")+"&email="+email,HttpMethod.GET, null, userReference);
		List<User> user = responseUser.getBody();
		return user.size()>0?user.get(0):null;
	}
	/**
	 * 通过邮箱获取用户所属的机构信息
	 * @param email
	 * @return
	 */
	public static List<Organization> queryOrgByEmail(String email) {
		User user = queryUserByEmail(email);
		if(user!=null){
			ParameterizedTypeReference<List<Organization>> orgReference = new ParameterizedTypeReference<List<Organization>>() {};
			ResponseEntity<List<Organization>> responseOrg = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/users/"+user.getId()+"/orgs?categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId"),HttpMethod.GET, null, orgReference);
			List<Organization> org = responseOrg.getBody();
			if(org.size()>0){
			  return org;
			}
		}
		return null;
	}

	public static Organization getOrgInfoById(String orgId) {
		ParameterizedTypeReference<Organization> orgReference = new ParameterizedTypeReference<Organization>() {};
		ResponseEntity<Organization> responseOrg = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/orgs/"+orgId,HttpMethod.GET, null, orgReference);
		Organization orgs = responseOrg.getBody();
		return orgs;
	}

	public static List<User> queryUserByNames(List<String> names) {
		List<User> list = new ArrayList<User>();
		for(String s:names){
			ParameterizedTypeReference<List<User>> userReference = new ParameterizedTypeReference<List<User>>() {};
			ResponseEntity<List<User>> responseUser = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/users?categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId")+"&email="+s+"@chinacreator.com",HttpMethod.GET, null, userReference);
			List<User> user = responseUser.getBody();
			list.addAll(user);
		}
		return list;
	}
	
	public static List<User> queryUserByids(List<String> ids) {
		List<User> list = new ArrayList<User>();
			ParameterizedTypeReference<List<User>> userReference = new ParameterizedTypeReference<List<User>>() {};
			ResponseEntity<List<User>> responseUser = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/users?categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId")+"&id="+ids,HttpMethod.GET, null, userReference);
			List<User> user = responseUser.getBody();
			list.addAll(user);
		return list;
	}


	public static Page<Map<String,Object>> queryByName(String userName, int pageIndex,
			int pageSize) {
		String url = "";
		if(userName!=null&&!userName.equals("")){
			url = "/uop/v1/users?name="+userName+"&page="+pageIndex+"&rows="+pageSize+"&categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId");
		}else{
			url = "/uop/v1/users?page="+pageIndex+"&rows="+pageSize+"&categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId");
		}
		ParameterizedTypeReference<Page<User>> userReference = new ParameterizedTypeReference<Page<User>>() {};
		ResponseEntity<Page<User>> responseUser = RestUtils.createRestTemplate().exchange(getUOPUrl()+url,HttpMethod.GET, null, userReference);
		Page<User> user = responseUser.getBody();
		List<String> userNames = new ArrayList<String>();
		for(User u:user.getContents()){
			if(StringUtils.isNotBlank(u.getEmail()) && u.getEmail().contains("@")){
				userNames.add(u.getEmail().substring(0, u.getEmail().indexOf("@")));
			}
		}
		List<Map<String,Object>> contents = new ArrayList<Map<String,Object>>();
		for(User u:user.getContents()){
			if(StringUtils.isNotBlank(u.getEmail()) && u.getEmail().contains("@")){
				Map<String,Object> map = getUserByName(u.getEmail().substring(0, u.getEmail().indexOf("@")));
				if(map!=null)contents.add(map);
			}
		}
		Page<Map<String,Object>> page = new Page<Map<String,Object>>(pageIndex, pageSize, user.getTotal(), contents);
		return page;
	}

  public static List<Map<String, Object>> getUserByNames(List<String> userNames) {
    if(userNames.size()>0){
      Dao<Object> dao = DaoFactory.create(Object.class);
      List<Map<String, Object>> user = dao.getSession().selectList("com.chinacreator.c2.project.info.MemberMapper.getUserByNames", userNames);
      return user;
    }else{
      return Lists.newArrayList();
    }
  }

  public static List<Organization> getOrgInfoByIds(List<String> orgs) {
    String ids = "";
    for(String id:orgs){
      ids += "id="+id+"&";
    }
    ParameterizedTypeReference<List<Organization>> orgReference = new ParameterizedTypeReference<List<Organization>>() {};
    ResponseEntity<List<Organization>> responseOrg = RestUtils.createRestTemplate().exchange(getUOPUrl()+"/uop/v1/orgs?"+ids+"categoryId="+ConfigManager.getInstance().getConfig("c2_sso_categoryId"),HttpMethod.GET, null, orgReference);
    return responseOrg.getBody();
  }
  
	public static <T> Page<T> spiltPage(List<T> data, int page, int rows) {
		Page<T> dataPage = new Page<T>();
		dataPage.setPageIndex(page);
		dataPage.setPageSize(rows);

		if (data != null) {
			int total = data.size();
			dataPage.setTotal(total);
			int pageSize = dataPage.getPageSize();
			int start = (dataPage.getPageIndex() - 1) * pageSize;
			int length = total - start;
			if (length > pageSize)
				length = pageSize;
			if (length > 0)
				dataPage.setContents(data.subList(start, start + length));
		}
		return dataPage;
	}

}
