package com.chinacreator.c2.project.log;

/**
 * 	Action类型枚举
 * 
 * @author tyf
 *
 */
public enum ActionTypes {
	
	TASK_ADD("opend","创建了任务 %s","md-crop-3-2"),
	TASK_START("started","启动了任务%s","md-play-arrow"),
	TASK_DELETE("deleted","删除了任务%s"," md-stop"),
	TASK_FINISH("finished","完成了任务  %s"," md-stop"),
	TASK_CLOSED("closed","关闭了任务  %s"," md-closed"),
	TASK_OPEN("closed","重新打开了任务  %s"," md-open"),
	TASK_EDIT("edited","更新 %s","md-mode-edit"),
	TASK_CONFIRM("confirmed","确认了任务 %s","md-lens"),
	TASK_UNCONFIRM("unconfirmed","取消了任务确认 %s","md-panorama-fisheye"),
	
	TASK_RECORD_ESTIMATE("recordestimate","记录了%s工时  %s","md-insert-invitation"),
	TASK_DELETE_ESTIMATE("deleteestimate","删除了%s工时 %s","md-insert-invitation"),
	TASK_EDIT_ESTIMATE("editestimate","编辑了%s工时","md-insert-invitation") ;
	
	private final String colVal;  
	private final String comment;
	private final String cssName;
	
	private ActionTypes(String colVal, String comment , String cssName) {
            this.colVal = colVal;
            this.comment = comment;
            this.cssName = cssName ;
   }

	/**
	 * 获取action类型
	 * @return
	 */
	public String getColVal() {
		return colVal;
	}

	/**
	 * 获取action类型对应的描述信息
	 * @return
	 */
	public String getComment() {
		return comment;
	}
	
	public String getCssName(){
		return cssName;
	}
	
	
	
}
