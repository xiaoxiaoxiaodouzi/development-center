package com.chinacreator.c2.project.weeklyReport.exception.copy;

import com.chinacreator.c2.exception.C2BusinessException;

public class WeekReportPersistenceException extends RuntimeException implements C2BusinessException{

	private static final long serialVersionUID = -7769608136983112289L;

	public WeekReportPersistenceException(String message) {
		super(message) ;
	}

	public WeekReportPersistenceException(String message, Throwable throwable) {
		super(message,throwable) ;
	}

}
