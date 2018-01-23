package com.huyu.lanconfig_hf.utils;

import android.content.Context;

import com.huyu.hflanconfigsdk.R;


public class BeanUtils {
	/**去除字符串前面的零*/
	public static String quChuLing(String h2b) {
		int index = 0;//预定义第一个非零字符串的位置

		char strs[] = h2b.toCharArray();// 将字符串转化成字符数组
		for(int i=0; i<h2b.length(); i++){
			if('0'!=strs[i]){
				index=i;// 找到非零字符串并跳出
				break;
			}
		}
		String strLast = h2b.substring(index, h2b.length());// 截取字符串
		return strLast;
	}

	public static String quChuLing2(String str) {
		int parseInt = Integer.parseInt(str);
		return String.valueOf(parseInt);
	}

	/**
	 * 返回防区属性
	 * str  000
	 * @return 普通
	 */
	public static String fangQuShuXing(String str,Context con){
		if("000".equals(str)){
			return con.getResources().getString(R.string.ordinary);
		}else if("001".equals(str)){
			return con.getResources().getString(R.string.left_behind);
		}else if("010".equals(str)){
			return con.getResources().getString(R.string.intelligence);
		}else if("011".equals(str)){
			return con.getResources().getString(R.string.emergency);
		}else if("100".equals(str)){
			return con.getResources().getString(R.string.guanbi);
		}else if("101".equals(str)){
			return con.getResources().getString(R.string.doorbell);
		}else if("110".equals(str)){
			return con.getResources().getString(R.string.usher);
		}else if("111".equals(str)){
			return con.getResources().getString(R.string.old_man);
		}
		return null;
	}

	/**返回防区属性对应的数字*/
	public static String fangQuShuXingNum(String str,Context con){
		if(str.equals(con.getResources().getString(R.string.ordinary))){
			return "000";
		}else if(str.equals(con.getResources().getString(R.string.left_behind))){
			return "001";
		}else if(str.equals(con.getResources().getString(R.string.intelligence))){
			return "010";
		}else if(str.equals(con.getResources().getString(R.string.emergency))){
			return "011";
		}else if(str.equals(con.getResources().getString(R.string.guanbi))){
			return "100";
		}else if(str.equals(con.getResources().getString(R.string.doorbell))){
			return "101";
		}else if(str.equals(con.getResources().getString(R.string.usher))){
			return "110";
		}else if(str.equals(con.getResources().getString(R.string.old_man))){
			return "111";
		}
		return null;
	}
	/**返回防区事件码对应的数字*/
	public static String fangQuCodeNum(String str,Context con){
		if(str.equals(con.getResources().getString(R.string.medical_care))){
			return "100";
		}else if(str.equals(con.getResources().getString(R.string.fire_alarm))){
			return "110";
		}else if(str.equals(con.getResources().getString(R.string.robbery_alarm))){
			return "121";
		}else if(str.equals(con.getResources().getString(R.string.silent))){
			return "122";
		}else if(str.equals(con.getResources().getString(R.string.thief_alarm))){
			return "130";
		}else if(str.equals(con.getResources().getString(R.string.perimeter))){
			return "131";
		}else if(str.equals(con.getResources().getString(R.string.gas))){
			return "151";
		}
		return null;
	}
	/**返回数字对应的防区事件码*/
	public static String numToFangQuCode(String str,Context con){
		if(str.equals("100")){
			return con.getResources().getString(R.string.medical_care);
		}else if(str.equals("110")){
			return con.getResources().getString(R.string.fire_alarm);
		}else if(str.equals("121")){
			return con.getResources().getString(R.string.robbery_alarm);
		}else if(str.equals("122")){
			return con.getResources().getString(R.string.silent);
		}else if(str.equals("130")){
			return con.getResources().getString(R.string.thief_alarm);
		}else if(str.equals("131")){
			return con.getResources().getString(R.string.perimeter);
		}else if(str.equals("151")){
			return con.getResources().getString(R.string.gas);
		}
		return null;
	}
	/**返回报警音频对应的数字*/
	public static String alarmToneNum(String str,Context con){
		if(str.equals(con.getResources().getString(R.string.siren))){
			return "00";
		}else if(str.equals(con.getResources().getString(R.string.bark))){
			return "01";
		}else if(str.equals(con.getResources().getString(R.string.satan_told1))){
			return "10";
		}else if(str.equals(con.getResources().getString(R.string.satan_told2))){
			return "11";
		}
		return null;
	}
	/**返回语言对应的数字*/
	public static String languageNum(String str,Context con){
		if(str.equals(con.getResources().getString(R.string.language_chinese))){
			return "00";
		}else if(str.equals(con.getResources().getString(R.string.language_english))){
			return "01";
		}else if(str.equals(con.getResources().getString(R.string.language_russian))){
			return "02";
		}else if(str.equals(con.getResources().getString(R.string.language_spain))){
			return "03";
		}else if(str.equals(con.getResources().getString(R.string.language_french))){
			return "04";
		}
		return null;
	}
	/**返回数字对应的语言*/
	public static String numToLanguage(int str,Context con){
		if(str==0){
			return con.getResources().getString(R.string.language_chinese);
		}else if(str==1){
			return con.getResources().getString(R.string.language_english);
		}else if(str==2){
			return con.getResources().getString(R.string.language_russian);
		}else if(str==3){
			return con.getResources().getString(R.string.language_spain);
		}else if(str==4){
			return con.getResources().getString(R.string.language_french);
		}
		return con.getResources().getString(R.string.language_chinese);
	}


}
