package com.chinacreator.c2.appraisals.web.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.chinacreator.c2.appraisals.Assessor;
import com.chinacreator.c2.appraisals.service.AssessorService;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.uop.OrgUserService;
import com.chinacreator.c2.uop.User;

@Controller
@Path("project/v1/")
@Consumes(MediaType.APPLICATION_JSON)
public class AssessorController {
	@Autowired
	private AssessorService assessorService;
	
	@GET
	@Path("getDeptUsers")
	public List<User> getDeptUsers(@QueryParam(value="orgId")String orgId){
		return assessorService.getDeptUsers(orgId);
	}
	
	@GET
	@Path("users")
	public Page<User> getUsers(@QueryParam(value="name")String name,@QueryParam(value="page")int page,@QueryParam(value="rows")int rows){
		return OrgUserService.getManagerUser(name, page, rows);
	}

	@POST
	@Path("assessors/{id}")
	public Assessor update(Assessor entity) {
		//TODO auto-generated method stub	
		return assessorService.update(entity);
	}

	@POST
	@Path("assessors")
	public Assessor insert(Assessor entity) {
		//TODO auto-generated method stub	
		return assessorService.insert(entity);
	}

	@GET
	@Path("assessors/{id}")
	public Assessor get(@PathParam(value = "id") java.lang.String id) {
		//TODO auto-generated method stub
		return assessorService.get(id);
	}
	
	@GET
	@Path("assessors/user/{userId}")
	public List<Assessor> getAssessorByuserId(@PathParam(value = "userId") java.lang.String userId) {
		//TODO auto-generated method stub
		return assessorService.getAssessorByuserId(userId);
	}

	@DELETE
	@Path("assessors")
	public void deleteBatch(@QueryParam(value = "id") List<java.lang.String> ids) {
		//TODO auto-generated method stub
		assessorService.deleteBatch(ids);
	}

	@DELETE
	@Path("assessors/{id}")
	public void delete(@PathParam(value = "id") java.lang.String id) {
		//TODO auto-generated method stub
		assessorService.delete(id);
	}

	@GET
	@Path("assessors")
	public Page<Assessor> getListByPage(@QueryParam(value = "page") int page, @QueryParam(value = "rows") int rows,
			@QueryParam(value = "orgId") String orgId,@QueryParam(value = "name") String name) {
		//TODO auto-generated method stub
		return assessorService.getListByPage(page, rows, orgId,name);

	}
}
