package com.chinacreator.c2.project.story;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import com.chinacreator.c2.uop.User;

public class StoryDTO extends Story {
	
	public StoryDTO(Story story) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		super();
		PropertyUtils.copyProperties(this, story);
	}
	
	private User openedByUser;
	
	public User getOpenedByUser() {
		return openedByUser;
	}

	public void setOpenedByUser(User openedByUser) {
		this.openedByUser = openedByUser;
	}
	
	
}
