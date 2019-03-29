package com.chinacreator.c2.report.workLog;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 工作日志
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.report.workLog.WorkLog", table = "c2_community_worklog", ds = "dev-center")
public class WorkLog implements Serializable {
	private static final long serialVersionUID = 1340629287698432L;
	/**
	 *日志id
	 */
	@Column(id = "ID", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer ID;

	/**
	 *合同编号
	 */
	@Column(id = "HTBH", datatype = "string64")
	private java.lang.String HTBH;

	/**
	 *任务简称
	 */
	@Column(id = "RWJC", datatype = "string256")
	private java.lang.String RWJC;

	/**
	 *登记日期
	 */
	@Column(id = "DJRQ", datatype = "date")
	private java.sql.Date DJRQ;

	/**
	 *工作日期
	 */
	@Column(id = "GZRQ", datatype = "date")
	private java.sql.Date GZRQ;

	/**
	 *工作时长
	 */
	@Column(id = "GZSC", datatype = "string20")
	private java.lang.String GZSC;

	/**
	 *员工编号
	 */
	@Column(id = "YGBH", datatype = "string32")
	private java.lang.String YGBH;

	/**
	 *员工姓名
	 */
	@Column(id = "YGXM", datatype = "string64")
	private java.lang.String YGXM;

	/**
	 *所在部门
	 */
	@Column(id = "SZBM", datatype = "string64")
	private java.lang.String SZBM;

	/**
	 *过程范畴
	 */
	@Column(id = "GCFC", datatype = "string64")
	private java.lang.String GCFC;

	/**
	 *项目过程
	 */
	@Column(id = "XMGC", datatype = "string64")
	private java.lang.String XMGC;

	/**
	 *过程活动
	 */
	@Column(id = "GCHD", datatype = "string64")
	private java.lang.String GCHD;

	/**
	 *工作地点
	 */
	@Column(id = "GZDD", datatype = "string64")
	private java.lang.String GZDD;

	/**
	 *工作类型
	 */
	@Column(id = "GZLX", datatype = "string64")
	private java.lang.String GZLX;

	/**
	 *登记人
	 */
	@Column(id = "DJR", datatype = "string64")
	private java.lang.String DJR;

	/**
	 *描述
	 */
	@Column(id = "MS", datatype = "string2000")
	private java.lang.String MS;

	/**
	 *项目WBS
	 */
	@Column(id = "XMWBS", datatype = "string256")
	private java.lang.String XMWBS;

	/**
	 *提交时长
	 */
	@Column(id = "TJSC", datatype = "string20")
	private java.lang.String TJSC;

	/**
	 *执行状态
	 */
	@Column(id = "ZXZT", datatype = "string32")
	private java.lang.String ZXZT;

	/**
	 *合同ID
	 */
	@Column(id = "HTID", datatype = "string64")
	private java.lang.String HTID;

	/**
	 *状态
	 */
	@Column(id = "STRUTS", datatype = "string32")
	private java.lang.String STRUTS;

	/**
	 * 设置日志id
	 */
	public void setID(java.lang.Integer ID) {
		this.ID = ID;
	}

	/**
	 * 获取日志id
	 */
	public java.lang.Integer getID() {
		return ID;
	}

	/**
	 * 设置合同编号
	 */
	public void setHTBH(java.lang.String HTBH) {
		this.HTBH = HTBH;
	}

	/**
	 * 获取合同编号
	 */
	public java.lang.String getHTBH() {
		return HTBH;
	}

	/**
	 * 设置任务简称
	 */
	public void setRWJC(java.lang.String RWJC) {
		this.RWJC = RWJC;
	}

	/**
	 * 获取任务简称
	 */
	public java.lang.String getRWJC() {
		return RWJC;
	}

	/**
	 * 设置登记日期
	 */
	public void setDJRQ(java.sql.Date DJRQ) {
		this.DJRQ = DJRQ;
	}

	/**
	 * 获取登记日期
	 */
	public java.sql.Date getDJRQ() {
		return DJRQ;
	}

	/**
	 * 设置工作日期
	 */
	public void setGZRQ(java.sql.Date GZRQ) {
		this.GZRQ = GZRQ;
	}

	/**
	 * 获取工作日期
	 */
	public java.sql.Date getGZRQ() {
		return GZRQ;
	}

	/**
	 * 设置工作时长
	 */
	public void setGZSC(java.lang.String GZSC) {
		this.GZSC = GZSC;
	}

	/**
	 * 获取工作时长
	 */
	public java.lang.String getGZSC() {
		return GZSC;
	}

	/**
	 * 设置员工编号
	 */
	public void setYGBH(java.lang.String YGBH) {
		this.YGBH = YGBH;
	}

	/**
	 * 获取员工编号
	 */
	public java.lang.String getYGBH() {
		return YGBH;
	}

	/**
	 * 设置员工姓名
	 */
	public void setYGXM(java.lang.String YGXM) {
		this.YGXM = YGXM;
	}

	/**
	 * 获取员工姓名
	 */
	public java.lang.String getYGXM() {
		return YGXM;
	}

	/**
	 * 设置所在部门
	 */
	public void setSZBM(java.lang.String SZBM) {
		this.SZBM = SZBM;
	}

	/**
	 * 获取所在部门
	 */
	public java.lang.String getSZBM() {
		return SZBM;
	}

	/**
	 * 设置过程范畴
	 */
	public void setGCFC(java.lang.String GCFC) {
		this.GCFC = GCFC;
	}

	/**
	 * 获取过程范畴
	 */
	public java.lang.String getGCFC() {
		return GCFC;
	}

	/**
	 * 设置项目过程
	 */
	public void setXMGC(java.lang.String XMGC) {
		this.XMGC = XMGC;
	}

	/**
	 * 获取项目过程
	 */
	public java.lang.String getXMGC() {
		return XMGC;
	}

	/**
	 * 设置过程活动
	 */
	public void setGCHD(java.lang.String GCHD) {
		this.GCHD = GCHD;
	}

	/**
	 * 获取过程活动
	 */
	public java.lang.String getGCHD() {
		return GCHD;
	}

	/**
	 * 设置工作地点
	 */
	public void setGZDD(java.lang.String GZDD) {
		this.GZDD = GZDD;
	}

	/**
	 * 获取工作地点
	 */
	public java.lang.String getGZDD() {
		return GZDD;
	}

	/**
	 * 设置工作类型
	 */
	public void setGZLX(java.lang.String GZLX) {
		this.GZLX = GZLX;
	}

	/**
	 * 获取工作类型
	 */
	public java.lang.String getGZLX() {
		return GZLX;
	}

	/**
	 * 设置登记人
	 */
	public void setDJR(java.lang.String DJR) {
		this.DJR = DJR;
	}

	/**
	 * 获取登记人
	 */
	public java.lang.String getDJR() {
		return DJR;
	}

	/**
	 * 设置描述
	 */
	public void setMS(java.lang.String MS) {
		this.MS = MS;
	}

	/**
	 * 获取描述
	 */
	public java.lang.String getMS() {
		return MS;
	}

	/**
	 * 设置项目WBS
	 */
	public void setXMWBS(java.lang.String XMWBS) {
		this.XMWBS = XMWBS;
	}

	/**
	 * 获取项目WBS
	 */
	public java.lang.String getXMWBS() {
		return XMWBS;
	}

	/**
	 * 设置提交时长
	 */
	public void setTJSC(java.lang.String TJSC) {
		this.TJSC = TJSC;
	}

	/**
	 * 获取提交时长
	 */
	public java.lang.String getTJSC() {
		return TJSC;
	}

	/**
	 * 设置执行状态
	 */
	public void setZXZT(java.lang.String ZXZT) {
		this.ZXZT = ZXZT;
	}

	/**
	 * 获取执行状态
	 */
	public java.lang.String getZXZT() {
		return ZXZT;
	}

	/**
	 * 设置合同ID
	 */
	public void setHTID(java.lang.String HTID) {
		this.HTID = HTID;
	}

	/**
	 * 获取合同ID
	 */
	public java.lang.String getHTID() {
		return HTID;
	}

	/**
	 * 设置状态
	 */
	public void setSTRUTS(java.lang.String STRUTS) {
		this.STRUTS = STRUTS;
	}

	/**
	 * 获取状态
	 */
	public java.lang.String getSTRUTS() {
		return STRUTS;
	}
}
