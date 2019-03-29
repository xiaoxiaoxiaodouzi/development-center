package com.chinacreator.c2.project.service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.chinacreator.asp.comp.sys.oauth2.common.CredentialStore;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.dao.mybatis.enhance.Pageable;
import com.chinacreator.c2.dao.mybatis.enhance.RowBounds4Page;
import com.chinacreator.c2.erp.ErpProject;
import com.chinacreator.c2.message.MessageRequest;
import com.chinacreator.c2.message.MessageRequestBuilder;
import com.chinacreator.c2.message.MessageSender;
import com.chinacreator.c2.project.constant.Const;
import com.chinacreator.c2.project.info.Member;
import com.chinacreator.c2.project.info.MembersMgt;
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.project.info.ProjectPlan;
import com.chinacreator.c2.project.stage.Stage;
import com.chinacreator.c2.security.shiro.impls.ProjectAuthorizationProvider;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;
import com.google.common.collect.Lists;

public class ProjectService {
	
	private static MembersMgt memberService = new MembersMgt() ;
	private static Lock lock = new ReentrantLock();
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Map<Integer, Project> projectMap;
	private Map<String, ErpProject> erpMap;
	
	/**
	 * 创建项目
	 * @param project 项目基本信息，一般前台只有code和name属性有值，其余默认值在这里补全
	 */
	public int createProject(Project project){
		project.setCreateDate(new Timestamp(System.currentTimeMillis()));
		project.setBrief("这是一个神秘的项目，没有任何摘要");
		project.setDescription("这是一个神秘的项目，什么描述都没有");
		project.setStatus(Const.PROJECT.DOING.value);
		project.setArcStatus(Const.ARCHIVE.ACTIVE.value);
		
		Random random=new Random();
		int picId=random.nextInt(20);
		while(picId==0){
			picId=random.nextInt(20);
		}
		project.setPic("assets/img/headers/sm/"+picId+".png");
		
		Dao<Project> dao=DaoFactory.create(Project.class);
		dao.insert(project);
		
		//新建项目后需要初始化任务阶段数据
		initStage(project.getId());
		//新建项目后需要初始化项目计划数据
		initPlan(project);
		return project.getId();
	}
	
	private void initPlan(Project project) {
		String[] names = {"需求分析","系统设计","实现测试","系统测试","系统上线"};
		Dao<ProjectPlan> dao = DaoFactory.create(ProjectPlan.class);
		List<ProjectPlan> list = Lists.newArrayList();
		for(String name:names){
			ProjectPlan pp = new ProjectPlan();
			pp.setName(name);
			pp.setStartDate(new Timestamp(System.currentTimeMillis()));
			pp.setEndDate(new Timestamp(System.currentTimeMillis()+30*24*60*60*1000));
			pp.setProjectId(project);
			list.add(pp);
		}
		dao.insertBatch(list);
	}

	private void initStage(Integer projectId) {
      String[] names = {"未开始","进行中","待审核","已完成"};
      Dao<Stage> dao = DaoFactory.create(Stage.class);
      List<Stage> list = Lists.newArrayList();
      int i=1;
      for(String name:names){
        Stage s = new Stage();
        s.setName(name);
        s.setProjectId(projectId);
        s.setOrderNo(i++);
        list.add(s);
      }
      dao.insertBatch(list);
    }
	
	/**
	 * 更新项目基本信息
	 * @param project
	 * @return
	 */
	public Project updateProject(Project project){
	  Dao<Project> dao=DaoFactory.create(Project.class);
	  Project oldProject = dao.selectByID(project.getId());//旧数据
	  if(!project.getContractNo().equals(oldProject.getContractNo())){//合同号发生变化
	    if(project.getArcStatus().equals("0")){//设为进行中
	      project.setStatus("doing");
	      project.setArcStatus("1");
	    }
	  }
	  dao.update(project);
	  return project;
	}
	
	/**
	 * 获取某项目详情
	 * @param projectId
	 * @return
	 */
	public Project getProjectInfo(String projectId){
	  Dao<Project> dao=DaoFactory.create(Project.class);
	  Project project = dao.selectByID(projectId);
      return project;
	}

  /**
	 * 获取当前用户参与的所有项目
	 * @return
	 */
	public List<Project> getMyProjects(){
		String userId = CredentialStore.getCurrCredential().getUserInfo().getId();
		User user = OrgUserService.getUserInfoById(userId);
		String ownerName = user.getEmail().substring(0, user.getEmail().indexOf("@"));
		String	owner = memberService.getUserInfoByUsername(ownerName).get("userId") ;
		return getProjects(owner);
	}
	
	public List<Project> getProjects(String userId){
	  Project condition=new Project();
      condition.setOwner(userId);
      
      Dao<Project> dao=DaoFactory.create(Project.class);
      //当前用户创建的项目
      List<Project> ownProjects = dao.select(condition);
      
      HashMap<Integer, Project> projects=new HashMap<Integer, Project>();
      for(Project project:ownProjects){
          projects.put(project.getId(), project);
      }
      
      Member memberCondition=new Member();
      memberCondition.setUserId(userId);
      
      Dao<Member> memberDao = DaoFactory.create(Member.class);
      List<Member> members = memberDao.select(memberCondition);

      //当前用户参与的项目id
      Set<Integer> ids=new HashSet<Integer>();
      for(Member member : members){
          int projectId = member.getProjectId();
          if(!projects.containsKey(projectId)){
              if(!ids.contains(projectId)){
                  ids.add(projectId);
              }
          }
      }
      if(!ids.isEmpty()){
          List<Object> temps = dao.getSession().selectList("selectProjects", Arrays.asList(ids.toArray()));
          for(Object temp:temps){
              ownProjects.add((Project)temp);
          }
      }
      
      MessageRequest request=new MessageRequestBuilder().to("$all").channel("web").category("demo").content("每次有人获取自己的项目列表时就会向所有在线用户广播这条消息，看到的请无视!").build();
      MessageSender.getInstance().send(request);
      
      return ownProjects;
	}
	
	/**
	 * 获取项目的基本统计信息(成员个数，任务个数等等)
	 * 
	 * @param projects 项目id
	 * 
	 * @return 项目信息
	 */
	public List<ProjectSimpleStatistics> getProjectStatistics(List<Integer> projectIds){
		Map<String,Object> conditions = new HashMap<String,Object>() ;
		conditions.put("projectIds", projectIds) ;
		return DaoFactory.create(Project.class).getSession().selectList("getProjectStatistics", conditions) ;
 	}
	
	public Page<ProjectSimpleStatistics> getProjectStatisticsByPage(Pageable page){
		Dao<Project> dao = DaoFactory.create(Project.class) ;
		RowBounds4Page rowBounds = new RowBounds4Page(page,null,null,true) ;
		List<ProjectSimpleStatistics> result = dao.getSession().selectList("getProjectStatistics",null,rowBounds) ;
		return new Page<ProjectSimpleStatistics>(page.getPageIndex(),page.getPageSize(),rowBounds.getTotalSize(),result);
	} 
	
	/**
	 * 转让项目
	 * @param projectId 项目ID
	 * @param ownerId 所有者ID
	 */
	@Transactional
	public void transformOwner(int projectId,String ownerId){
		if(null!=ownerId && !ownerId.trim().equals("")){	
		  //转让项目只是转让项目的所有权，成员以及角色信息仍然保留
			/*Dao<Member> memberDao = DaoFactory.create(Member.class);

			Member member = new Member();
			member.setProjectId(projectId);
			member.setUserId(ownerId);

			memberDao.getSession().delete("deleteByPU", member);*/
			
			Dao<Project> projectDao = DaoFactory.create(Project.class);
			
			Project project = new Project();
			project.setId(projectId);
			project.setOwner(ownerId);
			
			projectDao.update(project);
			
			ProjectAuthorizationProvider.removeProjectMap(projectId);
		}
	}
	
	/**
	 * 删除项目
	 * @param projectId 项目ID
	 */
	@Transactional
	public void deleteProject(int projectId){
		
		String[] names = new String[]{
				"deleteMemberByProject",
				"deleteDocFileByProject",
				"deleteDocByProject",
				"deleteModuleByProject",
				"deleteStoryByProject",
				"deleteTaskHistoryByProject",
				"deleteTaskActionByProject",
				"deleteTaskEstimateByProject",
				"deleteTaskByProject",
				"deleteLabelTaskByProject",
				"deleteLabelByProject",
				"deleteMilestoneTaskByProject",
				"deleteMilestoneByProject",
				"deleteTaskSnapshotByProject",
				"deleteWrSubmit2ByProject",
				"deleteWrStatisticsByProject",
				"deleteWrByProject",
				"deletePreferenceByProject",
		};
		
		Dao<Project> dao = DaoFactory.create(Project.class);		
		
		for (String name : names) {
			dao.getSession().delete(name, projectId);	
		}
		
		Project project = new Project();
		project.setId(projectId);
		dao.delete(project);
		
		ProjectAuthorizationProvider.removeProjectMap(projectId);
	}
	
    public String getProjectOwnerIfExist(String code){
        List<Project> projects = DaoFactory.create(Project.class).getSession().selectList("getProjectOwnerIfExist", code) ;
        if(projects.size()==0||"81588".equals(code)){
            return null;
        }else{
            return projects.get(0).getOwner() ;
        }
    }
	
	/**
	 * 归档项目
	 * @param projectId 项目ID
	 */
	public void archiveProject(int projectId){
		
		Dao<Project> dao = DaoFactory.create(Project.class);
		Project project = new Project();
		project.setId(projectId);
		project.setArcStatus("0") ;
		project.setStatus("done");
		
		dao.update(project);
	}
	
	/**
	 * 激活项目
	 * @param projectId 项目ID
	 */
	public void activeProject(int projectId){
		
		Dao<Project> dao = DaoFactory.create(Project.class);
		Project project = new Project();
		project.setId(projectId);
		project.setArcStatus("1") ;
		project.setStatus("doing");
		
		dao.update(project);
	}
	
	/**
	 * 获取当前用户参与的所有项目
	 * @return
	 */
	public List<Project> getAllMyProjects(){
		Project condition=new Project();
		
		String userId = CredentialStore.getCurrCredential().getUserInfo().getId();
		User user = OrgUserService.getUserInfoById(userId);
		String ownerName = user.getEmail().substring(0, user.getEmail().indexOf("@"));
		
		String owner = new MembersMgt().getUserInfoByUsername(ownerName).get("userId") ;
		condition.setOwner(owner);
		
		Dao<Project> dao=DaoFactory.create(Project.class);
		//当前用户创建的项目
		List<Project> ownProjects = dao.select(condition);
		
		HashMap<Integer, Project> projects=new HashMap<Integer, Project>();
		for(Project project:ownProjects){
			projects.put(project.getId(), project);
		}
		
		Member memberCondition=new Member();
		memberCondition.setUserId(owner);
		
		Dao<Member> memberDao = DaoFactory.create(Member.class);
		List<Member> members = memberDao.select(memberCondition);

		//当前用户参与的项目id
		Set<Integer> ids=new HashSet<Integer>();
		for(Member member : members){
			int projectId = member.getProjectId();
			if(!projects.containsKey(projectId)){
				if(!ids.contains(projectId)){
					ids.add(projectId);
				}
			}
		}
		if(!ids.isEmpty()){
			List<Object> temps = dao.getSession().selectList("selectProjects", Arrays.asList(ids.toArray()));
			for(Object temp:temps){
				ownProjects.add((Project)temp);
			}
		}
		
		MessageRequest request=new MessageRequestBuilder().to("$all").channel("web").category("demo").content("每次有人获取自己的项目列表时就会向所有在线用户广播这条消息，看到的请无视!").build();
		MessageSender.getInstance().send(request);
		//对项目进行升序排序
		Collections.sort(ownProjects, new Comparator<Project>(){
            /*
             * int compare(Project p1, Project p2) 返回一个基本类型的整型，
             * 返回负数表示：p1 小于p2，
             * 返回0 表示：p1和p2相等，
             * 返回正数表示：p1大于p2
             */
            public int compare(Project p1, Project p2) {
            	if(p1==null||p2==null) return 0;
                //按照Person的年龄进行升序排列
            	Integer p1code=Integer.valueOf(getNumbers(p1.getCode()));
            	Integer p2code=Integer.valueOf(getNumbers(p2.getCode()));
            	
                if(p1code > p2code){
                    return 1;
                }
                if(p1code == p2code){
                    return 0;
                }
                return -1;
            }
        });
		
		return ownProjects;
	}
	
	
	//截取数字  
	 public String getNumbers(String content) {  
		   String regEx="[^0-9]";
	       Pattern pattern = Pattern.compile(regEx);  
	       Matcher matcher = pattern.matcher(content);  
	       String str= matcher.replaceAll("").trim();
	       if(str.length() == 0)  return "0";
	       else return str;	        
	   }  
	
	/**
	 * 同步erp库项目状态
	 */
	public void synProjectStatus(){
	  this.logger.info("同步开始。。。。"+new Date(System.currentTimeMillis()));
	  lock.lock();
	  try {
	    //初始化数据
	    projectMap = new HashMap<Integer,Project>();
	    erpMap = new HashMap<String,ErpProject>();
	    
	    Dao<ErpProject> erpDao = DaoFactory.create(ErpProject.class);
	    List<ErpProject> erpProjects = erpDao.selectAll();//所有erp库正在进行中的项目
	    for(ErpProject p:erpProjects){
	      if(!erpMap.containsKey(p.getPcode())){
	        erpMap.put(p.getPcode(), p);
	      }
	    }
	    Dao<Project> projectDao = DaoFactory.create(Project.class);
	    List<Project> projects = projectDao.selectAll();//系统中所有的项目
	    for(Project p:projects){
	      if(!projectMap.containsKey(p.getId())){
	        projectMap.put(p.getId(), p);
	      }
	    }

	    List<Project> updateProjects = Lists.newArrayList();
	    //项目不在erp库中的修改为归档状态 合同编号需精准匹配
	    /*for(Integer key:projectMap.keySet()){
	      Project p = projectMap.get(key);
	      if(!erpMap.containsKey(p.getContractNo())){
	        if(!p.getArcStatus().equals("0")){
	          p.setArcStatus("0");
	          p.setStatus("done");
	          updateProjects.add(p);
	        }
	      }else{
	        if(!p.getArcStatus().equals("1")){//在erp库中的设为正在进行中
	          p.setArcStatus("1");
	          p.setStatus("doing");
	          updateProjects.add(p);
	        }
	      }
	    }*/
	    
	    //根据实际情况，调整匹配逻辑，合同编号模糊匹配上即算正在进行中
	    for(Integer key:projectMap.keySet()){
	      Project p = projectMap.get(key);
	      if(StringUtils.isNotBlank(p.getContractNo())){
  	        if(erpMap.containsKey(p.getContractNo())){//首选精准匹配合同号
  	          if(!p.getArcStatus().equals("1")){//在erp库中的设为正在进行中
                p.setArcStatus("1");
                p.setStatus("doing");
                updateProjects.add(p);
              }
            }else{//其次模糊匹配
              //查询到一个匹配自动修改合同编号，多个匹配则设为归档
              List<ErpProject> erps = erpDao.getSession().selectList("selectProject", p.getContractNo());
              if(CollectionUtils.isNotEmpty(erps)){
                if(erps.size()==1){
                  if(p.getArcStatus().equals("0")){//已归档的项目修改为进行中
                    p.setArcStatus("1");
                    p.setStatus("doing");
                    p.setContractNo(erps.get(0).getPcode());
                    updateProjects.add(p);
                  }
                }else{//匹配多个设为归档
                  if(p.getArcStatus().equals("1")){//进行中修改为已归档
                    p.setArcStatus("0");
                    p.setStatus("done");
                    updateProjects.add(p);
                  }
                }
              }else{//匹配不上设为归档
                if(!p.getArcStatus().equals("0")){
                  p.setArcStatus("0");
                  p.setStatus("done");
                  updateProjects.add(p);
                }
              }
            }
	      }
	    }
	    
	    if(updateProjects.size()>0) projectDao.updateBatch(updateProjects);
	  }
	  finally {
	    lock.unlock();
	    this.logger.info("同步完成。。。。"+new Date(System.currentTimeMillis()));
	  }
	}

}
