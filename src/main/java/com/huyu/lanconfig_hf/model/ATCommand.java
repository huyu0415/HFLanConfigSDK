package com.huyu.lanconfig_hf.model;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.huyu.lanconfig_hf.net.UdpUnicast;
import com.huyu.lanconfig_hf.net.UdpUnicast.UdpUnicastListener;
import com.huyu.lanconfig_hf.utils.Constants;
import com.huyu.lanconfig_hf.utils.LogUtil;
import com.huyu.lanconfig_hf.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ATCommand {

	private static final String TAG = "ATCommand";
	private static final String RESPONSE = "RESPONSE";

	private static final int CODE_ENTER_CMD_MODE_SUCCESS = 1;
	private static final int CODE_ENTER_CMD_MODE_FAILURE = 2;
	private static final int CODE_EXIT_CMD_MODE_SUCCESS = 3;
	private static final int CODE_EXIT_CMD_MODE_FAILURE = 4;
	private static final int CODE_RELOAD_SUCCESS = 5;
	private static final int CODE_RELOAD_FAILURE = 6;
	private static final int CODE_RESET_SUCCESS = 7;
	private static final int CODE_RESET_FAILURE = 8;
	private static final int CODE_CMD = 9;
	private static final int CODE_SEND_CMD_FILE_SUCCESS = 10;
	private static final int CODE_SEND_CMD_FILE_FAILURE = 11;
	private static final int CODE_SEND_CMD_FILE_RESPONSE = 12;

	private ATCommandListener listener;
	private UdpUnicast udpUnicast;
	private Handler handler;
	private boolean isCommonCMD;

	private String enterCMDModeResponse;
	private String exitCMDModeResponse;
	private String sendCMDFileResponse;
	private String reloadResponse;
	private String resetResponse;
	private String tryEnterCMDModeResponse;
	private String response;
	private int timesToTry;
	private int times;
	private NetworkProtocol protocol;

	public ATCommand() {
		super();

		timesToTry = 2;
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case CODE_ENTER_CMD_MODE_SUCCESS:
						onEnterCMDMode(true);
						new Thread(){
							public void run() {
								exitCMDMode();
							};
						}.start();
						break;
					case CODE_ENTER_CMD_MODE_FAILURE:
						onEnterCMDMode(false);
						break;
					case CODE_EXIT_CMD_MODE_SUCCESS:
						onExitCMDMode(true, protocol);
						break;
					case CODE_EXIT_CMD_MODE_FAILURE:
						onExitCMDMode(false, null);
						break;
					case CODE_RELOAD_SUCCESS:
						onReload(true);
						break;
					case CODE_RELOAD_FAILURE:
						onReload(false);
						break;
					case CODE_RESET_SUCCESS:
						onReset(true);
						break;
					case CODE_RESET_FAILURE:
						onReset(false);
						break;
					case CODE_CMD:
						onResponse(msg.getData().getString(RESPONSE));
						break;
					case CODE_SEND_CMD_FILE_SUCCESS:
						onSendFile(true);
						break;
					case CODE_SEND_CMD_FILE_FAILURE:
						onSendFile(false);
						break;
					case CODE_SEND_CMD_FILE_RESPONSE:
						onResponseOfSendFile(msg.getData().getString(RESPONSE));
						break;
					default:
						break;
				}
			}
		};
	}

	public ATCommand(UdpUnicast udpUnicast) {
		this();
		this.udpUnicast = udpUnicast;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(ATCommandListener listener) {
		this.listener = listener;
	}

	/**
	 * @param udpUnicast the udpUnicast to set
	 */
	public void setUdpUnicast(UdpUnicast udpUnicast) {
		this.udpUnicast = udpUnicast;
	}

	/**
	 * send a common command
	 * @param cmd
	 */
	public void send(String cmd) {
		if (!isCommonCMD) {
			udpUnicast.setListener(new UdpUnicast.UdpUnicastListener() {

				@Override
				public void onReceived(byte[] data, int length) {

					LogUtil.d(TAG, "onReceived[send]:" + new String(data, 0, length));

					Message msg = handler.obtainMessage(CODE_CMD);
					Bundle bundle = new Bundle();
					bundle.putString(RESPONSE, new String(data, 0, length));
					msg.setData(bundle);
					handler.sendMessage(msg);
				}
			});
			isCommonCMD = true;
		}
		udpUnicast.send(cmd);
	}

	/**
	 * Send a commmand file to device
	 * @param file
	 */
	public void sendFile(final File file) {

		times++;
		isCommonCMD = false;
		sendCMDFileResponse = null;

		//send a test cmd to verify the module is in cmd mode
		udpUnicast.setListener(new UdpUnicastListener() {

			@Override
			public void onReceived(byte[] data, int length) {
				response = new String(data, 0, length);
				sendCMDFileResponse = response.trim();
			}
		});
		if (!udpUnicast.send(Constants.CMD_TEST)) {
			handler.sendEmptyMessage(CODE_SEND_CMD_FILE_FAILURE);
			return;
		}

		waitReceiveResponse(3500, sendCMDFileResponse);

		LogUtil.d(TAG, "Response of No." + times + " times to test cmd mode:" + sendCMDFileResponse);

		if (sendCMDFileResponse == null) {
			//if there's no response, set the module enter cmd mode

			if (times < timesToTry) {

				//try to enter cmd mode
				new CMDModeTryer() {

					@Override
					void onResult(boolean success) {
						if (success) {
							//try send again
							sendFile(file);
						}else {
							handler.sendEmptyMessage(CODE_SEND_CMD_FILE_FAILURE);
						}
					}
				}.toTry(true);
			}else {
				handler.sendEmptyMessage(CODE_SEND_CMD_FILE_FAILURE);
			}
		}else if (sendCMDFileResponse.equals(Constants.RESPONSE_OK)) {
			//if it's in cmd mode, start a thread to send file

			new Thread(new Runnable() {

				@Override
				public void run() {

					try {

						BufferedReader reader = new BufferedReader(new FileReader(file));
						boolean success = true;
						String cmd = null;
						while ((cmd=reader.readLine()) != null) {

							cmd = cmd.trim();
							sendCMDFileResponse = null;
							LogUtil.d(TAG, "send cmd:" + cmd);
							routeResponse(">" + cmd +"\n");
							if (!udpUnicast.send(Utils.gernerateCMD(cmd))) {
								LogUtil.w(TAG, "Send cmd fail!");
								handler.sendEmptyMessage(CODE_SEND_CMD_FILE_FAILURE);
								success = false;
								break;
							}else {
								waitReceiveResponse(6000, sendCMDFileResponse);

								LogUtil.d(TAG, "Response of cmd[" + cmd + "]:" + sendCMDFileResponse);

								if (sendCMDFileResponse != null) {
									routeResponse(response);
								}

								if (sendCMDFileResponse == null || !sendCMDFileResponse.startsWith(Constants.RESPONSE_OK)) {
									handler.sendEmptyMessage(CODE_SEND_CMD_FILE_FAILURE);
									success = false;
									break;
								}
							}
						}

						if (success) {
							handler.sendEmptyMessage(CODE_SEND_CMD_FILE_SUCCESS);
						}
					} catch (Exception e) {
						e.printStackTrace();
						handler.sendEmptyMessage(CODE_SEND_CMD_FILE_FAILURE);
					}
				}
			}).start();
		}else {
			//if the response means it error
			handler.sendEmptyMessage(CODE_SEND_CMD_FILE_FAILURE);
		}

	}

	/**
	 * enter to command mode
	 */
	public void enterCMDMode() {
		isCommonCMD = false;
		enterCMDModeResponse = null;

		//send a test cmd to verify the module is in cmd mode
		udpUnicast.setListener(new UdpUnicastListener() {

			@Override
			public void onReceived(byte[] data, int length) {
				enterCMDModeResponse = new String(data, 0, length).trim();
			}
		});
		//  发送 测试指令：AT+\r
		if (!udpUnicast.send(Constants.CMD_TEST)) {
			handler.sendEmptyMessage(CODE_ENTER_CMD_MODE_FAILURE);
			return;
		}

		//  测试指令发送成功，回调onReceived（）， 等3.5s数据
		waitReceiveResponse(3500, enterCMDModeResponse);

		LogUtil.d(TAG, "测试指令AT+\r结果 :" + enterCMDModeResponse);

		if (enterCMDModeResponse == null) {
			//没回数据回来，就重试1次
			new CMDModeTryer() {

				@Override
				void onResult(boolean success) {
					if (success) {
						handler.sendEmptyMessage(CODE_ENTER_CMD_MODE_SUCCESS);
					}else {
						handler.sendEmptyMessage(CODE_ENTER_CMD_MODE_FAILURE);
					}
				}
			}.toTry(true);
		}else{
			handler.sendEmptyMessage(CODE_ENTER_CMD_MODE_SUCCESS);
		}
	}

	/**
	 * exit from command mode
	 */
	public void exitCMDMode() {

		times++;
		isCommonCMD = false;
		exitCMDModeResponse = null;

		//send a test cmd to verify the module is in cmd mode
		udpUnicast.setListener(new UdpUnicastListener() {

			@Override
			public void onReceived(byte[] data, int length) {
				exitCMDModeResponse = new String(data, 0, length).trim();
			}
		});
		//  发送 AT+NETP\r
		if (!udpUnicast.send(Constants.CMD_NETWORK_PROTOCOL)) {
			handler.sendEmptyMessage(CODE_EXIT_CMD_MODE_FAILURE);
			return;
		}

		//  发送成功，等待数据
		waitReceiveResponse(4000, exitCMDModeResponse);

		LogUtil.d(TAG, "第"+(times+1)+"次发送AT+NETP\r获取网络协议实体结果 :" + exitCMDModeResponse);

		if (exitCMDModeResponse == null) {
			//没有数据，重试2次
			if (times < timesToTry) {

				exitCMDMode();
			}else {
				handler.sendEmptyMessage(CODE_EXIT_CMD_MODE_FAILURE);
			}
		}else if (exitCMDModeResponse.startsWith(Constants.RESPONSE_OK_OPTION)) {
			/**正常结果格式类似为： "+ok=TCP，Server,8899,10,10,100,254"**/

			exitCMDModeResponse = exitCMDModeResponse.substring(4);
			protocol = Utils.decodeProtocol(exitCMDModeResponse);
			if (protocol == null) {
				handler.sendEmptyMessage(CODE_EXIT_CMD_MODE_FAILURE);
				return;
			}

			/**try to set device into transparent transmission mode**/
			exitCMDModeResponse = null;
			//  发送 AT+ENTM\r,进入透传，进透传前需要获得设备的网络协议实体
			if (udpUnicast.send(Constants.CMD_TRANSPARENT_TRANSMISSION)) {

				waitReceiveResponse(5000, exitCMDModeResponse);

				//  正常结果格式：+ok
				LogUtil.d(TAG, "发送 AT+ENTM\r,进入透传结果:" + exitCMDModeResponse);

				if (exitCMDModeResponse == null || !exitCMDModeResponse.equals(Constants.RESPONSE_OK)) {
					handler.sendEmptyMessage(CODE_EXIT_CMD_MODE_FAILURE);
				}else if (exitCMDModeResponse.equals(Constants.RESPONSE_OK)) {
					//  再发送 AT+Q\r  退出命令模式
					if (udpUnicast.send(Constants.CMD_EXIT_CMD_MODE)) {
						handler.sendEmptyMessage(CODE_EXIT_CMD_MODE_SUCCESS);
					}else {
						handler.sendEmptyMessage(CODE_EXIT_CMD_MODE_FAILURE);
					}
				}
			}else {
				handler.sendEmptyMessage(CODE_EXIT_CMD_MODE_FAILURE);
			}
		}else {
			//if the response means it error
			handler.sendEmptyMessage(CODE_EXIT_CMD_MODE_FAILURE);
		}
	}

	/**
	 * reload module to reset settings
	 */
	public void reload() {

		times++;
		isCommonCMD = false;
		reloadResponse = null;

		//send a test cmd to verify the module is in cmd mode
		udpUnicast.setListener(new UdpUnicastListener() {

			@Override
			public void onReceived(byte[] data, int length) {
				reloadResponse = new String(data, 0, length).trim();
			}
		});
		if (!udpUnicast.send(Constants.CMD_TEST)) {
			handler.sendEmptyMessage(CODE_RELOAD_FAILURE);
			return;
		}

		waitReceiveResponse(3500, reloadResponse);

		LogUtil.d(TAG, "Response of No." + times + " times to test cmd mode:" + reloadResponse);

		if (reloadResponse == null) {
			//if there's no response, set the module enter cmd mode

			if (times < timesToTry) {

				//try to enter cmd mode
				new CMDModeTryer() {

					@Override
					void onResult(boolean success) {
						if (success) {
							//try reload again
							reload();
						}else {
							handler.sendEmptyMessage(CODE_RELOAD_FAILURE);
						}
					}
				}.toTry(true);
			}else {
				handler.sendEmptyMessage(CODE_RELOAD_FAILURE);
			}
		}else if (reloadResponse.equals(Constants.RESPONSE_OK)) {
			//if it's in cmd mode, send reload command

			reloadResponse = null;
			if (udpUnicast.send(Constants.CMD_RELOAD)) {

				waitReceiveResponse(10000, reloadResponse);

				LogUtil.d(TAG, "Response of reload cmd:" + reloadResponse);

				if (reloadResponse == null || !reloadResponse.startsWith(Constants.RESPONSE_REBOOT_OK)) {
					handler.sendEmptyMessage(CODE_RELOAD_FAILURE);
				}else if (reloadResponse.startsWith(Constants.RESPONSE_REBOOT_OK)) {
					handler.sendEmptyMessage(CODE_RELOAD_SUCCESS);
				}
			}else {
				handler.sendEmptyMessage(CODE_RELOAD_FAILURE);
			}
		}else {
			//if the response means it error
			handler.sendEmptyMessage(CODE_RELOAD_FAILURE);
		}
	}

	/**
	 * restart module
	 */
	public synchronized void reset() {

		times++;
		isCommonCMD = false;
		resetResponse = null;

		udpUnicast.setListener(new UdpUnicastListener() {

			@Override
			public void onReceived(byte[] data, int length) {
				resetResponse = new String(data, 0, length).trim();
			}
		});
		if (!udpUnicast.send(Constants.CMD_TEST)) {
			handler.sendEmptyMessage(CODE_RESET_FAILURE);
			return;
		}

		waitReceiveResponse(3500, resetResponse);

		LogUtil.d(TAG, "Response of No." + times + " times to test cmd mode:" + resetResponse);

		if (resetResponse == null) {

			if (times < timesToTry) {

				//try to enter cmd mode
				new CMDModeTryer() {

					@Override
					void onResult(boolean success) {
						if (success) {
							reset();
						}else {
							handler.sendEmptyMessage(CODE_RESET_FAILURE);
						}
					}
				}.toTry(true);
			}else {
				handler.sendEmptyMessage(CODE_RESET_FAILURE);
			}
		}else if (resetResponse.equals(Constants.RESPONSE_OK)) {
			if (udpUnicast.send(Constants.CMD_RESET)) {
				handler.sendEmptyMessage(CODE_RESET_SUCCESS);
			}else {
				handler.sendEmptyMessage(CODE_RESET_FAILURE);
			}
		}else {
			handler.sendEmptyMessage(CODE_RESET_FAILURE);
		}
	}

	private void onEnterCMDMode(boolean success) {
		if (listener != null) {
			listener.onEnterCMDMode(success);
		}
	}

	private void onExitCMDMode(boolean success, NetworkProtocol protocol) {
		if (listener != null) {
			listener.onExitCMDMode(success, protocol);
		}
	}

	private void onReload(boolean success) {
		if (listener != null) {
			listener.onReload(success);
		}
	}

	private void onReset(boolean success) {
		if (listener != null) {
			listener.onReset(success);
		}
	}

	private void onResponse(String response) {
		if (listener != null) {
			listener.onResponse(response);
		}
	}

	private void onSendFile(boolean success) {
		if (listener != null) {
			listener.onSendFile(success);
		}
	}

	private void onResponseOfSendFile(String response) {
		if (listener != null) {
			listener.onResponseOfSendFile(response);
		}
	}

	//  重试发送
	private abstract class CMDModeTryer {

		void toTry(boolean enter) {

			tryEnterCMDModeResponse = null;

			if (enter) {

				udpUnicast.setListener(new UdpUnicastListener() {

					@Override
					public void onReceived(byte[] data, int length) {

						tryEnterCMDModeResponse = new String(data, 0, length);
					}
				});
				//  发送 HF-A11ASSISTHREAD 发送扫描头，
				if (!udpUnicast.send(Constants.CMD_SCAN_MODULES)) {
					onResult(false);
					return;
				}

				//  等待数据
				waitReceiveResponse(5000, tryEnterCMDModeResponse);

				//  正常返回结果格式应该类似： 10.10.10.101，ACCF2357761C，HF-LPB100
				LogUtil.d(TAG, "重试命令结果:" + tryEnterCMDModeResponse);
				if (tryEnterCMDModeResponse == null) {
					onResult(false);
				}else {
					String[] array = tryEnterCMDModeResponse.split(",");
					if (array != null && array.length>0 && Utils.isIP(array[0])) {
						//  发送 +ok
						if (udpUnicast.send(Constants.CMD_ENTER_CMD_MODE)) {
							onResult(true);
						}else {
							onResult(false);
						}
					}else {
						onResult(false);
					}
				}
			}
		}

		abstract void onResult(boolean success);
	}

	public void resetTimes() {
		times = 0;
	}

	// 等数据回传
	private void waitReceiveResponse(long wait, String response) {

		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < wait  && response == null) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
	}

	private void routeResponse(String response) {

		Message msg = handler.obtainMessage(CODE_SEND_CMD_FILE_RESPONSE);
		Bundle bundle = new Bundle();
		bundle.putString(RESPONSE, response);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	public void enterTTS(){
		new CMDModeTryer() {

			@Override
			void onResult(boolean success) {
				if (success) {
					handler.sendEmptyMessage(CODE_ENTER_CMD_MODE_SUCCESS);

				}else {
					handler.sendEmptyMessage(CODE_ENTER_CMD_MODE_FAILURE);
				}
			}
		}.toTry(true);
	}
}
