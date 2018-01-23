package com.huyu.lanconfig_hf.control;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.hiflying.smartlink.ISmartLinker;
import com.hiflying.smartlink.OnSmartLinkListener;
import com.hiflying.smartlink.v7.MulticastSmartLinker;

/**
 * ___                      _
 * / __|   ___   ___   ___   | |  ___
 * | (_--- / _ \ / _ \ / _ \  | | /___)
 * \____| \___/ \___/ \___/| |_| \___
 * /
 * \___/
 * Description:
 * Date: 2018/1/20
 * Author: Huyu
 * Success is getting what you want; happiness is wanting what you get.
 */

public class DeviceConfiger {


	private ISmartLinker mSmartLinker;
	private boolean mIsConncting = false;
	private OnSmartLinkListener mListener;

	public DeviceConfiger() {
		mSmartLinker = MulticastSmartLinker.getInstance();
	}

	public void setListener(OnSmartLinkListener mListener){
		this.mListener = mListener;
	}

	/**
	 * 开始配置
	 */
	public void startConfigWifi(Context context, String ssid, String pwd){
		if(!mIsConncting && mListener != null){
			//设置要配置的ssid 和pswd
			try {
				mSmartLinker.setOnSmartLinkListener(mListener);
				mSmartLinker.start(context, pwd, ssid);
				mIsConncting = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 停止配置
	 */
	public void stopConfigWifi(){
		mSmartLinker.setOnSmartLinkListener(null);
		mSmartLinker.stop();
		mIsConncting = false;
	}

	/**
	 * 获取 wifi SSID
	 */
	public String getCurrentSSid(Context context){

		WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if(wm != null){
			WifiInfo wi = wm.getConnectionInfo();
			if(wi != null){
				String ssid = wi.getSSID();
				if(ssid.length()>2 && ssid.startsWith("\"") && ssid.endsWith("\"")){
					return ssid.substring(1,ssid.length()-1);
				}else{
					return ssid;
				}
			}
		}
		return "";
	}

}
