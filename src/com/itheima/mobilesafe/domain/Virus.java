package com.itheima.mobilesafe.domain;

public class Virus {
	private String md5;
	private String desc;
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	@Override
	public String toString() {
		return "Virus [md5=" + md5 + ", desc=" + desc + "]";
	}
	
}
