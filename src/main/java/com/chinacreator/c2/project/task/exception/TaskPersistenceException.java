package com.chinacreator.c2.project.task.exception;

import com.chinacreator.c2.exception.C2BusinessException;

public class TaskPersistenceException extends RuntimeException implements C2BusinessException{
	
	private static final long serialVersionUID = -4527947779675611574L;

	public TaskPersistenceException(String message) {
		super(message) ;
	}

	public TaskPersistenceException(String message, Throwable throwable) {
		super(message,throwable) ;
	}

}
