package com.itheima.mobilesafe.domain;

public class ScanInfo {
	private boolean isVirus;
	private String appName;
	private String packageName;

	public boolean isVirus() {
		return isVirus;
	}

	public void setVirus(boolean isVirus) {
		this.isVirus = isVirus;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String toString() {
		return "ScanInfo [isVirus=" + isVirus + ", appName=" + appName
				+ ", packageName=" + packageName + "]";
	}
}
