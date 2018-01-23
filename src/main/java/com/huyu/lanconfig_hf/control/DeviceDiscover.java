package com.huyu.lanconfig_hf.control;

import com.huyu.lanconfig_hf.model.Module;
import com.huyu.lanconfig_hf.net.UdpBroadcast;
import com.huyu.lanconfig_hf.utils.Constants;
import com.huyu.lanconfig_hf.utils.LogUtil;
import com.huyu.lanconfig_hf.utils.Utils;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

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

public class DeviceDiscover {

	private final String TAG = "DeviceDiscover";
	private long lastTime;
	private UdpBroadcast udpBroadcast;
	private DiscoverListener mDiscoverListener;

	public DeviceDiscover() {

		udpBroadcast = new UdpBroadcast() {

			@Override
			public void onReceived(List<DatagramPacket> packets) {
				if(null != mDiscoverListener)
					mDiscoverListener.onreceived(decodePackets(packets));
			}
		};
	}

	public void startDiscover(long timeInterval){
		udpBroadcast.open();
		if (System.currentTimeMillis() - lastTime > timeInterval) {
			// 发送广播，搜索码：HF-A11ASSISTHREAD ）
			udpBroadcast.send(Constants.CMD_SCAN_MODULES);
			lastTime = System.currentTimeMillis();
		}
	}

	public void stopDiscover(){
		udpBroadcast.close();
	}

	public void setDiscoverListener(DiscoverListener discoverListener) {
		mDiscoverListener = discoverListener;
	}

	/**
	 *  将接收到的数据包解析称模块
	 *  正常数据类似：
	 *   HF-AllASSISTHREAD
	 *   10.10.10.101,ACCF2357761C,HF-LPB100
	 *   10.10.10.102,ACCF2357762C,HF-LPB100
	 *   ...
	 * decode pagkets to mudoles
	 * @param packets
	 * @return
	 */
	private List<Module> decodePackets(List<DatagramPacket> packets) {

		int i = 1;
		Module module;
		List<String> list = new ArrayList<>();
		List<Module> modules = new ArrayList<>();

		DECODE_PACKETS:
		for (DatagramPacket packet : packets) {

			String data = new String(packet.getData(), 0, packet.getLength());
			if (data.equals(Constants.CMD_SCAN_MODULES)) {
				continue;
			}
			LogUtil.i(TAG, data);

			for (String item : list) {
				if (item.equals(data)) {
					continue DECODE_PACKETS;
				}
			}

			list.add(data);
			if ((module = Utils.decodeBroadcast2Module(data)) != null) {
				module.setId(i);
				modules.add(module);
				i++;
			}
		}

		return modules;
	}


	public interface DiscoverListener {
		void onreceived(List<Module> mModules);
	}
}
