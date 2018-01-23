package com.huyu.lanconfig_hf.control;

import android.os.Handler;
import android.os.Message;

import com.huyu.lanconfig_hf.listener.ControlListener;
import com.huyu.lanconfig_hf.model.NetworkProtocol;
import com.huyu.lanconfig_hf.model.TransparentTransmission;
import com.huyu.lanconfig_hf.model.TransparentTransmissionListener;
import com.huyu.lanconfig_hf.utils.ByteUtils;
import com.huyu.lanconfig_hf.utils.CMDUtils;
import com.huyu.lanconfig_hf.utils.CMDUtils.CmdName;
import com.huyu.lanconfig_hf.utils.LogUtil;
import com.huyu.lanconfig_hf.utils.Utils;

import java.util.concurrent.ConcurrentHashMap;

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

public class DeviceController {

	private final String TAG = "DeviceController";
	private boolean tts_ok;
	private TransparentTransmission mTTransmission;

	private ControlListener mControlListener;

	private final int GET_STATUS = 0x00;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case GET_STATUS:
					sendCMD(CMDUtils.CmdName.GETSTATU, 0, 0, null);
					break;
				default:
					break;
			}
		}
	};

	public void init(String ip) {
		NetworkProtocol protocol = new NetworkProtocol();
		protocol.setProtocol("TCP");
		protocol.setServer("Server");
		protocol.setIp(ip);
		protocol.setPort(8899);
		mTTransmission = new TransparentTransmission();
		mTTransmission.setProtocol(protocol);

		mTTransmission.setListener(new TransparentTransmissionListener() {

			@Override
			public void onOpen(boolean success) {
				LogUtil.i(TAG, "进入透传模式:" + success);
				if(null != mControlListener)
					mControlListener.openTTSResponse(success);
			}

			@Override
			public void onReceive(byte[] data, int length) {
				boolean correctRespon = CMDUtils.vervifyData(data);
				if (data != null && data.length > 168 && correctRespon) {
					ConcurrentHashMap<CMDUtils.CmdName, Object> map = new ConcurrentHashMap<>();
					map.putAll(CMDUtils.parseData(data));
					LogUtil.i(TAG, "map=" + map);
					if (map.size() > 0 && mControlListener != null) {
						// 解析
						mControlListener.didGetStatus(map);
					}
				}
			}
		});


		enterTTSMode();
	}

	public void setControlListener(ControlListener mControlListener) {
		this.mControlListener = mControlListener;
	}

	public void exitControl() {
		mTTransmission.close();
	}


	/**
	 * @param groupNum   1-2
	 * @param weekStr    1111111
	 * @param armTime    15:00
	 * @param disarmTime 16:00
	 * @param isEnable   true
	 */
	public void setAutoTiming(int groupNum, String weekStr, String armTime, String disarmTime, boolean isEnable) {
		CmdName cmd;
		if (groupNum < 1 || groupNum > 2)
			return;
		if (groupNum == 1) {
			cmd = CmdName.AUTOARM1;
		} else {
			cmd = CmdName.AUTOARM3;
		}

		if (weekStr.length() != 7)
			return;

		String isOn = isEnable ? "01" : "00";
		String week = ByteUtils.b2h("0" + weekStr);
		String new_time1[] = armTime.split(":");
		String new_time2[] = disarmTime.split(":");
		String bcf1 = "00";
		String bcf2 = "01";
		String val1 = "01" + isOn + new_time1[0].trim() + new_time1[1].trim() + week + bcf1;
		String val2 = "01" + isOn + new_time2[0].trim() + new_time2[1].trim() + week + bcf2;
		String val = val1 + "*" + val2;
		sendCMD(cmd, 0, 0, val);
	}

	/**
	 * 0-10
	 */
	public void setAlarmVol(int volume) {
		if (volume > 10)
			volume = 10;
		if (volume < 0)
			volume = 0;
		sendCMD(CmdName.ALARMVOL, 0, 0, ByteUtils.int2HaxString(volume).toUpperCase());
	}

	/**
	 * 0-10
	 */
	public void setVoiceVol(int volume) {
		if (volume > 10)
			volume = 10;
		if (volume < 0)
			volume = 0;
		sendCMD(CmdName.VOICEVOL, 0, 0, ByteUtils.int2HaxString(volume).toUpperCase());
	}

	/**
	 * 0-arm
	 * 1-disaem
	 * 2-stay
	 */
	public void setArmDisarmStatus(int status) {
		String sta = status == 0 ? "00" : status == 1 ? "01" : "10";
		sendCMD(CmdName.ARM_DISARM, 30, 32, sta);
	}

	/**
	 *
	 * @param groupNum	1-3
	 * @param num		less then 32 bit
	 */
	public void setAlarmNum(int groupNum, String num) {
		CmdName cmdName;
		if (groupNum == 1)
			cmdName = CmdName.ALARMNUM1;
		else if (groupNum == 2)
			cmdName = CmdName.ALARMNUM2;
		else if (groupNum == 3)
			cmdName = CmdName.ALARMNUM3;
		else
			return;

		String regex = "[0-9*]+";

		if (null == num || num.length() == 0 || num.equals("*") || num.length() > 32 || !num.matches(regex)) {
			return;
		}

		// 将 * 转成 A，解析数据时再还原
		if (num.contains("*")) {
			num = num.replace('*', 'A');
		}
		String len = String.valueOf(num.length());
		if (num.length() < 10) {
			len = "0" + len;
		}
		StringBuilder numBuilder = new StringBuilder(num);
		for (int i = numBuilder.length(); i < 32; i++) {
			numBuilder.append("0");
		}
		num = numBuilder.toString();
		String val = len + num;
		sendCMD(cmdName, 0, 0, val);
	}

	/**
	 * @param tone "0"	siren
	 *             "1"	dog
	 *             "2"	devil 1
	 *             "3"	devil 2
	 */
	public void setAlarmTone(String tone) {
		String tone_binary = Integer.toBinaryString(Integer.parseInt(tone));
		if(tone_binary.length() == 1)
			tone_binary = "0" + tone_binary;
		sendCMD(CmdName.ALARMTONE, 28, 30, tone_binary);
	}

	/**
	 * @param times 0-20
	 */
	public void setRingTimes(int times) {
		if(times < 0)
			times = 0;
		if(times > 20)
			times = 20;
		String val = ByteUtils.int2HaxString(times).toUpperCase();
		sendCMD(CmdName.RINGTIMES, 0, 0, val);
	}

	/**
	 * @param language "00" -- chinese
	 *                 "01" -- english
	 *                 "02" -- russian
	 *                 "03" --	spanish
	 *                 "04" -- france
	 */
	private void setDeviceLanguage(String language) {
		if(language.matches("[0-4]{2}"))
			sendCMD(CmdName.LANGUAGE, 0, 0, language);
	}

	public void setDeviceTime(String year, String month, String day, String hour, String minute, String second) {
		if (year.length() == 1) {
			year = "0" + year;
		}else if(year.length() > 2)
			year = year.substring(year.length() - 2);
		if (month.length() == 1) {
			month = "0" + month;
		}
		if (day.length() == 1) {
			day = "0" + day;
		}
		if (hour.length() == 1) {
			hour = "0" + hour;
		}
		if (minute.length() == 1) {
			minute = "0" + minute;
		}
		if (second.length() == 1) {
			second = "0" + second;
		}
		sendCMD(CmdName.RTC, 0, 0, year + month + day + hour + minute + second);
	}

	public void setDevicePwd(String pwd){
		if(pwd.matches("[\\d]{6}"))
			sendCMD(CmdName.PASSWORD, 0, 0, pwd);
	}

	public void setDeviceServerParams(String ip, int port, String cid) {
		if (ip == null || ip.length() == 0 || (!Utils.isIP(ip) && !Utils.isDomain(ip))) {
			return;
		}
		if (port < 0 || port > 65535) {
			return;
		}
		if (cid == null || cid.length() != 6) {
			return;
		}
		String server = ip + "+" + port + "*" + cid;
		sendCMD(CmdName.IP, 0, 0, server);
	}

	/**
	 *
	 * @param zoneNum	1-8
	 * @param attrCode	"000"	ordinary
	 *                  "001"	stay
	 *                  "010"	intelligence
	 *                  "011"	emergency
	 *                  "100"	shut down
	 *                  "101"	doorbell
	 *                  "110"	welcome
	 *                  "111"	old man
	 */
	public void setZoneAttr(int zoneNum, String attrCode){
		int start;
		CmdName cmdName;
		if(zoneNum < 1 || zoneNum > 8 || null == attrCode || !attrCode.matches("[01]{3}"))
			return;

		if(zoneNum == 1){
			start = 25;
			cmdName = CmdName.ZONE1ATTR;
		}else if(zoneNum == 2){
			start = 22;
			cmdName = CmdName.ZONE2ATTR;
		}else if(zoneNum == 3){
			start = 19;
			cmdName = CmdName.ZONE3ATTR;
		}else if(zoneNum == 4){
			start = 16;
			cmdName = CmdName.ZONE4ATTR;
		}else if(zoneNum == 5){
			start = 13;
			cmdName = CmdName.ZONE5ATTR;
		}else if(zoneNum == 6){
			start = 10;
			cmdName = CmdName.ZONE6ATTR;
		}else if(zoneNum == 7){
			start = 7;
			cmdName = CmdName.ZONE7ATTR;
		}else{
			start = 4;
			cmdName = CmdName.ZONE8ATTR;
		}

		sendCMD(cmdName, start, start + 3, attrCode);
	}

	/**
	 *
	 * @param zoneNum	1-8
	 * @param code	"100"	medical_care
	 *                  "110"	fire_alarm
	 *                  "121"	robbery_alarm
	 *                  "122"	silent
	 *                  "130"	thief_alarm
	 *                  "131"	perimeter
	 *                  "151"	gas
	 */
	public void setZoneCode(int zoneNum, String code){
		int start;
		CmdName cmdName;
		if(zoneNum < 1 || zoneNum > 8 || null == code || !code.matches("[01235]{3}"))
			return;

		if(zoneNum == 1){
			cmdName = CmdName.EFFECTCODE1;
		}else if(zoneNum == 2){
			cmdName = CmdName.EFFECTCODE2;
		}else if(zoneNum == 3){
			cmdName = CmdName.EFFECTCODE3;
		}else if(zoneNum == 4){
			cmdName = CmdName.EFFECTCODE4;
		}else if(zoneNum == 5){
			cmdName = CmdName.EFFECTCODE5;
		}else if(zoneNum == 6){
			cmdName = CmdName.EFFECTCODE6;
		}else if(zoneNum == 7){
			cmdName = CmdName.EFFECTCODE7;
		}else{
			cmdName = CmdName.EFFECTCODE8;
		}

		sendCMD(cmdName, 0, 0, code);
	}





	private void enterTTSMode() {
		if (mTTransmission.init()) {
			tts_ok = true;
			mTTransmission.open();
		} else {
			tts_ok = false;
			LogUtil.e(TAG, "TTS初始化失败");
		}
	}

	/**
	 * 发送指令
	 */
	private byte sn = 0x00;
	public void sendCMD(final CMDUtils.CmdName cmdName, final int start, final int end, final String val) {

		new Thread() {
			public void run() {
				if (tts_ok) {
					LogUtil.i(TAG, "cmd = " + cmdName.name());
					if (cmdName.equals(CMDUtils.CmdName.GETSTATU)) {//  查询状态
						byte[] cmd1 = {(byte) 0xFF, (byte) 0xFF, 0x00, 0x06, 0x03, sn, 0x00, 0x00, 0x02, (byte) (sn + 0x0B)};
						sn++;
						mTTransmission.send(cmd1);
					} else {//  更改状态
						byte[] flags = CMDUtils.genFlags(cmdName);
						byte[] values = CMDUtils.genValues(cmdName, start, end, val);
						byte[] cmd2 = new byte[172];
						byte[] temp = {(byte) 0xFF, (byte) 0xFF, 0x00, (byte) 0xA8, 0x03, sn, 0x00, 0x00, 0x01,
								flags[0], flags[1], flags[2], flags[3], flags[4]};
						System.arraycopy(temp, 0, cmd2, 0, temp.length);
						System.arraycopy(values, 0, cmd2, 14, values.length);
						byte sum = 0;
						for (byte flag : flags) {
							sum += flag;
						}
						for (byte value : values) {
							sum += value;
						}

						cmd2[171] = (byte) (0xA8 + 0x03 + sn + 0x01 + sum);
						sn++;
						mTTransmission.send(cmd2);
						// 1s后获取
						mHandler.sendEmptyMessageDelayed(GET_STATUS, 1000);
					}
				}
			}
		}.start();
	}


}
