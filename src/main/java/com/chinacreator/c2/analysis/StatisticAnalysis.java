package com.chinacreator.c2.analysis;

import java.io.Serializable;
import java.util.Collection;

import com.chinacreator.c2.annotation.Children;
import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 统计分析
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.analysis.StatisticAnalysis", table = "C2_STATISTIC_ANALYSIS", ds = "dev-center")
public class StatisticAnalysis implements Serializable {
	private static final long serialVersionUID = 1704424927444992L;
	/**
	 *主键
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "int")
	private java.lang.Integer id;

	/**
	 *名称
	 */
	@Column(id = "name", datatype = "string128")
	private java.lang.String name;

	/**
	 *
	 */
	@Column(id = "create_by", datatype = "string64")
	private java.lang.String createBy;

	/**
	 *统计分析子项
	 */
	@Children(id = "widgets", fk = "id:stat_id")
	private Collection<StatisticAnalysisOption> widgets;

	/**
	 * 设置主键
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取主键
	 */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * 设置名称
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * 获取名称
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * 设置
	 */
	public void setCreateBy(java.lang.String createBy) {
		this.createBy = createBy;
	}

	/**
	 * 获取
	 */
	public java.lang.String getCreateBy() {
		return createBy;
	}

	/**
	 * 设置统计分析子项
	 */
	public void setWidgets(Collection<StatisticAnalysisOption> widgets) {
		this.widgets = widgets;
	}

	/**
	 * 获取统计分析子项
	 */
	public Collection<StatisticAnalysisOption> getWidgets() {
		return widgets;
	}
}
