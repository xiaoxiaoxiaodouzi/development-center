package com.chinacreator.c2.project.service;

/**
 *	项目基本统计信息
 */
public class ProjectSimpleStatistics {
	
	private int projectId ;
	
	private String projectName ;
	
	private String status;
	
	private int memberNum;
	
	private int taskNum;
	
	private int finishedTaskNum;
	
	private Long bugNum;
	
	private Long closedBugNum;
	
	private int storyNum ;
	
	private int finishedStoryNum ;
	
	private int milestoneNum ;
	
	private int finishedMilestoneNum ;
	
	private String pic ;
	
	/**
	 *  工时总数
	 */
	private double estimateCount ;
	
	/**
	 *  已消耗工时总数
	 */
	private double consumedCount ;

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}


	public double getEstimateCount() {
		return estimateCount;
	}

	public void setEstimateCount(double estimateCount) {
		this.estimateCount = estimateCount;
	}

	public double getConsumedCount() {
		return consumedCount;
	}

	public void setConsumedCount(double consumedCount) {
		this.consumedCount = consumedCount;
	}

	public int getMemberNum() {
		return memberNum;
	}

	public void setMemberNum(int memberNum) {
		this.memberNum = memberNum;
	}

	public int getTaskNum() {
		return taskNum;
	}

	public void setTaskNum(int taskNum) {
		this.taskNum = taskNum;
	}

	public int getFinishedTaskNum() {
		return finishedTaskNum;
	}

	public void setFinishedTaskNum(int finishedTaskNum) {
		this.finishedTaskNum = finishedTaskNum;
	}

	public Long getBugNum() {
		return bugNum;
	}

	public void setBugNum(Long bugNum) {
		this.bugNum = bugNum;
	}

	public Long getClosedBugNum() {
		return closedBugNum;
	}

	public void setClosedBugNum(Long closedBugNum) {
		this.closedBugNum = closedBugNum;
	}

	public int getStoryNum() {
		return storyNum;
	}

	public void setStoryNum(int storyNum) {
		this.storyNum = storyNum;
	}

	public int getFinishedStoryNum() {
		return finishedStoryNum;
	}

	public void setFinishedStoryNum(int finishedStoryNum) {
		this.finishedStoryNum = finishedStoryNum;
	}

	public int getMilestoneNum() {
		return milestoneNum;
	}

	public void setMilestoneNum(int milestoneNum) {
		this.milestoneNum = milestoneNum;
	}

	public int getFinishedMilestoneNum() {
		return finishedMilestoneNum;
	}

	public void setFinishedMilestoneNum(int finishedMilestoneNum) {
		this.finishedMilestoneNum = finishedMilestoneNum;
	}

	
}
