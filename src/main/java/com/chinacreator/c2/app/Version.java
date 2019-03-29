package com.chinacreator.c2.app;

import java.io.Serializable;

import com.chinacreator.c2.annotation.Column;
import com.chinacreator.c2.annotation.ColumnType;
import com.chinacreator.c2.annotation.Entity;

/**
 * 版本信息
 * @author 
 * @generated
 */
@Entity(id = "entity:com.chinacreator.c2.app.Version", table = "c2_app_version", ds = "dev-center")
public class Version implements Serializable {
	private static final long serialVersionUID = 1853381478039552L;
	/**
	 *版本
	 */
	@Column(id = "version", type = ColumnType.uuid, datatype = "string256")
	private java.lang.String version;

	/**
	 *ios打包版本
	 */
	@Column(id = "ios_build", datatype = "mediumint")
	private java.lang.Integer iosBuild;

	/**
	 *ios脚本版本
	 */
	@Column(id = "ios_script_version", datatype = "mediumint")
	private java.lang.Integer iosScriptVersion;

	/**
	 *ios升级地址
	 */
	@Column(id = "ios_url", datatype = "string256")
	private java.lang.String iosUrl;

	/**
	 *安卓打包版本
	 */
	@Column(id = "android_build", datatype = "mediumint")
	private java.lang.Integer androidBuild;

	/**
	 *安卓脚本版本
	 */
	@Column(id = "android_script_version", datatype = "mediumint")
	private java.lang.Integer androidScriptVersion;

	/**
	 *安卓升级地址
	 */
	@Column(id = "android_url", datatype = "string256")
	private java.lang.String androidUrl;

	/**
	 * 设置版本
	 */
	public void setVersion(java.lang.String version) {
		this.version = version;
	}

	/**
	 * 获取版本
	 */
	public java.lang.String getVersion() {
		return version;
	}

	/**
	 * 设置ios打包版本
	 */
	public void setIosBuild(java.lang.Integer iosBuild) {
		this.iosBuild = iosBuild;
	}

	/**
	 * 获取ios打包版本
	 */
	public java.lang.Integer getIosBuild() {
		return iosBuild;
	}

	/**
	 * 设置ios脚本版本
	 */
	public void setIosScriptVersion(java.lang.Integer iosScriptVersion) {
		this.iosScriptVersion = iosScriptVersion;
	}

	/**
	 * 获取ios脚本版本
	 */
	public java.lang.Integer getIosScriptVersion() {
		return iosScriptVersion;
	}

	/**
	 * 设置ios升级地址
	 */
	public void setIosUrl(java.lang.String iosUrl) {
		this.iosUrl = iosUrl;
	}

	/**
	 * 获取ios升级地址
	 */
	public java.lang.String getIosUrl() {
		return iosUrl;
	}

	/**
	 * 设置安卓打包版本
	 */
	public void setAndroidBuild(java.lang.Integer androidBuild) {
		this.androidBuild = androidBuild;
	}

	/**
	 * 获取安卓打包版本
	 */
	public java.lang.Integer getAndroidBuild() {
		return androidBuild;
	}

	/**
	 * 设置安卓脚本版本
	 */
	public void setAndroidScriptVersion(java.lang.Integer androidScriptVersion) {
		this.androidScriptVersion = androidScriptVersion;
	}

	/**
	 * 获取安卓脚本版本
	 */
	public java.lang.Integer getAndroidScriptVersion() {
		return androidScriptVersion;
	}

	/**
	 * 设置安卓升级地址
	 */
	public void setAndroidUrl(java.lang.String androidUrl) {
		this.androidUrl = androidUrl;
	}

	/**
	 * 获取安卓升级地址
	 */
	public java.lang.String getAndroidUrl() {
		return androidUrl;
	}
}
