package com.itheima.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class TaskInfo {
	private Drawable icon;
	private String name;
	private String packname;
	private boolean isUserTack;
	private long memSize;
	//listview里面的item里面的checkbox是否被选中
	private boolean isChecked;
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
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}
	public boolean isUserTack() {
		return isUserTack;
	}
	public void setUserTack(boolean isUserTack) {
		this.isUserTack = isUserTack;
	}
	public long getMemSize() {
		return memSize;
	}
	public void setMemSize(long memSize) {
		this.memSize = memSize;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	@Override
	public String toString() {
		return "TaskInfo [name=" + name + ", packname=" + packname
				+ ", isUserTack=" + isUserTack + ", memSize=" + memSize + "]";
	}
}
