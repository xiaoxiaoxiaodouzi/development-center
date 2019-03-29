package com.chinacreator.c2.project.preference.exception;

import com.chinacreator.c2.exception.C2BusinessException;

public class InvalidPreferenceParamsException extends RuntimeException implements C2BusinessException{

	private static final long serialVersionUID = 7105584890617951634L;

	public InvalidPreferenceParamsException(String message) {
		super(message) ;
	}

	public InvalidPreferenceParamsException(String message, Throwable throwable) {
		super(message,throwable) ;
	}

}

