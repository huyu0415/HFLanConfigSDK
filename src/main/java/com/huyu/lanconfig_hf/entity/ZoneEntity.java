package com.huyu.lanconfig_hf.entity;

public class ZoneEntity {

	private String code;
	private String attr;
	
	public ZoneEntity() {
		super();
	}

	public ZoneEntity(String code, String attr) {
		super();
		this.code = code;
		this.attr = attr;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	@Override
	public String toString() {
		return "ZoneEntity [code=" + code + ", attr=" + attr + "]";
	}
	
	
}
