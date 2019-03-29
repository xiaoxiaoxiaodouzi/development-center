package com.chinacreator.c2.report.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class ReportColumnNames {
	
	//工作日志
	public static final Set<String> WORK_LOG_REPORT = Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList(
			new String[]{"合同编号","任务简称","登记日期","工作日期","工作时长","员工工号","员工姓名","所在部门","过程范畴",
					"项目过程","过程活动","工作地点","工作类型","登记人","描述","项目WBS","统计时长","执行状态","合同ID"}))) ;
	
	//财务报表
	public static Set<String> FINANCIAL_REPORT = Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList(
			new String[]{"LOG_ID","ORG_ID","ORG_NAME","PROJECT_CODE","CONTRACT_NAME","USER_NAME","USER_REALNAME","WORK_TL"}))) ;
	
}
