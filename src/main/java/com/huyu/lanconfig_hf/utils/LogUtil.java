package com.huyu.lanconfig_hf.utils;

import android.util.Log;

/**
 * ___                      _
 * / __|   ___   ___   ___   | |  ___
 * | (_--- / _ \ / _ \ / _ \  | | /___)
 * \____| \___/ \___/ \___/| |_| \___
 * /
 * \___/
 * Description:
 * Date: 2018/1/23
 * Author: Huyu
 * Success is getting what you want; happiness is wanting what you get.
 */

public class LogUtil {

	private static boolean isDebug = false;

	public static void i (String tag, String msg){
		if(isDebug)
			Log.i("huyu_" + tag, msg);
	}

	public static void d (String tag, String msg){
		if(isDebug)
			Log.d("huyu_" + tag, msg);
	}

	public static void w (String tag, String msg){
		if(isDebug)
			Log.w("huyu_" + tag, msg);
	}

	public static void e (String tag, String msg){
		if(isDebug)
			Log.e("huyu_" + tag, msg);
	}
}
