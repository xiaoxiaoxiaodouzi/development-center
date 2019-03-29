package com.chinacreator.c2.appraisals.web.rest;

import java.text.ParseException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.chinacreator.c2.appraisals.Appraisals;
import com.chinacreator.c2.appraisals.service.AppraisalsService;
import com.chinacreator.c2.dao.DaoFactory;
import com.chinacreator.c2.dao.mybatis.enhance.Conditions;
import com.chinacreator.c2.dao.mybatis.enhance.Order;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.dao.mybatis.enhance.Rule;
import com.chinacreator.c2.dao.mybatis.enhance.Sortable;
import com.chinacreator.c2.web.ds.impl.DynamicTreeNode;

@Controller
@Path("projects/v1/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AppraisalsController {
	@Autowired
	private AppraisalsService appraisalsService;
	
	@GET
	@Path("appraisalsOrgs")
	public List<DynamicTreeNode> getAppraisalsOrgs(){
		return appraisalsService.getAppraisalsOrgs();
	}

	@POST
	@Path("appraisals/{id}")
	public Appraisals update(Appraisals entity) {
		//TODO auto-generated method stub	
		return appraisalsService.update(entity);
	}

	@POST
	@Path("appraisals")
	public Appraisals insert(Appraisals entity) {
		//TODO auto-generated method stub	
		return appraisalsService.insert(entity);
	}

	@GET
	@Path("appraisals/{id}")
	public Appraisals get(@PathParam(value = "id") java.lang.String id) {
		//TODO auto-generated method stub
		return appraisalsService.get(id);
	}

	@DELETE
	@Path("appraisals")
	public void deleteBatch(@QueryParam(value = "id") List<java.lang.String> ids) {
		//TODO auto-generated method stub
		appraisalsService.deleteBatch(ids);
	}

	@DELETE
	@Path("appraisals/{id}")
	public void delete(@PathParam(value = "id") java.lang.String id) {
		//TODO auto-generated method stub
		appraisalsService.delete(id);
	}

	@GET
	@Path("appraisals")
	public Page<Appraisals> getListByPage(@QueryParam(value = "page") int page, @QueryParam(value = "rows") int rows,
			@QueryParam(value = "order") String order,
			@QueryParam(value = "orgId") String orgId,@QueryParam(value = "userId") List<String> userIds,
			@QueryParam(value = "startYear") int startYear,@QueryParam(value = "endYear") int endYear,
			@QueryParam(value = "startMonth") int startMonth,@QueryParam(value = "endMonth") int endMonth,
			@QueryParam(value = "status") String status,@QueryParam(value = "isManagedOrg") boolean isManagedOrg) throws ParseException {
		//TODO auto-generated method stub
		if(userIds!=null && userIds.size()!=0){
			return appraisalsService.qeuryAppraisalsByUserIds(page, rows, order,startYear,endYear,startMonth,endMonth,status,orgId,isManagedOrg,userIds);
		}
		return appraisalsService.getListByPage(page, rows, order, startYear,endYear,startMonth,endMonth,status,orgId,isManagedOrg);
	}
	
	@GET
	@Path("appraisalusers")
	public List<Appraisals> queryAppraisalUsers(
			@QueryParam(value = "orgId") String orgId,
			@QueryParam(value = "startYear") int startYear,@QueryParam(value = "endYear") int endYear,
			@QueryParam(value = "startMonth") int startMonth,@QueryParam(value = "endMonth") int endMonth,
			@QueryParam(value = "status") String status,@QueryParam(value = "isManagedOrg") boolean isManagedOrg) throws ParseException {
		//TODO auto-generated method stub
		return appraisalsService.queryAppraisalUsers(startYear,endYear,startMonth,endMonth,status,orgId,isManagedOrg);
	}
	
	@GET
	@Path("appraisaluserdate/{userId}")
	public List<Appraisals> queryAppraisalUserDates(
			@PathParam(value = "userId") String userId){
		return DaoFactory.create(Appraisals.class).selectByCondition(new Conditions("AND")
				.append(new Rule("userId","eq",userId)), new Sortable(new Order("date", "desc")));
	}
	
}
