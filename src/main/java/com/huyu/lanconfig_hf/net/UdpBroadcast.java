package com.huyu.lanconfig_hf.net;

import com.huyu.lanconfig_hf.utils.Constants;
import com.huyu.lanconfig_hf.utils.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public abstract class UdpBroadcast {

	/**
	 * 定义 1.setPort();设置端口号
	 * 2.open（） 开始，在 onStart()中初始化
	 * 3.close()  ,在 onStop()中调用
	 * 4.send()  发送udp广播，
	 * 5.stopReceive()  停止接收
	 * 6.onReceived（）  UdpBroadcast子类必须实现的接口，用来处理收到的数据
	 */
	private static final String TAG = "UdpBroadcast";
	private static final int BUFFER_SIZE = 100;

	private int port = Constants.UDP_PORT;
	private DatagramSocket socket;
	private DatagramPacket packetToSend;
	private InetAddress inetAddress;
	private ReceiveData receiveData;

	public void setPort(int port) {
		this.port = port;
	}

	public UdpBroadcast() {
		super();

		try {
			inetAddress = InetAddress.getByName("255.255.255.255");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 打开udp连接
	 * Open udp socket
	 */
	public void open() {

		try {
			//			socket = new DatagramSocket(port);   // 偶尔因为手机端口被占用，未来得及释放，会报 address already in use,改换下面代码
			if(socket==null ){
				socket = new DatagramSocket(null);
				socket.setReuseAddress(true);
				socket.bind(new InetSocketAddress(port));
			}
			socket.setBroadcast(true);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 关闭udp连接
	 * Close udp socket
	 */
	public void close() {
		stopReceive();
		if (socket != null) {
			socket.close();
			socket=null;
		}
	}

	/**
	 * 发送信息
	 * broadcast message
	 * @param text
	 * 			the message to broadcast
	 */
	public void send(String text) {
		if (socket == null || text == null) {
			return;
		}

		text = text.trim();
		packetToSend = new DatagramPacket(
				text.getBytes(), text.getBytes().length, inetAddress, port);

		try {
			socket.setSoTimeout(200);
			stopReceive();

			new Thread() {
				@Override
				public void run() {

					//remove the data in read chanel
					//					DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
					//					while (true) {
					//						try {
					//							socket.receive(packet);
					//						} catch (Exception e) {
					//							break;
					//						}
					//					}

					//send data
					try {
						socket.setSoTimeout(3000);
						socket.send(packetToSend);
						LogUtil.e(TAG , "send data = "
								+packetToSend.getSocketAddress()+";"
								+new String(packetToSend.getData()));
					} catch (Exception e) {
						e.printStackTrace();
					}

					//receive response，启线程，在15秒内接收数据，回调
					receiveData = new ReceiveData();
					receiveData.start();
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stop to receive
	 */
	public void stopReceive() {

		if (receiveData!=null && !receiveData.isStoped()) {
			receiveData.stop();
		}
	}

	public abstract void onReceived(List<DatagramPacket> packets);

	private class ReceiveData implements Runnable {

		private boolean stop;
		private Thread thread;
		private List<DatagramPacket> packets;

		private ReceiveData() {
			thread = new Thread(this);
			packets = new ArrayList<DatagramPacket>();
		}

		@Override
		public void run() {

			long time = System.currentTimeMillis();

			int i= 0;
			while (System.currentTimeMillis() - time < 10000 && !stop) {
				try {
					DatagramPacket packetToReceive = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
					socket.receive(packetToReceive);
					LogUtil.e(TAG, "packetToReceive"+(++i)+ "= "+packetToReceive.getSocketAddress()+"\n"+new String(packetToReceive.getData()));
					packets.add(packetToReceive);
				} catch (SocketTimeoutException e) {
					LogUtil.d(TAG, "Receive packet timeout!接收超时（3秒的时间）");
					break;
				}catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			if (!stop) {
				stop = true;
				onReceived(packets);
			}
		}

		void start() {
			thread.start();
		}

		void stop() {
			stop = true;
		}

		boolean isStoped() {
			return stop;
		}
	}
}
