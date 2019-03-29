package com.chinacreator.c2.api.web.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.chinacreator.c2.context.OperationContextHolder;
import com.chinacreator.c2.context.WebOperationContext;
import com.chinacreator.c2.dao.Dao;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.project.task.Estimate;
import com.chinacreator.c2.web.controller.ResponseFactory;
import com.google.common.base.Preconditions;

@Controller
@RequestMapping(value = "/api")
public class EventController {
	
	private static ResponseFactory factory = new ResponseFactory();
	private static Dao<Estimate> estimateDao = DaoFactory.create(Estimate.class) ;
  
    @RequestMapping(value = "/events",method = RequestMethod.GET)
    public Object doGetMyEvents(@RequestParam("startDate") Long startDate, @RequestParam("endDate") Long endDate) {
    	
    	List<Estimate> result =  new ArrayList<Estimate>() ;
    	try {
        	Preconditions.checkNotNull(startDate, "起始时间不能为空!") ;
        	Preconditions.checkNotNull(endDate, "截止时间不能为空!") ;
        	
			String userName=((WebOperationContext)OperationContextHolder.getContext()).getUser().getName();
			Map<String,Object> condition = new HashMap<String,Object>() ;
			condition.put("startDate", new Date(startDate)) ;
			condition.put("endDate", new Date(endDate)) ;
			condition.put("userName", userName) ;
			result = estimateDao.getSession().selectList("com.chinacreator.c2.project.task.EstimateMapper.selectEvents", condition);
		} catch (Exception e) {
			return factory.createResponseBodyException(e) ;
		}
        return factory.createResponseBodyJSONObject(result) ;
    }

}
