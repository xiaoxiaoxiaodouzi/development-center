package com.chinacreator.c2.project.story;

/**
 * Action类型枚举
 * @author hushow
 *
 */
public enum StoryActionType {
	
	STORY_OPENED("story_opened"),
	STORY_CLOSED("story_closed"),
	STORY_UPDATE("story_update"),
	STORY_COMMENT("story_comment"),
	STORY_DELETE("story_delete"),
	STORY_CONFIRM("story_confirm");
	
	private final String key;
	
	private StoryActionType(String key) {
            this.key = key;
   }

	/**
	 * 获取action类型key
	 * @return
	 */
	public String getKey() {
		return key;
	}
	
}
