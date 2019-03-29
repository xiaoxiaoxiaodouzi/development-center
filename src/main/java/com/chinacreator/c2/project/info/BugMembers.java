package com.chinacreator.c2.project.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.project.bug.Bug;
import com.chinacreator.c2.uop.OrgUserConstant;

public class BugMembers {

	private MembersMgt memberMgt = new MembersMgt();
	private JobService jobService = new JobService();
	
	/**
	 * 获取参与项目的成员信息
	 * @param projectId
	 * 			项目编号
	 * @param bugId 
	 * 			bug编号
	 * @return list 
	 * 			返回满足条件的成员信息
	 */
	public List<Map<String, Object>> getMembers(int projectId , int bugId) {
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		{
			Map<String, Object> userDTO = memberMgt.getProjectCreator(projectId);
			Map<String, Object> userMap = new HashMap<String, Object>();
			userMap.put("userDTO", userDTO);
			list.add(userMap);
		}

		Map<String, Map<String, Object>> projectUserMap = memberMgt.getProjectUserMap(projectId);
		Map<String, JobDTO> projectJobMap = memberMgt.getProjectJobMap(projectId);
		Dao<Member> dao = DaoFactory.create(Member.class);
		List<Member> members = dao.getSession().selectList("selectByProjectId", projectId);

		Dao<Bug> bugDao = DaoFactory.create(Bug.class);
		//根据相应的 bugId 查询出对应的用户信息
		String userName = bugDao.getSession().selectOne("com.chinacreator.c2.project.bug.BugMapper.selectUserNameByBugId", bugId);
		
		for (Member member : members) {
			Map<String, Object> userMap = new HashMap<String, Object>();
			List<JobDTO> jobList = new ArrayList<JobDTO>();
			for (Map<String, Object> map : list) {
				Map<String, Object> userDTO = (Map<String, Object>) map.get("userDTO");
				if (member.getUserId().equals(userDTO.get("user_id"))) {
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
				userMap.put("userDTO", userDTO);
				userMap.put("jobDTOs", jobList);
				
				if(userDTO!=null){
				    //查询出的用户中，过滤掉已离职，或者不是当前bug的分配人用户
	                if(Integer.parseInt(userDTO.get("user_isvalid").toString())!=2 && 
	                        !userName.equals(userDTO.get("user_name"))){
	                    continue;
	                }
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
}
