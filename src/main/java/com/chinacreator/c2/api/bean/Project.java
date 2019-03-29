package com.chinacreator.c2.api.bean;

import java.io.Serializable;
import java.util.Collection;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;
import com.chinacreator.c2.project.service.ProjectSimpleStatistics;

@Entity(id = "entity:com.chinacreator.c2.project.info.Project", table = "zt_project", ds = "dev-center")
public class Project implements Serializable {
	
	private static final long serialVersionUID = 785721773872388657L;

	/**
	 *id
	 */
	@Column(id = "id", type = ColumnType.increment, datatype = "int")
	private java.lang.Integer id;

	/**
	 *项目名称
	 */
	@Column(id = "name_", datatype = "string128")
	private java.lang.String name;

	/**
	 *项目编号
	 */
	@Column(id = "code", datatype = "string64")
	private java.lang.String code;

	/**
	 *项目状态
	 */
	@Column(id = "status_", datatype = "string10")
	private java.lang.String status;

	/**
	 *项目描述
	 */
	@Column(id = "description", datatype = "string2000")
	private java.lang.String description;

	/**
	 *创建时间
	 */
	@Column(id = "create_date", datatype = "timestamp")
	private java.sql.Timestamp createDate;

	/**
	 *激活、归档（不显示）
	 */
	@Column(id = "arc_status", datatype = "char20")
	private java.lang.String arcStatus;

	/**
	 *摘要
	 */
	@Column(id = "brief", datatype = "string1024")
	private java.lang.String brief;

	/**
	 *图片·
	 */
	@Column(id = "pic", datatype = "string128")
	private java.lang.String pic;

	/**
	 *图标
	 */
	@Column(id = "icon", datatype = "string128")
	private java.lang.String icon;

	/**
	 *门户链接
	 */
	@Column(id = "link", datatype = "string512")
	private java.lang.String link;

	/**
	 *云管控链接
	 */
	@Column(id = "cloud_link", datatype = "string512")
	private java.lang.String cloudLink;

	/**
	 *门户发布状态
	 */
	@Column(id = "publish", datatype = "char20")
	private java.lang.String publish;

	/**
	 *云发布状态
	 */
	@Column(id = "cloud_publish", datatype = "char20")
	private java.lang.String cloudPublish;

	/**
	 *项目负责人
	 */
	@Column(id = "owner", datatype = "string64")
	private java.lang.String owner;
	
	@Column(id = "owner", datatype = "boolean")
	private boolean isStar ;
	
	private ProjectSimpleStatistics statistic ;
	
	public ProjectSimpleStatistics getStatistic() {
		return statistic;
	}

	public void setStatistic(ProjectSimpleStatistics statistic) {
		this.statistic = statistic;
	}

	public boolean isStar() {
		return isStar;
	}

	public void setStar(boolean isStar) {
		this.isStar = isStar;
	}

	/**
	 * 设置id
	 */
	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	/**
	 * 获取id
	 */
	public java.lang.Integer getId() {
		return id;
	}

	/**
	 * 设置项目名称
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * 获取项目名称
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * 设置项目编号
	 */
	public void setCode(java.lang.String code) {
		this.code = code;
	}

	/**
	 * 获取项目编号
	 */
	public java.lang.String getCode() {
		return code;
	}

	/**
	 * 设置项目状态
	 */
	public void setStatus(java.lang.String status) {
		this.status = status;
	}

	/**
	 * 获取项目状态
	 */
	public java.lang.String getStatus() {
		return status;
	}

	/**
	 * 设置项目描述
	 */
	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	/**
	 * 获取项目描述
	 */
	public java.lang.String getDescription() {
		return description;
	}

	/**
	 * 设置创建时间
	 */
	public void setCreateDate(java.sql.Timestamp createDate) {
		this.createDate = createDate;
	}

	/**
	 * 获取创建时间
	 */
	public java.sql.Timestamp getCreateDate() {
		return createDate;
	}

	/**
	 * 设置激活、归档（不显示）
	 */
	public void setArcStatus(java.lang.String arcStatus) {
		this.arcStatus = arcStatus;
	}

	/**
	 * 获取激活、归档（不显示）
	 */
	public java.lang.String getArcStatus() {
		return arcStatus;
	}

	/**
	 * 设置摘要
	 */
	public void setBrief(java.lang.String brief) {
		this.brief = brief;
	}

	/**
	 * 获取摘要
	 */
	public java.lang.String getBrief() {
		return brief;
	}

	/**
	 * 设置图片·
	 */
	public void setPic(java.lang.String pic) {
		this.pic = pic;
	}

	/**
	 * 获取图片·
	 */
	public java.lang.String getPic() {
		return pic;
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

	/**
	 * 设置门户链接
	 */
	public void setLink(java.lang.String link) {
		this.link = link;
	}

	/**
	 * 获取门户链接
	 */
	public java.lang.String getLink() {
		return link;
	}

	/**
	 * 设置云管控链接
	 */
	public void setCloudLink(java.lang.String cloudLink) {
		this.cloudLink = cloudLink;
	}

	/**
	 * 获取云管控链接
	 */
	public java.lang.String getCloudLink() {
		return cloudLink;
	}

	/**
	 * 设置门户发布状态
	 */
	public void setPublish(java.lang.String publish) {
		this.publish = publish;
	}

	/**
	 * 获取门户发布状态
	 */
	public java.lang.String getPublish() {
		return publish;
	}

	/**
	 * 设置云发布状态
	 */
	public void setCloudPublish(java.lang.String cloudPublish) {
		this.cloudPublish = cloudPublish;
	}

	/**
	 * 获取云发布状态
	 */
	public java.lang.String getCloudPublish() {
		return cloudPublish;
	}

	/**
	 * 设置项目负责人
	 */
	public void setOwner(java.lang.String owner) {
		this.owner = owner;
	}

	/**
	 * 获取项目负责人
	 */
	public java.lang.String getOwner() {
		return owner;
	}
}