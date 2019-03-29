package com.chinacreator.c2.security.shiro.impls;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.ioc.ApplicationContextManager;
import com.chinacreator.c2.project.info.Member;
import com.chinacreator.c2.project.info.MembersMgt;
import com.chinacreator.c2.sysmgr.exception.AuthenticationException;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;

public class ProjectAuthorizationProvider {

	private static final String sfs_SEPARATOR = "##";

	private static Map<Integer, String> sm_PROJECT_CREATOR = new HashMap<Integer, String>();

	public boolean isProjectPermited(int projectId, String permExpr) throws AuthenticationException {
		
		try {
			if(null==permExpr || permExpr.trim().equals("")){
				return false;
			}
			if(permExpr.equals("story_cud")){//开发角色用户均可增加需求model_cud
				if(isProjectCreator(projectId)||isProjectDevelop(projectId,"13")){
					return true;
				}
			}
			if(isProjectCreator(projectId)){
				return true;
			}
			
			return OrgUserService.isPermitted(projectId, permExpr);
		} catch (Exception e) {
			throw new AuthenticationException(e.getMessage(), e);
		}
	}

	private boolean isProjectDevelop(int projectId,String roleId) {
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
		return isProjectTester(projectId, uu.get("user_id").toString(), roleId);
	}

	public Map<String, Boolean> isProjectPermitedByBatch(int projectId, String... permExprs)
			throws AuthenticationException {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		try {
			for (String perm : permExprs) {
				map.put(perm, isProjectPermited(projectId, perm));
			}
		} catch (Exception e) {
			throw new AuthenticationException(e.getMessage(), e);
		}
		return map;
	}

	public Map<String, Boolean> isProjectPermitedByBatch(String... permExprs) throws AuthenticationException {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		try {
			for (String perm : permExprs) {
				if(null!=perm && !perm.trim().equals("")){
					if(isProjectCreator(perm)){
						map.put(perm, true);
					}else{
						if(perm.contains("##test")){//项目负责人、测试人员、项目创建者拥有测试用例操作权限
							if(isProjectTester(perm,"14") || isProjectTester(perm,"11")){
								map.put(perm, true);
							}
						}else{
							if(perm.indexOf(sfs_SEPARATOR)>0){
								int projectId = Integer.parseInt(perm.substring(0, perm.indexOf(sfs_SEPARATOR)));
								map.put(perm, OrgUserService.isPermitted(projectId,perm));
							}else{
								map.put(perm, OrgUserService.isPermitted(perm));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new AuthenticationException(e.getMessage(), e);
		}
		return map;
	}

	//判断是否为项目测试人员
	private boolean isProjectTester(String perm,String roleId) {
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
		int index = perm.indexOf(sfs_SEPARATOR);
		if (index > 0) {
			try {
				int projectId = Integer.parseInt(perm.substring(0, index));
				return isProjectTester(projectId,uu.get("user_id").toString(),roleId);
			} catch (Exception e) {
			}
		}
		return false;
	}

	private boolean isProjectTester(int projectId, String userId,String roleId) {
		Dao<Member> dao = DaoFactory.create(Member.class);
		Member m = new Member();
		m.setJobId(roleId);//14为测试人员角色
		m.setProjectId(projectId);
		m.setUserId(userId);
		Member member = dao.selectOne(m);
		return member!=null?true:false;
	}

	private boolean isProjectCreator(String permExpr) {
		int index = permExpr.indexOf(sfs_SEPARATOR);
		if (index > 0) {
			try {
				int projectId = Integer.parseInt(permExpr.substring(0, index));
				return isProjectCreator(projectId);
			} catch (Exception e) {
			}
		}
		return false;
	}

	private boolean isProjectCreator(int projectId) {
		WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
		User user = OrgUserService.getUserInfoById(context.getUser().getId());
		String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
		Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
		String pc = sm_PROJECT_CREATOR.get(projectId);
		if (null == pc) {
			MembersMgt membersMgt = new MembersMgt();
			Map<String,Object> userDTO = membersMgt.getProjectCreator(projectId);
			if (null != userDTO) {
				pc = userDTO.get("user_id").toString();
				sm_PROJECT_CREATOR.put(projectId, pc);
			}
		}
		return uu.get("user_id").toString().equals(pc);
	}

	public static void removeProjectMap(int projectId){
		sm_PROJECT_CREATOR.remove(projectId);
	}

	public static String combinPerm(int projectId, String permExpr) {
		return projectId + sfs_SEPARATOR + permExpr;
	}
}
