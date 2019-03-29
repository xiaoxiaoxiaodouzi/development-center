package com.chinacreator.c2.report.bean;

import java.util.ArrayList;
import java.util.List;

public class EchartQueryParams {
	
	private EchartDateRange begin ;
	
	private EchartDateRange end ;
	
	private List<EchartDateRange> ranges ;
	
	private Integer projectId ;

	public List<EchartDateRange> getRanges() {
		ranges = new ArrayList<EchartDateRange>() ;
		if(begin!=null)ranges.add(begin) ;
		if(end!=null)ranges.add(end) ;
		if(begin !=null && end !=null
				&& begin.getYear()!=end.getYear()){
			int minus = end.getYear() - begin.getYear() ;
			if(minus<=0) throw new RuntimeException("ECharts入参时间范围起始年份不能小于截止年份") ;
			while(minus>0){
				ranges.add(new EchartDateRange(begin.getYear()+1)) ;
				minus--;
			}
		}
		return ranges;
	}

	public void setRanges(List<EchartDateRange> ranges) {
		this.ranges = ranges;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public EchartDateRange getBegin() {
		return begin;
	}

	public void setBegin(EchartDateRange begin) {
		this.begin = begin;
	}

	public EchartDateRange getEnd() {
		return end;
	}

	public void setEnd(EchartDateRange end) {
		this.end = end;
	}
	
}


