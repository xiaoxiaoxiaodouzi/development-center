/**
 * 
 */
package com.chinacreator.c2.project.constant;

/**
 * 系统常量类
 * 
 * @author tbw
 */
public class Const {
	/**
	 * 问题状态
	 */
	public static enum ISSUE_STATUS {
		OPEND("打开", "1"), CLOSED("关闭", "2");
		public String name;
		public String value;

		private ISSUE_STATUS(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String toString() {
			return this.value;
		}
	}

	/**
	 * 任务状态
	 */
	public static enum TASK_STATUS {
		DOING("进行中", "doing"), DONE("已完成", "done");
		public String name;
		public String value;

		private TASK_STATUS(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String toString() {
			return this.value;
		}
	}

	/**
	 * 周报状态
	 */
	public static enum WEEKLY_REPORT {
		NORMAL("正常", "1"), DELAY("延迟", "2");
		public String name;
		public String value;

		private WEEKLY_REPORT(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String toString() {
			return this.value;
		}
	}

	/**
	 * 发布状态
	 */
	public static enum PUBLISH {
		OFFLINE("未发布", "1"), ONLINE("已发布", "2");
		public String name;
		public String value;

		private PUBLISH(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String toString() {
			return this.value;
		}
	}
	
	/**
	 * 项目状态
	 */
	public static enum PROJECT {
		WAIT("未发布", "WAIT"), DOING("进行中", "DOING"), DONE("已完成", "DONE");
		public String name;
		public String value;

		private PROJECT(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String toString() {
			return this.value;
		}
	}
	
	/**
	 * 归档状态
	 */
	public static enum ARCHIVE {
		ACTIVE("激活", "1"), ARCHIVED("已归档", "2");
		public String name;
		public String value;

		private ARCHIVE(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String toString() {
			return this.value;
		}
	}

	/**
	 * 需求状态
	 */
	public static enum STORY {
		NORMAL("正常", "1"), CLOSED("关闭", "2");
		public String name;
		public String value;

		private STORY(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String toString() {
			return this.value;
		}
	}
}
