package com.huyu.lanconfig_hf.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

import java.lang.reflect.Method;

/**
 *
 *
 */
public class ScreenListener{
	private static String TAG = "ScreenObserver";
	private Context mContext;
	private ScreenBroadcastReceiver mScreenReceiver;
	private ScreenStateListener mScreenStateListener;
	private static Method mReflectScreenState;

	public ScreenListener(Context context){
		mContext = context;
		mScreenReceiver = new ScreenBroadcastReceiver();
		try {
			mReflectScreenState = PowerManager.class.getMethod("isScreenOn",
					new Class[] {});
		} catch (NoSuchMethodException nsme) {
		}
	}

	/**
	 * screen鐘舵�骞挎挱鎺ユ敹鑰�?    * @author zhangyg
	 *
	 */
	private class ScreenBroadcastReceiver extends BroadcastReceiver{
		private String action = null;
		@Override
		public void onReceive(Context context, Intent intent) {
			action = intent.getAction();
			if(Intent.ACTION_SCREEN_ON.equals(action)){
				mScreenStateListener.onScreenOn();
			}else if(Intent.ACTION_SCREEN_OFF.equals(action)){
				mScreenStateListener.onScreenOff();
			}
		}
	}


	/**
	 * 璇锋眰screen鐘舵�鏇存柊
	 * @param listener
	 */
	public void requestScreenStateUpdate(ScreenStateListener listener) {
		mScreenStateListener = listener;
		startScreenBroadcastReceiver();

		firstGetScreenState();
	}

	/**
	 * 绗竴娆¤姹俿creen鐘舵�?
	 */
	private void firstGetScreenState(){
		PowerManager manager = (PowerManager) mContext
				.getSystemService(Activity.POWER_SERVICE);
		if (isScreenOn(manager)) {
			if (mScreenStateListener != null) {
				mScreenStateListener.onScreenOn();
			}
		} else {
			if (mScreenStateListener != null) {
				mScreenStateListener.onScreenOff();
			}
		}
	}

	/**
	 * 鍋滄screen鐘舵�鏇存柊
	 */
	public void stopScreenStateUpdate(){
		mContext.unregisterReceiver(mScreenReceiver);
	}

	/**
	 * 鍚姩screen鐘舵�骞挎挱鎺ユ敹鍣�? */
	private void startScreenBroadcastReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		mContext.registerReceiver(mScreenReceiver, filter);
	}

	/**
	 * screen鏄惁鎵撳紑鐘舵�?
	 * @param pm
	 * @return
	 */
	private static boolean isScreenOn(PowerManager pm) {
		boolean screenState;
		try {
			screenState = (Boolean) mReflectScreenState.invoke(pm);
		} catch (Exception e) {
			screenState = false;
		}
		return screenState;
	}

	public interface ScreenStateListener {
		public void onScreenOn();
		public void onScreenOff();
	}
}