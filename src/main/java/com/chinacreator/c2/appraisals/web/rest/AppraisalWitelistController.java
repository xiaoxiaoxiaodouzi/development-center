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

import com.chinacreator.c2.appraisals.AppraisalWitelist;
import com.chinacreator.c2.appraisals.service.AppraisalWitelistService;
import com.chinacreator.c2.dao.mybatis.enhance.Page;

@Controller
@Path("project/v1/")
@Consumes(MediaType.APPLICATION_JSON)
public class AppraisalWitelistController {
	@Autowired
	private AppraisalWitelistService appraisalWitelistService;

	@POST
	@Path("appraisalwhitelists/{id}")
	public AppraisalWitelist update(AppraisalWitelist entity) {
		//TODO auto-generated method stub	
		return appraisalWitelistService.update(entity);
	}

	@POST
	@Path("appraisalwhitelists")
	public AppraisalWitelist insert(AppraisalWitelist entity) {
		//TODO auto-generated method stub	
		return appraisalWitelistService.insert(entity);
	}

	@GET
	@Path("appraisalwhitelists/{id}")
	public AppraisalWitelist get(@PathParam(value = "id") java.lang.String id) {
		//TODO auto-generated method stub
		return appraisalWitelistService.get(id);
	}

	@DELETE
	@Path("appraisalwhitelists")
	public void deleteBatch(@QueryParam(value = "id") List<java.lang.String> ids) {
		//TODO auto-generated method stub
		appraisalWitelistService.deleteBatch(ids);
	}

	@DELETE
	@Path("appraisalwhitelists/{id}")
	public void delete(@PathParam(value = "id") java.lang.String id) {
		//TODO auto-generated method stub
		appraisalWitelistService.delete(id);
	}

	@GET
	@Path("appraisalwhitelists")
	public Page<AppraisalWitelist> getListByPage(@QueryParam(value = "page") int page,
			@QueryParam(value = "rows") int rows, @QueryParam(value = "sidx") String sidx,
			@QueryParam(value = "sord") String sord, @QueryParam(value = "cond") String cond) {
		//TODO auto-generated method stub
		return appraisalWitelistService.getListByPage(page, rows, sidx, sord, cond);

	}
}
