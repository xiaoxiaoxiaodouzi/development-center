package com.chinacreator.c2.analysis;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 统计分析图表
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.analysis.StatisticsAnalysisWidget", table = "C2_STATISTIC_ANALYSIS_WIDGET", ds = "dev-center")
public class StatisticsAnalysisWidget implements Serializable {
	private static final long serialVersionUID = 1716036226678784L;
	/**
	 *主键
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "mediumint")
	private java.lang.Integer id;

	/**
	 *目录
	 */
	@Column(id = "category", datatype = "tinyint")
	private java.lang.Integer category;

	/**
	 *图表类型
	 */
	@Column(id = "type", datatype = "string64")
	private java.lang.String type;

	/**
	 *显示名称
	 */
	@Column(id = "display_name", datatype = "string128")
	private java.lang.String displayName;

	/**
	 *描述
	 */
	@Column(id = "description", datatype = "string512")
	private java.lang.String description;

	/**
	 *是否启用
	 */
	@Column(id = "enabled", datatype = "boolean")
	private java.lang.Boolean enabled;

	/**
	 *图标
	 */
	@Column(id = "icon", datatype = "string128")
	private java.lang.String icon;

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
	 * 设置目录
	 */
	public void setCategory(java.lang.Integer category) {
		this.category = category;
	}

	/**
	 * 获取目录
	 */
	public java.lang.Integer getCategory() {
		return category;
	}

	/**
	 * 设置图表类型
	 */
	public void setType(java.lang.String type) {
		this.type = type;
	}

	/**
	 * 获取图表类型
	 */
	public java.lang.String getType() {
		return type;
	}

	/**
	 * 设置显示名称
	 */
	public void setDisplayName(java.lang.String displayName) {
		this.displayName = displayName;
	}

	/**
	 * 获取显示名称
	 */
	public java.lang.String getDisplayName() {
		return displayName;
	}

	/**
	 * 设置描述
	 */
	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	/**
	 * 获取描述
	 */
	public java.lang.String getDescription() {
		return description;
	}

	/**
	 * 设置是否启用
	 */
	public void setEnabled(java.lang.Boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * 获取是否启用
	 */
	public java.lang.Boolean isEnabled() {
		return enabled;
	}

	/**
	 * 设置图标
	 */
	public void setIcon(java.lang.String icon) {
		this.icon = icon;
	}

	/**
	 * 获取图标
	 */
	public java.lang.String getIcon() {
		return icon;
	}
}
