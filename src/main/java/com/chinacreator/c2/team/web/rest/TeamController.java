package com.chinacreator.c2.team.web.rest;

import java.util.List;
import java.util.Map;
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
import com.chinacreator.c2.dao.mybatis.enhance.Page;
import com.chinacreator.c2.project.info.Project;
import com.chinacreator.c2.team.Team;
import com.chinacreator.c2.team.TeamWeekReportSubmit;
import com.chinacreator.c2.team.service.TeamService;

@Controller
@Path("team/v1")
@Consumes(MediaType.APPLICATION_JSON)
public class TeamController {
  @Autowired
  private TeamService teamService;

  @POST
  @Path("addTeam")
  public Team insert(Team entity) {
    //TODO auto-generated method stub	
    return teamService.insert(entity);
  }
  
  @POST
  @Path("/{teamId}")
  public Team update(@PathParam(value="teamId")int teamId,Team entity){
    return teamService.update(teamId,entity);
  }

  @GET
  @Path("getTeam")
  public List<Map<String,Object>> getList(@QueryParam(value = "owner") String owner) {
    //TODO auto-generated method stub
    return teamService.getListByPage(owner);
  }
  
  @GET
  @Path("teams")
  public Page<Map<String,Object>> getTeamByPage(@QueryParam(value = "owner") String owner,@QueryParam(value="page")int page){
    return teamService.getTeamByPage(owner,page);
  }
  
  @GET
  @Path("getAuthTeam")
  public List<Map<String,Object>> getAuthList(@QueryParam(value = "owner") String owner){
    return teamService.getAuthList(owner);
  }
  
  @GET
  @Path("getMembers")
  public List<Map<String,String>> getMembers(@QueryParam(value="orgId") String orgId,@QueryParam(value="name") String name){
    return teamService.getMembers(orgId,name);
  }
  
  @GET
  @Path("/{teamId}/users")
  public List<Map<String,String>> getTeamUser(@PathParam(value="teamId")Integer teamId){
    return teamService.getTeamUser(teamId);
  }
  
  @PUT
  @Path("/{teamId}/users")
  public void deleteTeamUser(@PathParam(value="teamId")Integer teamId,List<String> userNames){
    teamService.deleteTeamUser(teamId,userNames);
  }
  
  @POST
  @Path("/{teamId}/users")
  public void addTeamUser(@PathParam(value="teamId")Integer teamId,List<String> userNames){
    teamService.addTeamUser(teamId,userNames);
  }
  
  @POST
  @Path("/{teamId}/transform")
  public void transformOwner(@PathParam(value="teamId")Integer teamId,@QueryParam(value="owner")String owner){
    teamService.transformOwner(teamId,owner);
  }
  
  @GET
  @Path("/{teamId}/submit")
  public List<Map<String,Object>> getWeekReportSubmit2(@PathParam(value="teamId")Integer teamId){
    return teamService.getWeekReportSubmit2(teamId);
  }
  
  @POST
  @Path("/{teamId}/submit")
  public void setSubmit2(@PathParam(value="teamId")Integer teamId,List<TeamWeekReportSubmit> submits){
    teamService.setSubmit2(teamId,submits);
  }
  
  @GET
  @Path("/{teamId}/preference")
  public Map<String, Object> getTeamPreference(@PathParam(value="teamId")Integer teamId){
    return teamService.getTeamPreference(teamId);
  }
  
  @GET
  @Path("/{reportId}/projects")
  public List<Project> getTeamProject(@PathParam(value="reportId")Integer reportId,@QueryParam(value="belongToNextWeek")boolean belongToNextWeek){
    return teamService.getTeamProject(reportId,belongToNextWeek);
  }
  
  @DELETE
  @Path("/{teamId}")
  public void deleteTeam(@PathParam(value="teamId")Integer teamId){
    teamService.deleteTeam(teamId);
  }
  
  @GET
  @Path("/{teamId}")
  public Team getTeamById(@PathParam(value="teamId")Integer teamId){
    return teamService.getTeamById(teamId);
  }
}
