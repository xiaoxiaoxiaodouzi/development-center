package com.chinacreator.c2.project.weeklyReport;

import java.util.Calendar;

public class test {
public static void main(String[] args) {
	Calendar cal = Calendar.getInstance();
	cal.setWeekDate(2017, 0, Calendar.SUNDAY) ;
	Calendar curr = Calendar.getInstance();
	System.out.println((curr.getTimeInMillis()-cal.getTimeInMillis())/(1000*60*60*24)) ;
	System.out.println(cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.DATE));
}
}
