package com.chinacreator.c2.erp;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * aaa
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.erp.aa", table = "aaa", ds = "dev-center")
public class Aa implements Serializable {
	private static final long serialVersionUID = 1727454733418496L;
	@Column(id = "asd", type = ColumnType.uuid, datatype = "char22")
	private java.lang.String asd;

	/**
	 * 设置${field.desc}
	 */
	public void setAsd(java.lang.String asd) {
		this.asd = asd;
	}

	/**
	 * 获取${field.desc}
	 */
	public java.lang.String getAsd() {
		return asd;
	}
}
