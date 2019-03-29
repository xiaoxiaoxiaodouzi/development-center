package com.chinacreator.c2.search;

import com.chinacreator.c2.exception.C2BusinessException;

public class C2ElasticException extends RuntimeException implements C2BusinessException{

	private static final long serialVersionUID = 1L;
	
	public C2ElasticException(String message) {
		super(message) ;
	}

}
