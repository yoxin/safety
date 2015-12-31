package com.itheima.mobilesafe.domain;

/**
 * BlackNumber的bean
 * @author YOXIN
 *
 */
public class BlackNumberInfo {
	private String number;
	private String mode; //1:电话拦截，2：手机拦截，3全部拦截
	public BlackNumberInfo(String number, String mode) {
		super();
		this.number = number;
		this.mode = mode;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	@Override
	public String toString() {
		return "BlackNumberInfo [number=" + number + ", mode=" + mode + "]";
	}
}
