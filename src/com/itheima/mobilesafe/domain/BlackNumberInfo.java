package com.itheima.mobilesafe.domain;

/**
 * BlackNumber��bean
 * @author YOXIN
 *
 */
public class BlackNumberInfo {
	private String number;
	private String mode; //1:�绰���أ�2���ֻ����أ�3ȫ������
	private Integer id;
	
	public BlackNumberInfo(String number, String mode) {
		super();
		this.number = number;
		this.mode = mode;
	}
	public BlackNumberInfo(String number, String mode, Integer id) {
		super();
		this.number = number;
		this.mode = mode;
		this.id = id;
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
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
}
