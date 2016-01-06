package com.itheima.mobilesafe.domain;

import android.graphics.drawable.Drawable;


public class AppInfo {
	private Drawable icon;
	private String name;
	private String packageName;
	private boolean inRom;
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public boolean isInRom() {
		return inRom;
	}
	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}
	@Override
	public String toString() {
		return "appInfo [name=" + name + ", packageName=" + packageName
				+ ", inRom=" + inRom + "]";
	}
}
