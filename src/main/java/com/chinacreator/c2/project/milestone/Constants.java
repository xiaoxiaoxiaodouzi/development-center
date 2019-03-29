package com.chinacreator.c2.project.milestone;

import java.util.HashMap;
import java.util.Map;

public class Constants {
	public static enum MilestoneEntity{
		title("title","标题"),
		description("description","描述"),
		enddate("enddate","截止日期"),
		status("status","状态");
		private String val;
		private String name;
		private MilestoneEntity(String val, String name) {
			this.val = val;
			this.name = name;
		}
		public String getVal() {
			return val;
		}
		public void setVal(String val) {
			this.val = val;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public static Map<String,String> toMap(){
			Map<String,String> returnMap=new HashMap<String,String>();
			for (MilestoneEntity m : MilestoneEntity.values()) {
				returnMap.put(m.getVal(), m.getName());
			}
			return returnMap ;
		}
	};
	public static enum MilestoneTargetType{
		UPDATE("milestone_update","修改里程碑"),
		UPDATE_STATUS("milestone_update_status","修改里程碑状态"),
		UPDATE_STORY("milestone_story_update","修改需求"),
		DELETE_STORY("milestone_story_delete","删除需求"),
		OPENED_STORY("milestone_story_opened","打开需求"),
		CLOSED_STORY("milestone_story_closed","关闭需求"),
		CONFIRM_STORY("milesone_story_confirmed","确认需求");
		
		private String val;
		private String name;
		private MilestoneTargetType(String val, String name) {
			this.val = val;
			this.name = name;
		}
		public String getVal() {
			return val;
		}
		public void setVal(String val) {
			this.val = val;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public static Map toMap(){
			Map returnMap=new HashMap();
			for (MilestoneTargetType m : MilestoneTargetType.values()) {
				returnMap.put(m.getVal(), m.getName());
			}
			return returnMap ;
		}
	};
	public static enum MilestoneStatus{
		NEW("0","新建"),
		OPENED("1","确认"),
		CLOSED("2","关闭");
		private String val;
		private String name;
		private MilestoneStatus(String val, String name) {
			this.val = val;
			this.name = name;
		}
		public String getVal() {
			return val;
		}
		public void setVal(String val) {
			this.val = val;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public static Map<String,String> toMap(){
			Map<String,String> returnMap=new HashMap<String,String>();
			for (MilestoneStatus m : MilestoneStatus.values()) {
				returnMap.put(m.getVal(), m.getName());
			}
			return returnMap ;
		}
	};
}
