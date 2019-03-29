package com.chinacreator.c2.team.web.rest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.team.service.TeamWeekReportService;

@Controller
@Path("team/v1/{teamId}/reports")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TeamWeekReportController {
  
  @Autowired
  private TeamWeekReportService teamWeekReportService;

  @GET
  @Path("")
  public Page<Map<String,Object>> getListByPage(@QueryParam(value = "page") int page,
      @QueryParam(value = "owner") String owner, @PathParam(value = "teamId") int teamId) {
    return teamWeekReportService.getListByPage(page, owner, teamId);
  }
  
  @POST
  @Path("")
  public void addTeamWeekReport(@PathParam(value = "teamId") int teamId,Map<String,Object> params) {
    teamWeekReportService.addTeamWeekReport(teamId,params);

  }
  
  @GET
  @Path("/{reportId}")
  public Map<String,Object> getWeekReportInfo(@PathParam(value = "teamId") int teamId,@PathParam(value="reportId")String reportId){
    return teamWeekReportService.getWeekReportInfo(reportId);
  }
  
  @GET
  @Path("/{reportId}/taskSnapshots")
  public List<Map<String,Object>> getWeekReportTaskSnapshots(@PathParam(value="reportId")String reportId,
      @QueryParam(value="belongToNextWeek")boolean belongToNextWeek,@PathParam(value = "teamId") String teamId){
    return teamWeekReportService.getWeekReportTaskSnapshots(reportId,belongToNextWeek,teamId);
  }
  
  @GET
  @Path("{reportId}/nextWeekTask")
  public Collection<Object> getNextWeekTask(@PathParam(value="reportId")String reportId,@PathParam(value = "teamId") int teamId,
      @QueryParam(value="startParam") String startParam,@QueryParam(value="endParam") String endParam){
    return teamWeekReportService.getNextWeekTask(reportId,startParam,endParam,teamId);
  }
  
  @GET
  @Path("{reportId}/nextWeekProjects")
  public List<Project> getNextWeekProject(@PathParam(value="reportId")String reportId,@PathParam(value = "teamId") int teamId,
      @QueryParam(value="startParam") String startParam,@QueryParam(value="endParam") String endParam){
    return teamWeekReportService.getNextWeekProject(reportId,startParam,endParam,teamId);
  }
  
  @GET
  @Path("{reportId}/tasks")
  public Map<String,Object> getWeeklyReportTasks(@PathParam(value="reportId")String reportId,@PathParam(value = "teamId") int teamId,
     @QueryParam(value="startParam") String startParam,@QueryParam(value="endParam") String endParam){
    return teamWeekReportService.getWeeklyReportTasks(reportId,startParam,endParam,teamId);
  }
  
  @GET
  @Path("{reportId}/logs")
  public Collection<Object> getWeeklyReportLogs(@PathParam(value="reportId")String reportId,@PathParam(value = "teamId") int teamId,
     @QueryParam(value="st") String st,@QueryParam(value="et") String et){
    return teamWeekReportService.getWeeklyReportLogs(reportId,st,et,teamId);
  }
}
