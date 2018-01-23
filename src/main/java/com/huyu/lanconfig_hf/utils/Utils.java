package com.huyu.lanconfig_hf.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.huyu.lanconfig_hf.model.Module;
import com.huyu.lanconfig_hf.model.NetworkProtocol;

import java.util.regex.Pattern;

public final class Utils {

	private static final String ENTER = "\r";
	public static int COMMAND = 1;
	/**
	 * Transparent Transmission
	 */
	public static int TTS = 2;
	public static int RESPONSE_CMD = 3;
	public static int RESPONSE_TTS = 4;

	/**
	 * 生成 带 \r 的命令
	 * @param text
	 * @return
	 */
	public static String gernerateCMD(String text) {

		if (text == null) {
			return null;
		}

		return text + ENTER;
	}

	/**
	 * @param type
	 * @param text
	 * @return
	 */
	public synchronized static String gernerateEchoText(int type, String text) {

		if (type == COMMAND) {

			if (text == null) {
				return ">\n";
			}else {
				return ">" + text + "\n";
			}
		}else if (type == TTS) {

			if (text == null) {
				return ">\n";
			}else {
				return ">" + text + "\n";
			}
		}else if (type == RESPONSE_CMD) {
			if (text == null) {
				return "\n";
			}else {
				return " " + text;
			}
		}else if (type == RESPONSE_TTS) {
			if (text == null) {
				return "";
			}else {
				return text;
			}
		}else {
			if (text == null) {
				return "";
			}else {
				return text;
			}
		}
	}

	/**
	 * 将 完整 响应数据 解析称 模块
	 * @param response
	 * @return
	 */
	public synchronized static Module decodeBroadcast2Module(String response) {

		if (response == null) {
			return null;
		}

		String[] array = response.split(",");
		if (array==null || (array.length<2 && array.length>3) ||
				!isIP(array[0]) || !isMAC(array[1])) {
			return null;
		}

		Module module = new Module();
		module.setIp(array[0]);
		module.setMac(array[1]);
		if (array.length == 3) {
			module.setModuleID(array[2]);
		}

		return module;
	}

	public static String appendCharacters(String oldStr, String append, int count) {
		if ((oldStr==null && append==null) || count<0) {
			return null;
		}

		if (count == 0) {
			return new String(oldStr);
		}

		StringBuffer sb;

		if (oldStr == null) {
			sb = new StringBuffer();
		}else {
			sb = new StringBuffer(oldStr);
		}
		for (int i = 0; i < count; i++) {
			sb.append(append);
		}

		return sb.toString();
	}

	/**
	 * 获得 udp 端口
	 * @param context
	 * @return
	 */
	public static int getUdpPort(Context context) {

		String port = context.getSharedPreferences(
				context.getPackageName() + "_preferences", Context.MODE_PRIVATE)
				.getString(Constants.KEY_UDP_PORT, Constants.UDP_PORT + "");
		try {
			return Integer.valueOf(port);
		} catch (Exception e) {
			return Constants.UDP_PORT;
		}
	}

	/**
	 * 获得 udp 搜索码
	 * @param context
	 * @return
	 */
	public static String getCMDScanModules(Context context) {

		return context.getSharedPreferences(
				context.getPackageName() + "_preferences", Context.MODE_PRIVATE)
				.getString(Constants.KEY_CMD_SCAN_MODULES, Constants.CMD_SCAN_MODULES);
	}

	public static void toast(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
	}

	public static void toast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static boolean isIP(String str) {
		Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])" +
				"\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\." +
				"((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\." +
				"((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		return pattern.matcher(str).matches();
	}

	public static boolean isDomain(String str) {
		if(str.length() > 30)
			return false;
		Pattern pattern = Pattern.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)?(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~/])+$");
		return pattern.matcher(str).matches();
	}

	public static boolean isMAC(String str) {

		str = str.trim();
		if (str.length() != 12) {
			return false;
		}

		char[] chars = new char[12];
		str.getChars(0, 12, chars, 0);
		for (int i = 0; i < chars.length; i++) {
			if (!((chars[i]>='0' && chars[i]<='9') || (chars[i]>='A' && chars[i]<='F') || (chars[i]>='a' && chars[i]<='f'))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 将响应解析称网络协议实体。包含 协议类型，客户端还是服务端，端口，ip
	 * @param response
	 * @return
	 */
	public synchronized static NetworkProtocol decodeProtocol(String response) {

		if (response == null) {
			return null;
		}

		String[] array = response.split(",");
		if (array == null) {
			return null;
		}

		//look for ip and port
		int index = -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals("TCP") || array[i].equals("UDP")) {
				index = i;
				break;
			}
		}
		if (index == -1) {
			return null;
		}

		try {
			if (!isIP(array[index+3])) {
				return null;
			}

			NetworkProtocol protocol = new NetworkProtocol();
			protocol.setProtocol(array[0]);
			protocol.setServer(array[1]);
			protocol.setPort(Integer.valueOf(array[index+2]));
			protocol.setIp(array[index+3]);

			return protocol;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//  生成进度对话框
	public static ProgressDialog genProgressDialog(Context context, String message){
		ProgressDialog dialog=new ProgressDialog(context);
		dialog.setIndeterminate(true);
		dialog.setMessage(message);
		dialog.setCancelable(false);
		return dialog;
	}
}
