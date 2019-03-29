package com.chinacreator.c2.erp;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * ERP项目
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.erp.ErpProject", table = "V_PROJECT", ds = "erp", cache = false)
public class ErpProject {
	@Column(id = "pid", type = ColumnType.uuid, datatype = "string32")
	private java.lang.String pid;

	@Column(id = "pcode", datatype = "string20")
	private java.lang.String pcode;

	@Column(id = "pname", datatype = "string256")
	private java.lang.String pname;

	/**
	 * 设置${field.desc}
	 */
	public void setPid(java.lang.String pid) {
		this.pid = pid;
	}

	/**
	 * 获取${field.desc}
	 */
	public java.lang.String getPid() {
		return pid;
	}

	/**
	 * 设置${field.desc}
	 */
	public void setPcode(java.lang.String pcode) {
		this.pcode = pcode;
	}

	/**
	 * 获取${field.desc}
	 */
	public java.lang.String getPcode() {
		return pcode;
	}

	/**
	 * 设置${field.desc}
	 */
	public void setPname(java.lang.String pname) {
		this.pname = pname;
	}

	/**
	 * 获取${field.desc}
	 */
	public java.lang.String getPname() {
		return pname;
	}
}
