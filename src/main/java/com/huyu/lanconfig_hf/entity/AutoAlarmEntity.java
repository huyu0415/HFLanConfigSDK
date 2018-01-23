package com.huyu.lanconfig_hf.entity;

public class AutoAlarmEntity {

	private boolean isSet;
	private boolean isOn;
	private String hour;
	private String minute;
	private String armOrDisarm;
	private String weekRepeat;
	
	public AutoAlarmEntity(boolean isSet, boolean isOn, String hour, String minute, String armOrDisarm,
			String weekRepeat) {
		super();
		this.isSet = isSet;
		this.isOn = isOn;
		this.hour = hour;
		this.minute = minute;
		this.armOrDisarm = armOrDisarm;
		this.weekRepeat = weekRepeat;
	}

	public AutoAlarmEntity() {
		super();
	}

	public boolean getIsSet() {
		return isSet;
	}

	public void setIsSet(boolean isSet) {
		this.isSet = isSet;
	}

	public boolean getIsOn() {
		return isOn;
	}

	public void setIsOn(boolean isOn) {
		this.isOn = isOn;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public String getArmOrDisarm() {
		return armOrDisarm;
	}

	public void setArmOrDisarm(String armOrDisarm) {
		this.armOrDisarm = armOrDisarm;
	}

	public String getWeekRepeat() {
		return weekRepeat;
	}

	public void setWeekRepeat(String weekRepeat) {
		this.weekRepeat = weekRepeat;
	}

	@Override
	public String toString() {
		return "AutoAlarmEntity [isSet=" + isSet + ", isOn=" + isOn + ", hour=" + hour + ", minute=" + minute
				+ ", armOrDisarm=" + armOrDisarm + ", weekRepeat=" + weekRepeat + "]";
	}
	
}
