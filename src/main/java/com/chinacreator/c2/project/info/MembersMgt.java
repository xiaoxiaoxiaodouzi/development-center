package com.chinacreator.c2.project.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.chinacreator.c2.config.ConfigManager;
import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.security.handler.Pinyin4jUtil;
import com.chinacreator.c2.security.handler.SynUser;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.Organization;
import com.chinacreator.c2.uop.User;
import com.chinacreator.c2.workbench.Assistant;
import com.google.common.collect.Lists;

public class MembersMgt {

	/** 默认访客岗位ID */
	private static final String sfs_JOBID = "15";

	private JobService jobService = new JobService();

	@Transactional
	public void addMembers(int projectId, String... userNames) {

		List<Member> list = new ArrayList<Member>();
		for (String name : userNames) {
			Dao<Object> dao = DaoFactory.create(Object.class);
			Map<String,Object> user = dao.getSession().selectOne("com.chinacreator.c2.project.info.MemberMapper.existsByUserName", name);
			Member member = new Member();
			member.setProjectId(projectId);
			member.setUserId(user.get("user_id").toString());
			member.setJobId(sfs_JOBID);
			list.add(member);
		}

		Dao<Member> dao = DaoFactory.create(Member.class);
		dao.insertBatch(list);
	}

	@Transactional
	public void removeMembers(int projectId, String userId) {
		Dao<Member> dao = DaoFactory.create(Member.class);

		Member member = new Member();
		member.setProjectId(projectId);
		member.setUserId(userId);

		dao.getSession().delete("deleteByPU", member);
	}

	@Transactional
	public void setMembersByPerm(int projectId, String userId, String jobId, boolean state) {
		Dao<Member> dao = DaoFactory.create(Member.class);

		Member member = new Member();
		member.setProjectId(projectId);
		member.setUserId(userId);
		member.setJobId(jobId);

		if (state) {
			if (dao.count(member) <= 0) {
				dao.insert(member);
			}
		} else {
			dao.getSession().delete("deleteByPUJ", member);
		}
	}

	public List<Map<String, Object>> getMembers(int projectId) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		{
			Map<String, Object> userDTO = getProjectCreator(projectId);
			Map<String, Object> userMap = new HashMap<String, Object>();
			userMap.put("userDTO", userDTO);
			list.add(userMap);
		}

		Map<String, Map<String,Object>> projectUserMap = getProjectUserMap(projectId);
		Map<String, JobDTO> projectJobMap = getProjectJobMap(projectId);
		Dao<Member> dao = DaoFactory.create(Member.class);
		List<Member> members = dao.getSession().selectList("selectByProjectId", projectId);
		for (Member member : members) {
			Map<String, Object> userMap = new HashMap<String, Object>();
			List<JobDTO> jobList = new ArrayList<JobDTO>();
			for (Map<String, Object> map : list) {
				Map<String, Object> userDTO = (Map<String, Object>) map.get("userDTO");
				if (member.getUserId().equals(userDTO.get("user_id").toString())) {
					userMap = map;
					jobList = (List<JobDTO>) map.get("jobDTOs");
					if(null==jobList){
						jobList = new ArrayList<JobDTO>();
					}
					break;
				}
			}
			if (userMap.isEmpty()) {
				Map<String, Object> userDTO = projectUserMap.get(member.getUserId());
				if (null == userDTO) {
					Dao<Object> obdao = DaoFactory.create(Object.class);
					userDTO = obdao.getSession().selectOne("com.chinacreator.c2.project.info.MemberMapper.queryUserById", member.getUserId());
				}
				if(userDTO!=null){
					userMap.put("userDTO", userDTO);
					userMap.put("jobDTOs", jobList);
					list.add(userMap);
				}
			}
			JobDTO jobDTO = projectJobMap.get(member.getJobId());
			if (null == jobDTO) {
				jobDTO = jobService.queryByPK(member.getJobId());
			}
			jobList.add(jobDTO);
		}
		
		return list;
	}

	public Map<String,Object> getProjectCreator(int projectId) {
		Dao<Member> dao = DaoFactory.create(Member.class);
		Map<String,Object> userDTO = dao.getSession().selectOne("queryByProjectCreator", projectId);
		return userDTO;
	}

	public List<Map<String,Object>> getProjectResponsible(int projectId) {
		Dao<Member> dao = DaoFactory.create(Member.class);
		List<Map<String,Object>> list = dao.getSession()
				.selectList("com.chinacreator.c2.project.info.MemberMapper.queryByProjectResponsible", projectId);
		return list;
	}

	public List<Map<String,String>> getAddMembers(int projectId, String search,String orgId) {
		
		List<String> userNameList = new ArrayList<String>();

		if ((null != search && !search.trim().equals(""))||(null != orgId && !orgId.trim().equals(""))) {
			List<User> list = OrgUserService.queryUserByUserCondition(orgId,search);
			if(list.size()>0){
				for (User userDTO : list) {
					if(userDTO.getEmail()!=null)userNameList.add(userDTO.getEmail().substring(0, userDTO.getEmail().indexOf("@")));
				}
			}
		} else {
			WebOperationContext context = (WebOperationContext)OperationContextHolder.getContext();
			User user = OrgUserService.getUserInfoById(context.getUser().getId());
			String currentUserName =user.getEmail().substring(0, user.getEmail().indexOf("@"));
			Map<String,Object> uu = OrgUserService.getUserByName(currentUserName);
			Dao<Member> dao = DaoFactory.create(Member.class);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("projectId", projectId);
			map.put("currUserId", uu.get("user_id"));

			userNameList = dao.getSession()
					.selectList("com.chinacreator.c2.project.info.MemberMapper.queryAddMember", map);
			
		}
		
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		if(userNameList.size()>0){
			//根据远程获取的用户数据和机构数据再到本地拼装图标
			List<Map<String,String>> developUsers = DaoFactory.create(Object.class).getSession().selectList("queryUsersByUsernames", userNameList);
			for (Map<String,String> map:developUsers) {
				User u = OrgUserService.queryUserByEmail(map.get("userName")+"@chinacreator.com");
				if(u!=null){
					map.put("remark1", map.get("icon"));
					List<Organization> org = OrgUserService.queryOrgs(u.getId());
					if(org!=null&&org.size()>0){
						map.put("orgId", org.get(0).getId());
						map.put("orgShowName", org.get(0).getName());
					}
					list.add(map);
				}
				
			}
		}
		
		return list;
	}

	public List<Map<String, Object>> getAllMembers() {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		List<User> userDTOs = OrgUserService.queryUserByUserNames();
		
		int start = 0, end = 200;
	      while (start < userDTOs.size()) {
	        List<String> userNames = new ArrayList<String>();
	        for(int i=start;i<Math.min(userDTOs.size(), end);i++){
	            if(userDTOs.get(i).getEmail()!=null&&userDTOs.get(i).getEmail().contains("@")){
	                userNames.add(userDTOs.get(i).getEmail().substring(0, userDTOs.get(i).getEmail().indexOf("@")));
	            }
	        }
	        if(userNames.size()>0){
	          List<Map<String,Object>> userList = OrgUserService.getUserByNames(userNames);
	          for(Map<String, Object> map:userList){
	            if(map!=null){
	              map.put("id", map.get("user_id"));
	              map.put("username", map.get("user_name"));
	              map.put("userRealname", map.get("user_realname"));
	              map.put("icon", map.get("remark1"));
	              //map.put("jobNumber", userDTO.get("workno"));
	              map.put("qp", Pinyin4jUtil.getPinYin(map.get("user_realname").toString()));
	              map.put("jp", Pinyin4jUtil.getPinYinHeadChar(map.get("user_realname").toString()));
	            }
	          }
	          list.addAll(userList);
	        }
	        start = end;
	        end += 200;
	    }
	      
		/*for (User userDTO : userDTOs) {
			if (userDTO.getEmail()!=null) {
				Map<String, Object> map = OrgUserService.getUserByName(userDTO.getEmail().substring(0, userDTO.getEmail().indexOf("@")));
				if(map!=null){
				    map.put("id", map.get("user_id"));
					map.put("username", userDTO.getEmail().substring(0, userDTO.getEmail().indexOf("@")));
					map.put("userRealname", userDTO.getName());
					map.put("icon", map.get("remark1"));
					map.put("jobNumber", userDTO.get("workno"));
					map.put("qp", Pinyin4jUtil.getPinYin(userDTO.getName()));
					map.put("jp", Pinyin4jUtil.getPinYinHeadChar(userDTO.getName()));

					list.add(map);
				}
			}
		}*/

		return list;
	}
	
	//查询某个机构下的用户
	public List<Map<String,Object>> getMembersByOrgId(String orgId){
	  List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	  List<User> userList = OrgUserService.queryUserByOrg(orgId);
	  for (User user : userList) {
        if (user.getEmail()!=null) {
            Map<String, Object> map = OrgUserService.getUserByName(user.getEmail().substring(0, user.getEmail().indexOf("@")));
            if(map!=null){
                map.put("username", user.getEmail().substring(0, user.getEmail().indexOf("@")));
                map.put("userRealname", user.getName());
                map.put("icon", map.get("remark1"));
                map.put("qp", Pinyin4jUtil.getPinYin(user.getName()));
                map.put("jp", Pinyin4jUtil.getPinYinHeadChar(user.getName()));

                list.add(map);
            }
        }
      }
	  return list;
	}
	
	//查询基础研发中心下的用户
	public List<Map<String,Object>> getMembersByWorgOrg(){
	  String orgId = ConfigManager.getInstance().getConfig("c2_sso_orgId");
	  return getMembersByOrgId(orgId);
	}

	protected Map<String, Map<String,Object>> getProjectUserMap(int projectId) {
		Map<String, Map<String,Object>> map = new HashMap<String, Map<String,Object>>();

		Dao<Member> dao = DaoFactory.create(Member.class);
		List<Map<String,Object>> list = dao.getSession()
				.selectList("com.chinacreator.c2.project.info.MemberMapper.queryUserByProjectId", projectId);

		for (Map<String,Object> userDTO : list) {
			map.put(userDTO.get("user_id").toString(), userDTO);
		}

		return map;
	}

	protected Map<String, JobDTO> getProjectJobMap(int projectId) {
		Map<String, JobDTO> map = new HashMap<String, JobDTO>();

		Dao<Member> dao = DaoFactory.create(Member.class);
		List<JobDTO> list = dao.getSession()
				.selectList("com.chinacreator.c2.project.info.MemberMapper.queryJobByProjectId", projectId);

		for (JobDTO jobDTO : list) {
			map.put(jobDTO.getJobId(), jobDTO);
		}

		return map;
	}
	
	/**
	 *  获取项目中的所有工作人员,访客除外
	 * 
	 * @param projectId
	 * @return
	 */
	public List<Map<String,Object>> getProjectWorker(Integer projectId){
		return DaoFactory.create(Member.class).getSession()
				.selectList("com.chinacreator.c2.project.info.MemberMapper.selectProjectWorkers", projectId);
	}
	/**
	 * 获取用户名列表中对应的用户信息
	 */
	public List<Map<String,String>> getUserInfoByUsername(List<String> userNameList) {
		return DaoFactory.create(Object.class)
				.getSession().selectList("com.chinacreator.c2.project.info.MemberMapper.queryUsersByUsernames", userNameList);
	}
	/**
	 * 获取用户名中对应的用户信息
	 */
	public Map<String,String> getUserInfoByUsername(String userName) {
		return DaoFactory.create(Object.class)
				.getSession().selectOne("com.chinacreator.c2.project.info.MemberMapper.queryUserInfoByUsername", userName);
	}
	/**
	 * 获取用户对应的用户信息
	 */
	public List<User> getUserInfoByUserRealname(String userRealname) {
		//拼装图标
		List<User> developUsers = OrgUserService.queryUserByUserCondition(null,userRealname);
		List<String> userNames = new ArrayList<String>();
		for (User userDTO:developUsers) {
			userNames.add(userDTO.getEmail().substring(0, userDTO.getEmail().indexOf("@")));
		}
		for (User userDTO:developUsers) {
			Map<String,Object> map = OrgUserService.getUserByName(userDTO.getEmail().substring(0, userDTO.getEmail().indexOf("@")));
			if(map!=null){
				userDTO.put("remark1",map.get("remark1"));
				userDTO.put("userRealname",map.get("user_realname"));
				userDTO.put("userName",map.get("user_name"));
				userDTO.put("userId",map.get("user_id"));
			}
				
			List<Organization> org = OrgUserService.queryOrgByEmail(userDTO.getEmail());
			if(org!=null&&org.size()>0){
              String name = "";
              for(Organization o:org){
                name += o.getName()+",";
              }
              userDTO.put("orgShowName", name.substring(0, name.length()-1));
            }
		}
		return developUsers;
	}
	
	/**
	 * 添加助理
	 */
	public void addAssistant(List<Assistant> assistantList) {
		Dao<Assistant> dao = DaoFactory.create(Assistant.class);
		dao.insertBatch(assistantList);
	}
	
	/**
	 * 删除助理
	 */
	public void delAssistant(List<Assistant> assistantList) {
		Dao<Assistant> dao = DaoFactory.create(Assistant.class);
		dao.deleteBatch(assistantList);
	}
	
	/**
	 * 得到所有助理的用户信息
	 */
	public List<Map<String,String>> getAllAssistantsInfo() {
		Dao<Assistant> dao = DaoFactory.create(Assistant.class);
		List<Assistant> AssistList = new ArrayList<Assistant>();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Assistant a = new Assistant();
		AssistList = dao.select(a);
		
		if(AssistList.size() > 0) {
			List<String> userNameList = new ArrayList<String>();
			for (Assistant one : AssistList) {
				userNameList.add(one.getUserName());
			}
			
			//拼装图标
			if(userNameList.size()>0) {
				list = DaoFactory.create(Object.class).getSession().selectList("queryUsersByUsernames", userNameList);
				for (Map<String,String> userDTO : list) {//拼装机构id和名称
					List<Organization> org = OrgUserService.queryOrgByEmail(userDTO.get("userName")+"@chinacreator.com");
					if(org!=null&&org.size()>0){
		              String name = "";
		              for(Organization o:org){
		                name += o.getName()+",";
		              }
		              userDTO.put("orgShowName", name.substring(0, name.length()-1));
		            }
						
				}
			}
			return list;
		} else 
			return list;		
	}
	
	/**
	 * 某用户是否为助理
	 * @param userName
	 * @return
	 */
	public boolean isRoleAssistant(String userName) {
		Dao<Assistant> dao = DaoFactory.create(Assistant.class);
		Assistant user = dao.selectByID(userName);
		
		if(null != user) {
			return true;
		} else
			return false;
	}
	
	/**
	 * 修改用户头像
	 * @param usedto
	 */
	public void editUserImg(Map<String,String> userDto){
		Dao<Object> dao = DaoFactory.create(Object.class);
		dao.getSession().update("com.chinacreator.c2.project.info.MemberMapper.editUserImg", userDto);
	}
}
