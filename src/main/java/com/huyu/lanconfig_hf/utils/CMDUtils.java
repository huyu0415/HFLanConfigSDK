package com.huyu.lanconfig_hf.utils;

import android.util.Log;

import com.huyu.lanconfig_hf.entity.AutoAlarmEntity;
import com.huyu.lanconfig_hf.entity.ZoneEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class CMDUtils {

	private static final String TAG = "CMDUtils";

	public enum CmdName {
		ARM_DISARM, ALARMTONE, ZONE1ATTR, ZONE2ATTR, ZONE3ATTR, ZONE4ATTR, ZONE5ATTR, ZONE6ATTR, ZONE7ATTR, ZONE8ATTR,
		VOICEVOL, ALARMVOL, RINGTIMES, ALARMNUM1, ALARMNUM2, ALARMNUM3, AUTOARM1, AUTOARM2, AUTOARM3, AUTOARM4, PASSWORD,
		RTC, LANGUAGE, IP, PORT, CID, EFFECTCODE1, EFFECTCODE2, EFFECTCODE3, EFFECTCODE4, EFFECTCODE5, EFFECTCODE6,
		EFFECTCODE7, EFFECTCODE8, GETSTATU, VERSION
	}

	/**
	 * 验证数据头是不是 FF FF 00 A4
	 */
	public static boolean vervifyData(byte[] fullData) {
		return ByteUtils.Byte2HexString(fullData[0]).equals("FF") && (ByteUtils.Byte2HexString(fullData[3]).equals("A4"));
	}

	public static ConcurrentHashMap<CmdName, Object> parseData(byte[] fullData) {
		ConcurrentHashMap<CmdName, Object> map = new ConcurrentHashMap<>();
//		LogUtil.i(TAG + "_收到原始字节数据：", Arrays.toString(fullData));
		String packageData = ByteUtils.Bytes2HexString(fullData);
		String[] data1 = packageData.split(" ");
		LogUtil.i(TAG + "_字节数据转为16进制数据：", Arrays.toString(data1));
		if (vervifyData(fullData)) {
			byte sum = 0;
			for (int i = 2; i < 167; i++) {
				sum += fullData[i];
			}

			if (sum == fullData[167]) {
				String[] data = new String[158];
				System.arraycopy(data1, 9, data, 0, 158);
				LogUtil.i(TAG + "_设备数据", Arrays.toString(data));


				//  data[0]--data[3]  00, 00, 00, 14
				StringBuilder str_zone = new StringBuilder();
				for (int i = 0; i < 4; i++) {
					str_zone.append(data[i]);
				}
				String ZoneAttr = ByteUtils.hexString2binaryString(str_zone.toString());
				//  00000000000000000000000000010100
				//  0.布防 1.撤防 2.留守
				String arm = ZoneAttr.substring(30);
				int Arm_Disarm = Integer.parseInt(arm, 2);
				map.put(CmdName.ARM_DISARM, Arm_Disarm);

				//  AlarmTone 报警声
				String tone = ZoneAttr.substring(28, 30);
				int AlarmTone = Integer.parseInt(tone, 2);
				//  0.警笛 1.狗叫声 2.鬼叫声1 3.鬼叫声2
				map.put(CmdName.ALARMTONE, AlarmTone);

				//  000普通，001留守，010智能，011紧急，100关闭，101门铃，110迎宾，111老人
				//  防区事件码： 100：医疗， 110：火警，121：劫警，122：无声劫警，130：匪警，131：周界，151：燃气
				//  Zone1Attr 1防区属性
				ZoneEntity zoneEntity1 = new ZoneEntity();
				String att1 = ZoneAttr.substring(25, 28);
				StringBuilder effectCode1 = new StringBuilder();
				for (int i = 133; i < 136; i++) {
					effectCode1.append((char) fullData[i + 9]);
				}
				zoneEntity1.setAttr(att1);
				zoneEntity1.setCode(effectCode1.toString());
				map.put(CmdName.ZONE1ATTR, zoneEntity1);
				//  Zone2Attr 2防区属性
				String att2 = ZoneAttr.substring(22, 25);
				ZoneEntity zoneEntity2 = new ZoneEntity();
				StringBuilder effectCode2 = new StringBuilder();
				for (int i = 136; i < 139; i++) {
					effectCode2.append((char) fullData[i + 9]);
				}
				zoneEntity2.setAttr(att2);
				zoneEntity2.setCode(effectCode2.toString());
				map.put(CmdName.ZONE2ATTR, zoneEntity2);

				//  Zone3Attr 3防区属性
				String att3 = ZoneAttr.substring(19, 22);
				ZoneEntity zoneEntity3 = new ZoneEntity();
				StringBuilder effectCode3 = new StringBuilder();
				for (int i = 139; i < 142; i++) {
					effectCode3.append((char) fullData[i + 9]);
				}
				zoneEntity3.setAttr(att3);
				zoneEntity3.setCode(effectCode3.toString());
				map.put(CmdName.ZONE3ATTR, zoneEntity3);

				//  Zone4Attr 4防区属性
				String att4 = ZoneAttr.substring(16, 19);
				ZoneEntity zoneEntity4 = new ZoneEntity();
				StringBuilder effectCode4 = new StringBuilder();
				for (int i = 142; i < 145; i++) {
					effectCode4.append((char) fullData[i + 9]);
				}
				zoneEntity4.setAttr(att4);
				zoneEntity4.setCode(effectCode4.toString());
				map.put(CmdName.ZONE4ATTR, zoneEntity4);

				//  Zone5Attr 5防区属性
				String att5 = ZoneAttr.substring(13, 16);
				ZoneEntity zoneEntity5 = new ZoneEntity();
				StringBuilder effectCode5 = new StringBuilder();
				for (int i = 145; i < 148; i++) {
					effectCode5.append((char) fullData[i + 9]);
				}
				zoneEntity5.setAttr(att5);
				zoneEntity5.setCode(effectCode5.toString());
				map.put(CmdName.ZONE5ATTR, zoneEntity5);

				//  Zone6Attr 6防区属性
				String att6 = ZoneAttr.substring(10, 13);
				ZoneEntity zoneEntity6 = new ZoneEntity();
				StringBuilder effectCode6 = new StringBuilder();
				for (int i = 148; i < 151; i++) {
					effectCode6.append((char) fullData[i + 9]);
				}
				zoneEntity6.setAttr(att6);
				zoneEntity6.setCode(effectCode6.toString());
				map.put(CmdName.ZONE6ATTR, zoneEntity6);

				//  Zone7Attr 7防区属性
				String att7 = ZoneAttr.substring(7, 10);
				ZoneEntity zoneEntity7 = new ZoneEntity();
				StringBuilder effectCode7 = new StringBuilder();
				for (int i = 151; i < 154; i++) {
					effectCode7.append((char) fullData[i + 9]);
				}
				zoneEntity7.setAttr(att7);
				zoneEntity7.setCode(effectCode7.toString());
				map.put(CmdName.ZONE7ATTR, zoneEntity7);

				//  Zone8Attr 8防区属性
				String att8 = ZoneAttr.substring(4, 7);
				ZoneEntity zoneEntity8 = new ZoneEntity();
				StringBuilder effectCode8 = new StringBuilder();
				for (int i = 154; i < 157; i++) {
					effectCode8.append((char) fullData[i + 9]);
				}
				zoneEntity8.setAttr(att8);
				zoneEntity8.setCode(effectCode8.toString());
				map.put(CmdName.ZONE8ATTR, zoneEntity8);

				// voiceVol 语音音量  0--10
				String voice_val = data[4];
				int VoiceVol = Integer.parseInt(voice_val);
				map.put(CmdName.VOICEVOL, VoiceVol);

				// voiceVol 报警音量  0--10
				String alarm_vol = data[5];
				int AlarmVol = Integer.parseInt(alarm_vol);
				map.put(CmdName.ALARMVOL, AlarmVol);

				// RingTimes 报警次数  0--20
				String ringtimes = data[6];
				int RingTimes = Integer.parseInt(ringtimes, 16);
				map.put(CmdName.RINGTIMES, RingTimes);

				//  报警号码 1
				StringBuilder alarmnum1 = new StringBuilder();
				for (int i = 7; i < 24; i++) {
					alarmnum1.append(data[i]);
				}
				int len1 = Integer.parseInt(alarmnum1.substring(0, 2));
				String AlarmNum1 = alarmnum1.substring(2, len1 + 2);
				if (AlarmNum1.contains("A")) {
					AlarmNum1 = AlarmNum1.replace('A', '*');
				}
				map.put(CmdName.ALARMNUM1, AlarmNum1);

				//  报警号码 2
				StringBuilder alarmnum2 = new StringBuilder();
				for (int i = 24; i < 41; i++) {
					alarmnum2.append(data[i]);
				}
				int len2 = Integer.parseInt(alarmnum2.substring(0, 2));
				String AlarmNum2 = alarmnum2.substring(2, len2 + 2);
				if (AlarmNum2.contains("A")) {
					AlarmNum2 = AlarmNum2.replace('A', '*');
				}
				map.put(CmdName.ALARMNUM2, AlarmNum2);

				//  报警号码 3
				StringBuilder alarmnum3 = new StringBuilder();
				for (int i = 41; i < 58; i++) {
					alarmnum3.append(data[i]);
				}
				int len3 = Integer.parseInt(alarmnum3.substring(0, 2));
				String AlarmNum3 = alarmnum3.substring(2, len3 + 2);
				if (AlarmNum3.contains("A")) {
					AlarmNum3 = AlarmNum3.replace('A', '*');
				}
				map.put(CmdName.ALARMNUM3, AlarmNum3);

				//AutoArm1 定时布撤防1
				StringBuilder autoalarm1 = new StringBuilder();
				for (int i = 58; i < 64; i++) {
					autoalarm1.append(data[i]);
				}
				AutoAlarmEntity autoAlarmEntity1 = new AutoAlarmEntity();
				int isSet1 = Integer.parseInt(autoalarm1.substring(1, 2));
				if (isSet1 == 0) {
					autoAlarmEntity1.setIsSet(false);
				} else if (isSet1 == 1) {
					autoAlarmEntity1.setIsSet(true);
				}
				int isOn1 = Integer.parseInt(autoalarm1.substring(3, 4));
				if (isOn1 == 0) {
					autoAlarmEntity1.setIsOn(false);
				} else if (isOn1 == 1) {
					autoAlarmEntity1.setIsOn(true);
				}
				int arm_disarm1 = Integer.parseInt(autoalarm1.substring(11));
				if (arm_disarm1 == 0) {
					autoAlarmEntity1.setArmOrDisarm("arm");
				} else if (arm_disarm1 == 1) {
					autoAlarmEntity1.setArmOrDisarm("disarm");
				}
				String hour1 = autoalarm1.substring(4, 6);
				autoAlarmEntity1.setHour(hour1);
				String minute1 = autoalarm1.substring(6, 8);
				autoAlarmEntity1.setMinute(minute1);
				String autoAlarmBinary1 = ByteUtils.hexString2binaryString(autoalarm1.substring(8, 10));
				String week1 = autoAlarmBinary1.substring(1);
				autoAlarmEntity1.setWeekRepeat(week1);
				map.put(CmdName.AUTOARM1, autoAlarmEntity1);

				//AutoArm2 定时布撤防2
				StringBuilder autoalarm2 = new StringBuilder();
				for (int i = 64; i < 70; i++) {
					autoalarm2.append(data[i]);
				}
				AutoAlarmEntity autoAlarmEntity2 = new AutoAlarmEntity();
				int isSet2 = Integer.parseInt(autoalarm2.substring(0, 2), 16);
				if (isSet2 == 0) {
					autoAlarmEntity2.setIsSet(false);
				} else if (isSet2 == 1) {
					autoAlarmEntity2.setIsSet(true);
				}
				int isOn2 = Integer.parseInt(autoalarm2.substring(2, 4), 16);
				if (isOn2 == 0) {
					autoAlarmEntity2.setIsOn(false);
				} else if (isOn2 == 1) {
					autoAlarmEntity2.setIsOn(true);
				}
				int arm_disarm2 = Integer.parseInt(autoalarm2.substring(10), 16);
				if (arm_disarm2 == 0) {
					autoAlarmEntity2.setArmOrDisarm("arm");
				} else if (arm_disarm2 == 1) {
					autoAlarmEntity2.setArmOrDisarm("disarm");
				}
				String hour2 = autoalarm2.substring(4, 6);
				autoAlarmEntity2.setHour(hour2);
				String minute2 = autoalarm2.substring(6, 8);
				autoAlarmEntity2.setMinute(minute2);
				String autoAlarmBinary2 = ByteUtils.hexString2binaryString(autoalarm2.substring(8, 10));
				String week2 = autoAlarmBinary2.substring(1);
				autoAlarmEntity2.setWeekRepeat(week2);
				map.put(CmdName.AUTOARM2, autoAlarmEntity2);

				//AutoArm3 定时布撤防3
				StringBuilder autoalarm3 = new StringBuilder();
				for (int i = 70; i < 76; i++) {
					autoalarm3.append(data[i]);
				}
				AutoAlarmEntity autoAlarmEntity3 = new AutoAlarmEntity();
				int isSet3 = Integer.parseInt(autoalarm3.substring(0, 2), 16);
				if (isSet3 == 0) {
					autoAlarmEntity3.setIsSet(false);
				} else if (isSet3 == 1) {
					autoAlarmEntity3.setIsSet(true);
				}
				int isOn3 = Integer.parseInt(autoalarm3.substring(2, 4), 16);
				if (isOn3 == 0) {
					autoAlarmEntity3.setIsOn(false);
				} else if (isOn3 == 1) {
					autoAlarmEntity3.setIsOn(true);
				}
				int arm_disarm3 = Integer.parseInt(autoalarm3.substring(10), 16);
				if (arm_disarm3 == 0) {
					autoAlarmEntity3.setArmOrDisarm("arm");
				} else if (arm_disarm3 == 1) {
					autoAlarmEntity3.setArmOrDisarm("disarm");
				}
				String hour3 = autoalarm3.substring(4, 6);
				autoAlarmEntity3.setHour(hour3);
				String minute3 = autoalarm3.substring(6, 8);
				autoAlarmEntity3.setMinute(minute3);
				String autoAlarmBinary3 = ByteUtils.hexString2binaryString(autoalarm3.substring(8, 10));
				String week3 = autoAlarmBinary3.substring(1);
				autoAlarmEntity3.setWeekRepeat(week3);
				map.put(CmdName.AUTOARM3, autoAlarmEntity3);

				//AutoArm4 定时布撤防4
				StringBuilder autoalarm4 = new StringBuilder();
				for (int i = 76; i < 82; i++) {
					autoalarm4.append(data[i]);
				}
				AutoAlarmEntity autoAlarmEntity4 = new AutoAlarmEntity();
				int isSet4 = Integer.parseInt(autoalarm4.substring(0, 2), 16);
				if (isSet4 == 0) {
					autoAlarmEntity4.setIsSet(false);
				} else if (isSet4 == 1) {
					autoAlarmEntity4.setIsSet(true);
				}
				int isOn4 = Integer.parseInt(autoalarm4.substring(2, 4), 16);
				if (isOn4 == 0) {
					autoAlarmEntity4.setIsOn(false);
				} else if (isOn4 == 1) {
					autoAlarmEntity4.setIsOn(true);
				}
				int arm_disarm4 = Integer.parseInt(autoalarm4.substring(10), 16);
				if (arm_disarm4 == 0) {
					autoAlarmEntity4.setArmOrDisarm("arm");
				} else if (arm_disarm4 == 1) {
					autoAlarmEntity4.setArmOrDisarm("disarm");
				}
				String hour4 = autoalarm4.substring(4, 6);
				autoAlarmEntity4.setHour(hour4);
				String minute4 = autoalarm4.substring(6, 8);
				autoAlarmEntity4.setMinute(minute4);
				String autoAlarmBinary4 = ByteUtils.hexString2binaryString(autoalarm4.substring(8, 10));
				String week4 = autoAlarmBinary4.substring(1);
				autoAlarmEntity4.setWeekRepeat(week4);
				map.put(CmdName.AUTOARM4, autoAlarmEntity4);

				// password
				StringBuilder password = new StringBuilder();
				for (int i = 82; i < 85; i++) {
					password.append(data[i]);
				}
				map.put(CmdName.PASSWORD, password.toString());

				// RCT 当前时间    15, 09, 02, 12, 56, 20  2015.09.02 12:56:20
				StringBuilder rtc = new StringBuilder();
				for (int i = 85; i < 91; i++) {
					rtc.append(data[i]);
				}
				map.put(CmdName.RTC, rtc.toString());

				// language   0表示中文，1表示英文，2表示俄语，3表示西班牙语，4表示法语
				String language = data[91];
				int Language = Integer.parseInt(language, 16);
				map.put(CmdName.LANGUAGE, Language);

				//ip
				StringBuilder ip = new StringBuilder();
				for (int i = 92; i < 122; i++) {
					ip.append((char) fullData[i + 9]);
				}
				map.put(CmdName.IP, ip.toString().trim());

				//port
				StringBuilder port = new StringBuilder();
				for (int i = 122; i < 127; i++) {
					port.append((char) fullData[i + 9]);
				}
				map.put(CmdName.PORT, port.toString().trim());

				//CID
				StringBuilder cid = new StringBuilder();
				for (int i = 127; i < 133; i++) {
					cid.append((char) fullData[i + 9]);
				}
				map.put(CmdName.CID, cid.toString().trim());

				//version
				String version = "" + Integer.parseInt(data[157], 16);
				map.put(CmdName.VERSION, version.charAt(0) + "." + version.charAt(1));

				return map;
			}

			Log.i("TAG 数据校验和有误", "检查数据校验和");
			return map;
		}
		Log.i("TAG 解析数据头有误", "检查数据头是否是FF,FF,00,A4");
		return map;
	}

	private static HashMap<CmdName, Integer> cmdNameFlag;

	/**
	 * 生成标志位
	 */
	public static byte[] genFlags(CmdName cmdName) {
		if (cmdNameFlag == null) {
			cmdNameFlag = new HashMap<>();
			cmdNameFlag.put(CmdName.ARM_DISARM, 39);
			cmdNameFlag.put(CmdName.ALARMTONE, 38);
			cmdNameFlag.put(CmdName.ZONE1ATTR, 37);
			cmdNameFlag.put(CmdName.ZONE2ATTR, 36);
			cmdNameFlag.put(CmdName.ZONE3ATTR, 35);
			cmdNameFlag.put(CmdName.ZONE4ATTR, 34);
			cmdNameFlag.put(CmdName.ZONE5ATTR, 33);
			cmdNameFlag.put(CmdName.ZONE6ATTR, 32);
			cmdNameFlag.put(CmdName.ZONE7ATTR, 31);
			cmdNameFlag.put(CmdName.ZONE8ATTR, 30);
			cmdNameFlag.put(CmdName.VOICEVOL, 29);
			cmdNameFlag.put(CmdName.ALARMVOL, 28);
			cmdNameFlag.put(CmdName.RINGTIMES, 27);
			cmdNameFlag.put(CmdName.ALARMNUM1, 26);
			cmdNameFlag.put(CmdName.ALARMNUM2, 25);
			cmdNameFlag.put(CmdName.ALARMNUM3, 24);
			cmdNameFlag.put(CmdName.AUTOARM1, 23);
			cmdNameFlag.put(CmdName.AUTOARM2, 22);
			cmdNameFlag.put(CmdName.AUTOARM3, 21);
			cmdNameFlag.put(CmdName.AUTOARM4, 20);
			cmdNameFlag.put(CmdName.PASSWORD, 19);
			cmdNameFlag.put(CmdName.RTC, 18);
			cmdNameFlag.put(CmdName.LANGUAGE, 17);
			cmdNameFlag.put(CmdName.IP, 16);
			cmdNameFlag.put(CmdName.PORT, 15);
			cmdNameFlag.put(CmdName.CID, 14);
			cmdNameFlag.put(CmdName.EFFECTCODE1, 13);
			cmdNameFlag.put(CmdName.EFFECTCODE2, 12);
			cmdNameFlag.put(CmdName.EFFECTCODE3, 11);
			cmdNameFlag.put(CmdName.EFFECTCODE4, 10);
			cmdNameFlag.put(CmdName.EFFECTCODE5, 9);
			cmdNameFlag.put(CmdName.EFFECTCODE6, 8);
			cmdNameFlag.put(CmdName.EFFECTCODE7, 7);
			cmdNameFlag.put(CmdName.EFFECTCODE8, 6);
		}
		int position = cmdNameFlag.get(cmdName);
		StringBuilder flags = new StringBuilder();
		if (position == 14 || position == 15 || position == 16) {
			flags = new StringBuilder("0000000000000011100000000000000000000000");
		} else if (position == 23) {
			flags = new StringBuilder("0000000000000000000000110000000000000000");
		} else if (position == 21) {
			flags = new StringBuilder("0000000000000000000011000000000000000000");
		} else {
			for (int i = 0; i < 40; i++) {
				if (i == position) {
					flags.append("1");
					continue;
				}
				flags.append("0");
			}
		}
		String hex = ByteUtils.b2h(flags.toString());
		return ByteUtils.HexString2Bytes(hex);
	}

	private static String[] value;

	public static byte[] genValues(CmdName cmdName, int start, int end, String val) {
		if (null == value || value.length != 157) {
			value = new String[157];
			Arrays.fill(value, "00");
		}
		switch (cmdName) {
			case ARM_DISARM://30,32
			case ALARMTONE://28,30
			case ZONE1ATTR://25,28
			case ZONE2ATTR://22,25
			case ZONE3ATTR://19,22
			case ZONE4ATTR://16,19
			case ZONE5ATTR://13,16
			case ZONE6ATTR://10,13
			case ZONE7ATTR://7,10
			case ZONE8ATTR://4,7   传参如：0~31 0~31 10或者111
				StringBuilder hexString = new StringBuilder();
				for (int i = 0; i < 4; i++) {
					hexString.append(value[i]);
				}
				String binary = ByteUtils.hexString2binaryString(hexString.toString());
				StringBuilder sb = new StringBuilder();
				sb.append(binary);
				sb.replace(start, end, val);
				String hex = ByteUtils.b2h(sb.toString());
				for (int i = 0; i < 4; i++) {
					value[i] = hex.substring(i * 2, i * 2 + 2);
				}
				break;
			case VOICEVOL://  传参如"0A"
				value[4] = val;
				break;
			case ALARMVOL://  传参如"0A"
				value[5] = val;
				break;
			case RINGTIMES://  传参如"0A"
				value[6] = val;
				break;
			case ALARMNUM1://  传参如"0595255000000000000000000000000000"
				for (int i = 7; i < 24; i++) {
					value[i] = val.substring((i - 7) * 2, (i - 7) * 2 + 2);
				}
			case ALARMNUM2:
				for (int i = 24; i < 41; i++) {
					value[i] = val.substring((i - 24) * 2, (i - 24) * 2 + 2);
				}
			case ALARMNUM3:
				for (int i = 41; i < 58; i++) {
					value[i] = val.substring((i - 41) * 2, (i - 41) * 2 + 2);
				}
				break;
			case AUTOARM1://  传参如：010000000000
			case AUTOARM2:
				int index1 = val.indexOf("*");
				String val1 = val.substring(0, index1);
				String val2 = val.substring(index1 + 1);
				for (int i = 58; i < 64; i++) {
					value[i] = val1.substring((i - 58) * 2, (i - 58) * 2 + 2);
				}
				for (int i = 64; i < 70; i++) {
					value[i] = val2.substring((i - 64) * 2, (i - 64) * 2 + 2);
				}
				break;
			case AUTOARM3://  传参如：010000000000
			case AUTOARM4:
				int index2 = val.indexOf("*");
				String val3 = val.substring(0, index2);
				String val4 = val.substring(index2 + 1);
				for (int i = 70; i < 76; i++) {
					value[i] = val3.substring((i - 70) * 2, (i - 70) * 2 + 2);
				}
				for (int i = 76; i < 82; i++) {
					value[i] = val4.substring((i - 76) * 2, (i - 76) * 2 + 2);
				}
				break;
			case PASSWORD://  传参如："123456"
				for (int i = 82; i < 85; i++) {
					value[i] = val.substring((i - 82) * 2, (i - 82) * 2 + 2);
				}
				break;
			case RTC://  传参如："150902125620"
				for (int i = 85; i < 91; i++) {
					value[i] = val.substring((i - 85) * 2, (i - 85) * 2 + 2);
				}
				break;
			case LANGUAGE://  传参如："00"
				value[91] = val;
				break;
			case IP:
			case PORT:
			case CID://  30+5+6字节  传参如："122.22.56.9+6501*000902"
				int p1 = val.indexOf("+");
				int p2 = val.indexOf("*");

				String val_ip = val.substring(0, p1);
				String val_port = val.substring(p1 + 1, p2);
				String val_cid = val.substring(p2 + 1);
				char ips[] = val_ip.toCharArray();
				StringBuilder ip = new StringBuilder();
				for (char ip1 : ips) {
					ip.append(Integer.toHexString((int) ip1));
				}
				for (int j = (ip.length()) / 2; j < 30; j++) {
					ip.append("00");
				}

				char ports[] = val_port.toCharArray();
				StringBuilder p = new StringBuilder();
				for (char port : ports) {
					p.append(Integer.toHexString((int) port));
				}
				for (int j = (p.length()) / 2; j < 5; j++) {
					p.append("00");
				}

				char cids[] = val_cid.toCharArray();
				StringBuilder c = new StringBuilder();
				for (char cid : cids) {
					c.append(Integer.toHexString((int) cid));
				}
				for (int j = (c.length()) / 2; j < 6; j++) {
					c.append("00");
				}

				String min = ip.toString() + p + c;
				for (int i = 92; i < 133; i++) {
					value[i] = min.substring((i - 92) * 2, (i - 92) * 2 + 2);
				}
				break;
			case EFFECTCODE1: // 传参：100：医疗， 110：火警，121：劫警，122：无声劫警，130：匪警，131：周界，151：燃气
				StringBuilder eff1 = new StringBuilder();
				for (int k = 0; k < val.length(); k++) {
					eff1.append(Integer.toHexString((int) (val.charAt(k))));
				}
				for (int i = 133; i < 136; i++) {
					value[i] = eff1.substring((i - 133) * 2, (i - 133) * 2 + 2);
				}
				break;
			case EFFECTCODE2: // 传参：100：医疗， 110：火警，121：劫警，122：无声劫警，130：匪警，131：周界，151：燃气
				StringBuilder eff2 = new StringBuilder();
				for (int k = 0; k < val.length(); k++) {
					eff2.append(Integer.toHexString((int) (val.charAt(k))));
				}
				for (int i = 136; i < 139; i++) {
					value[i] = eff2.substring((i - 136) * 2, (i - 136) * 2 + 2);
				}
				break;
			case EFFECTCODE3: // 传参：100：医疗， 110：火警，121：劫警，122：无声劫警，130：匪警，131：周界，151：燃气
				StringBuilder eff3 = new StringBuilder();
				for (int k = 0; k < val.length(); k++) {
					eff3.append(Integer.toHexString((int) (val.charAt(k))));
				}
				for (int i = 139; i < 142; i++) {
					value[i] = eff3.substring((i - 139) * 2, (i - 139) * 2 + 2);
				}
				break;
			case EFFECTCODE4: // 传参：100：医疗， 110：火警，121：劫警，122：无声劫警，130：匪警，131：周界，151：燃气
				StringBuilder eff4 = new StringBuilder();
				for (int k = 0; k < val.length(); k++) {
					eff4.append(Integer.toHexString((int) (val.charAt(k))));
				}
				for (int i = 142; i < 145; i++) {
					value[i] = eff4.substring((i - 142) * 2, (i - 142) * 2 + 2);
				}
				break;
			case EFFECTCODE5: // 传参：100：医疗， 110：火警，121：劫警，122：无声劫警，130：匪警，131：周界，151：燃气
				StringBuilder eff5 = new StringBuilder();
				for (int k = 0; k < val.length(); k++) {
					eff5.append(Integer.toHexString((int) (val.charAt(k))));
				}
				for (int i = 145; i < 148; i++) {
					value[i] = eff5.substring((i - 145) * 2, (i - 145) * 2 + 2);
				}
				break;
			case EFFECTCODE6: // 传参：100：医疗， 110：火警，121：劫警，122：无声劫警，130：匪警，131：周界，151：燃气
				StringBuilder eff6 = new StringBuilder();
				for (int k = 0; k < val.length(); k++) {
					eff6.append(Integer.toHexString((int) (val.charAt(k))));
				}
				for (int i = 148; i < 151; i++) {
					value[i] = eff6.substring((i - 148) * 2, (i - 148) * 2 + 2);
				}
				break;
			case EFFECTCODE7: // 传参：100：医疗， 110：火警，121：劫警，122：无声劫警，130：匪警，131：周界，151：燃气
				StringBuilder eff7 = new StringBuilder();
				for (int k = 0; k < val.length(); k++) {
					eff7.append(Integer.toHexString((int) (val.charAt(k))));
				}
				for (int i = 151; i < 154; i++) {
					value[i] = eff7.substring((i - 151) * 2, (i - 151) * 2 + 2);
				}
				break;
			case EFFECTCODE8: // 传参：100：医疗， 110：火警，121：劫警，122：无声劫警，130：匪警，131：周界，151：燃气
				StringBuilder eff8 = new StringBuilder();
				for (int k = 0; k < val.length(); k++) {
					eff8.append(Integer.toHexString((int) (val.charAt(k))));
				}
				for (int i = 154; i < 157; i++) {
					value[i] = eff8.substring((i - 154) * 2, (i - 154) * 2 + 2);
				}
				break;
			default:
				break;
		}
		StringBuilder newHex = new StringBuilder();
		for (String aValue : value) {
			newHex.append(aValue);
		}
		return ByteUtils.HexString2Bytes(newHex.toString());
	}

}
