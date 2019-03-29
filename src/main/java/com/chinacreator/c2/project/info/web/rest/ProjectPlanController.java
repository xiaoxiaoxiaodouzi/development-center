package com.chinacreator.c2.project.info.web.rest;

import java.util.List;
import java.util.Map;

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

import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.project.info.ProjectPlan;
import com.chinacreator.c2.project.info.service.ProjectPlanService;

@Controller
@Path("ProjectPlan")
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectPlanController {
	@Autowired
	private ProjectPlanService projectPlanService;

	@POST
	@Path("/plan/{id}")
	public ProjectPlan update(ProjectPlan entity) {
		// TODO auto-generated method stub
		return projectPlanService.update(entity);
	}

	@POST
	@Path("/plan")
	public ProjectPlan insert(ProjectPlan projectPlan) {
		// TODO auto-generated method stub
		return projectPlanService.insert(projectPlan);
	}

	@GET
	@Path("/{id}")
	public ProjectPlan get(@PathParam(value = "id") java.lang.Integer id) {
		// TODO auto-generated method stub
		return projectPlanService.get(id);
	}

	@GET
	@Path("/project")
	public List<ProjectPlan> getPlanByprojectId(@QueryParam(value = "projectId") java.lang.Integer projectId,
			@QueryParam(value = "parent") java.lang.Integer parent,
			@QueryParam(value = "search") Map<String, Object> search) {
		// TODO auto-generated method stub
		return projectPlanService.getPlanByProjectId(projectId, parent, search);
	}

	@GET
	@Path("all/project")
	public List<ProjectPlan> getPlanByproject(@QueryParam(value = "projectId") java.lang.Integer projectId,
			@QueryParam(value = "parent") java.lang.Integer parent,
			@QueryParam(value = "search") Map<String, Object> search) {
		// TODO auto-generated method stub
		return projectPlanService.getPlanByproject(projectId, parent, search);
	}

	@DELETE
	@Path("")
	public void deleteBatch(@QueryParam(value = "id") List<java.lang.Integer> ids) {
		// TODO auto-generated method stub
		projectPlanService.deleteBatch(ids);
	}

	@DELETE
	@Path("/{id}")
	public void delete(@PathParam(value = "id") java.lang.Integer id) {
		// TODO auto-generated method stub
		projectPlanService.delete(id);
	}

	@GET
	@Path("/page")
	public Page<ProjectPlan> getListByPage(@QueryParam(value = "page") int page, @QueryParam(value = "rows") int rows,
			@QueryParam(value = "sidx") String sidx, @QueryParam(value = "sord") String sord,
			@QueryParam(value = "cond") String cond) {
		// TODO auto-generated method stub
		return projectPlanService.getListByPage(page, rows, sidx, sord, cond);

	}
}