package com.chinacreator.c2.project.weeklyReport;

import java.io.Serializable;
import java.util.List;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 任务快照扩展
 */
public class TaskSnapshotEx extends TaskSnapshot implements Serializable {
	private static final long serialVersionUID = 8881942176855478321L;
	private String userRealname;
	private String userIcon;
	private Integer moduleId;
	private Integer storyId;
	public String getUserRealname() {
		return userRealname;
	}
	public void setUserRealname(String userRealname) {
		this.userRealname = userRealname;
	}
	public String getUserIcon() {
		return userIcon;
	}
	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}
	
	public Integer getModuleId(){
		return this.moduleId;
	}
	public void setModuleId(Integer moduleId){
		this.moduleId=moduleId;
	}
	public Integer getStoryId() {
		return storyId;
	}
	public void setStoryId(Integer storyId) {
		this.storyId = storyId;
	}
	
}
