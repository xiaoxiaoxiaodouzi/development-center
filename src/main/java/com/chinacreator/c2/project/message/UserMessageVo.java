package com.chinacreator.c2.project.message;


public class UserMessageVo {
	
	/**
	 * 发送用户登陆名
	 */
	private String fromUserId;
	
	/**
	 * 发送用户中文名
	 */
	private String fromUserRealname;
	
	/**
	 * 发送用户图标
	 */
	private String fromUserIcon;
	
	/**
	 *标题
	 */
	private String title;

	/**
	 *内容
	 */
	private String content;
	
	
	/**
	 * 链接
	 */
	private String link;


	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getFromUserRealname() {
		return fromUserRealname;
	}

	public void setFromUserRealname(String fromUserRealname) {
		this.fromUserRealname = fromUserRealname;
	}

	public String getFromUserIcon() {
		return fromUserIcon;
	}

	public void setFromUserIcon(String fromUserIcon) {
		this.fromUserIcon = fromUserIcon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	
}
