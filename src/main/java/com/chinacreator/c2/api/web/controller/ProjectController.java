package com.chinacreator.c2.api.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.chinacreator.c2.api.bean.Project;
import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.project.info.MembersMgt;
import com.chinacreator.c2.project.info.UserPreferences;
import com.chinacreator.c2.project.preference.UserPreferenceService;
import com.chinacreator.c2.project.service.ProjectSimpleStatistics;
import com.chinacreator.c2.web.controller.ResponseFactory;

@Controller
@RequestMapping(value = "/api")
public class ProjectController {
	
	private static Dao<Project> projectDao = DaoFactory.create(Project.class) ;
	private static UserPreferenceService preference = new UserPreferenceService() ;
	private static ResponseFactory factory = new ResponseFactory();
  
    @RequestMapping(value = "/projects",method = RequestMethod.GET)
    public Object doGetMyProjects() {
    	String ownerName=((WebOperationContext)OperationContextHolder.getContext()).getUser().getName() ;
		String owner = new MembersMgt().getUserInfoByUsername(ownerName).get("userId") ;
		
    	List<Project> projects = projectDao.getSession().selectList("com.chinacreator.c2.api.project.ProjectMapper.getMyProjectsInfo", owner);
    	Map<String,Project> result = new HashMap<String,Project>() ;
    	for (Project project : projects) {
    		result.put(String.valueOf(project.getId()), project) ;
		}
    	
    	Map<String, UserPreferences> prefer = preference.getUserPreferences("starProject");
    	List<String> starList = new ArrayList<String>();
    	if(null !=prefer && null!=prefer.get("starProject")){
    		if(StringUtils.isNotBlank(prefer.get("starProject").getInfoContent())){
    			starList = Arrays.asList(prefer.get("starProject").getInfoContent().split(",")) ;
    		}
    	}
    	Map<String,Object> condition = new HashMap<String,Object>() ;
    	
    	condition.put("projectIds", result.keySet()) ;
    	List<ProjectSimpleStatistics> statistics = projectDao.getSession().selectList("getProjectStatistics", condition) ;
    	List<Project> results = new LinkedList<Project>() ;
    	for (ProjectSimpleStatistics statistic : statistics) {
    		String projectId = String.valueOf(statistic.getProjectId()) ;
    		Project project = result.get(projectId) ;
    		project.setStatistic(statistic) ;
			if(starList.contains(projectId)){
				project.setStar(true) ;
				results.add(0, project);
			}else{
				results.add(project) ;
			}
			
		}
    	
        return factory.createResponseBodyJSONObject(results) ;
    }

}
