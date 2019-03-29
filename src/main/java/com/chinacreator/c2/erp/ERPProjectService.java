package com.chinacreator.c2.erp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.dao.mybatis.enhance.Pageable;
import com.chinacreator.c2.project.info.Member;
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.project.service.ProjectService;

public class ERPProjectService {
	public Page<ErpProject> getERPProjects(String code,Pageable pageable){
		Dao<ErpProject> dao=DaoFactory.create(ErpProject.class);
		
		ErpProject conditionErp=new ErpProject();
		if(StringUtils.isNotEmpty(code)){
			conditionErp.setPcode("%"+code+"%");
		}
		return dao.selectByPage(conditionErp, pageable, null, true);
	}
	
	public void createProjects(ErpProject[] projects){
		Dao<ErpProject> dao=DaoFactory.create(ErpProject.class);
		Dao<Member> memberDao=DaoFactory.create(Member.class);
		
		ProjectService projectService=new ProjectService();
		for(ErpProject project:projects){
			try {
				Project c2Project=new Project();
				c2Project.setCode(project.getPcode());
				c2Project.setName(project.getPname());
				
				projectService.createProject(c2Project);
				
				List<String> emails =dao.getSession().selectList("selectMemberEmails", project.getPcode());

				List<Member> members=new ArrayList<Member>();
				for(String email:emails){
					if(StringUtils.isNotEmpty(email)){
						int index=email.indexOf("@");
						String userName=email.substring(0,index);
						Map<String,Object> user = queryByUserName(userName);
						
						if(user.get("user_id")!=null){
							Member member=new Member();
							member.setJobId("15");//访客
							member.setUserId(user.get("user_id").toString());
							member.setProjectId(c2Project.getId());
							members.add(member);
						}
					}
				}
				memberDao.insertBatch(members);
			} catch (Exception e) {
			}
		}
	}

	private Map<String, Object> queryByUserName(String name) {
		Dao<Object> dao = DaoFactory.create(Object.class);
		Map<String,Object> user = dao.getSession().selectOne("com.chinacreator.c2.project.info.MemberMapper.existsByUserName", name);
		return user;
	}
}
