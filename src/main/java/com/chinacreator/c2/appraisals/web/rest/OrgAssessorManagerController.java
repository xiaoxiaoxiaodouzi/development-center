package com.chinacreator.c2.appraisals.web.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.chinacreator.c2.appraisals.OrgAssessorManager;
import com.chinacreator.c2.appraisals.service.OrgAssessorManagerService;
import com.chinacreator.c2.dao.mybatis.enhance.Page;

@Controller
@Path("project/v1/")
@Consumes(MediaType.APPLICATION_JSON)
public class OrgAssessorManagerController {
	@Autowired
	private OrgAssessorManagerService orgAssessorManagerService;

	@POST
	@Path("orgassessormanagers/{id}")
	public OrgAssessorManager update(OrgAssessorManager entity) {
		//TODO auto-generated method stub	
		return orgAssessorManagerService.update(entity);
	}
	
	@PUT
	@Path("orgassessormanagers/{orgId}")
	public int updateBatchByOrgId(@PathParam(value="orgId")String orgId,List<OrgAssessorManager> entity) {
		//TODO auto-generated method stub	
		return orgAssessorManagerService.updateBatchByOrgId(orgId,entity);
	}


	@POST
	@Path("orgassessormanagers")
	public List<OrgAssessorManager> insert(List<OrgAssessorManager> entity) {
		//TODO auto-generated method stub	
		return orgAssessorManagerService.insert(entity);
	}

	@GET
	@Path("orgassessormanagers/{id}")
	public OrgAssessorManager get(@PathParam(value = "id") java.lang.String id) {
		//TODO auto-generated method stub
		return orgAssessorManagerService.get(id);
	}

	@DELETE
	@Path("orgassessormanagers")
	public void deleteBatch(@QueryParam(value = "id") List<java.lang.String> ids) {
		//TODO auto-generated method stub
		orgAssessorManagerService.deleteBatch(ids);
	}

	@DELETE
	@Path("orgassessormanagers/{id}")
	public void delete(@PathParam(value = "id") java.lang.String id) {
		//TODO auto-generated method stub
		orgAssessorManagerService.delete(id);
	}

	@GET
	@Path("orgassessormanagers")
	public Page<OrgAssessorManager> getListByPage(@QueryParam(value = "page") int page,
			@QueryParam(value = "rows") int rows, @QueryParam(value = "sidx") String sidx,
			@QueryParam(value = "sord") String sord, @QueryParam(value = "cond") String cond) {
		//TODO auto-generated method stub
		return orgAssessorManagerService.getListByPage(page, rows, sidx, sord, cond);

	}
}
