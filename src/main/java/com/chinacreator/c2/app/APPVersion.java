package com.chinacreator.c2.app;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 版本信息
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.app.APPVersion", table = "c2_app_version", ds = "dev-center", cache = false)
public class APPVersion {
	/**
	 *
	 */
	@Column(id = "id", type = ColumnType.uuid, datatype = "char22")
	private java.lang.String id;

	/**
	 *
	 */
	@Column(id = "version", datatype = "string256")
	private java.lang.String version;

	/**
	 *
	 */
	@Column(id = "ios_build", datatype = "mediumint")
	private java.lang.Integer iosBuild;

	/**
	 *
	 */
	@Column(id = "ios_script_version", datatype = "mediumint")
	private java.lang.Integer iosScriptVersion;

	/**
	 *
	 */
	@Column(id = "ios_url", datatype = "string256")
	private java.lang.String iosUrl;

	/**
	 *
	 */
	@Column(id = "android_build", datatype = "mediumint")
	private java.lang.Integer androidBuild;

	/**
	 *
	 */
	@Column(id = "android_script_version", datatype = "mediumint")
	private java.lang.Integer androidScriptVersion;

	/**
	 *
	 */
	@Column(id = "android_url", datatype = "string256")
	private java.lang.String androidUrl;

	/**
	 *
	 */
	@Column(id = "name", datatype = "string256")
	private java.lang.String name;

	/**
	 *
	 */
	@Column(id = "android_min_version", datatype = "string256")
	private java.lang.String androidMinVersion;

	/**
	 *
	 */
	@Column(id = "ios_min_version", datatype = "string256")
	private java.lang.String iosMinVersion;

	/**
	 *
	 */
	@Column(id = "android_version", datatype = "string256")
	private java.lang.String androidVersion;

	/**
	 *
	 */
	@Column(id = "ios_version", datatype = "string256")
	private java.lang.String iosVersion;

	/**
	 * 设置
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	 * 获取
	 */
	public java.lang.String getId() {
		return id;
	}

	/**
	 * 设置
	 */
	public void setVersion(java.lang.String version) {
		this.version = version;
	}

	/**
	 * 获取
	 */
	public java.lang.String getVersion() {
		return version;
	}

	/**
	 * 设置
	 */
	public void setIosBuild(java.lang.Integer iosBuild) {
		this.iosBuild = iosBuild;
	}

	/**
	 * 获取
	 */
	public java.lang.Integer getIosBuild() {
		return iosBuild;
	}

	/**
	 * 设置
	 */
	public void setIosScriptVersion(java.lang.Integer iosScriptVersion) {
		this.iosScriptVersion = iosScriptVersion;
	}

	/**
	 * 获取
	 */
	public java.lang.Integer getIosScriptVersion() {
		return iosScriptVersion;
	}

	/**
	 * 设置
	 */
	public void setIosUrl(java.lang.String iosUrl) {
		this.iosUrl = iosUrl;
	}

	/**
	 * 获取
	 */
	public java.lang.String getIosUrl() {
		return iosUrl;
	}

	/**
	 * 设置
	 */
	public void setAndroidBuild(java.lang.Integer androidBuild) {
		this.androidBuild = androidBuild;
	}

	/**
	 * 获取
	 */
	public java.lang.Integer getAndroidBuild() {
		return androidBuild;
	}

	/**
	 * 设置
	 */
	public void setAndroidScriptVersion(java.lang.Integer androidScriptVersion) {
		this.androidScriptVersion = androidScriptVersion;
	}

	/**
	 * 获取
	 */
	public java.lang.Integer getAndroidScriptVersion() {
		return androidScriptVersion;
	}

	/**
	 * 设置
	 */
	public void setAndroidUrl(java.lang.String androidUrl) {
		this.androidUrl = androidUrl;
	}

	/**
	 * 获取
	 */
	public java.lang.String getAndroidUrl() {
		return androidUrl;
	}

	/**
	 * 设置
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * 获取
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * 设置
	 */
	public void setAndroidMinVersion(java.lang.String androidMinVersion) {
		this.androidMinVersion = androidMinVersion;
	}

	/**
	 * 获取
	 */
	public java.lang.String getAndroidMinVersion() {
		return androidMinVersion;
	}

	/**
	 * 设置
	 */
	public void setIosMinVersion(java.lang.String iosMinVersion) {
		this.iosMinVersion = iosMinVersion;
	}

	/**
	 * 获取
	 */
	public java.lang.String getIosMinVersion() {
		return iosMinVersion;
	}

	/**
	 * 设置
	 */
	public void setAndroidVersion(java.lang.String androidVersion) {
		this.androidVersion = androidVersion;
	}

	/**
	 * 获取
	 */
	public java.lang.String getAndroidVersion() {
		return androidVersion;
	}

	/**
	 * 设置
	 */
	public void setIosVersion(java.lang.String iosVersion) {
		this.iosVersion = iosVersion;
	}

	/**
	 * 获取
	 */
	public java.lang.String getIosVersion() {
		return iosVersion;
	}
}
