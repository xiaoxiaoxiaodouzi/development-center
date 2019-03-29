package com.chinacreator.c2.project.log.exception;

public class LogPersisException extends RuntimeException {

	private static final long serialVersionUID = 9218005699346605412L;
	
	public LogPersisException(String message) {
		super(message) ;
	}

	public LogPersisException(String message, Throwable throwable) {
		super(message,throwable) ;
	}

}
