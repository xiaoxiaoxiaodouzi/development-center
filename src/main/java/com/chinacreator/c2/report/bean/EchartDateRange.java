package com.chinacreator.c2.report.bean;

public class EchartDateRange {
	
	private Integer year ;
	
	private Integer weekStart ;
	
	private Integer weekEnd ;
	
	public EchartDateRange(Integer year, Integer weekStart, Integer weekEnd) {
		super();
		this.year = year;
		this.weekStart = weekStart;
		this.weekEnd = weekEnd;
	}

	public EchartDateRange(Integer year) {
		super();
		this.year = year;
	}
	
	public EchartDateRange(Integer year, Integer weekStart) {
		super();
		this.year = year;
		this.weekStart = weekStart;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getWeekStart() {
		return weekStart;
	}

	public void setWeekStart(Integer weekStart) {
		this.weekStart = weekStart;
	}

	public Integer getWeekEnd() {
		return weekEnd;
	}

	public void setWeekEnd(Integer weekEnd) {
		this.weekEnd = weekEnd;
	}

	
}
