package com.huyu.lanconfig_hf.net;

import com.huyu.lanconfig_hf.utils.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

public class TCPClient implements INetworkTransmission {

	private final String TAG = "TCPClient";
	private String ip;
	private int port;
	private Socket socket;
	private BufferedInputStream inputStream;
	private BufferedOutputStream outputStream;
	private TCPClientListener listener;
	private byte[] buffer;
	private boolean flag;

	public TCPClient(String ip, int port) {
		super();
		this.ip = ip;
		this.port = port;
		buffer = new byte[1024];
		flag=true;
	}

	@Override
	public void setParameters(String ip, int port) {
		// TODO Auto-generated method stub
		this.ip = ip;
		this.port = port;
	}

	/**
	 * @return the listener
	 */
	public TCPClientListener getListener() {
		return listener;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(TCPClientListener listener) {
		this.listener = listener;
	}

	@Override
	public synchronized boolean open() {
		socket = new Socket();
		new Thread(new Runnable() {
			int time=1;

			@Override
			public void run() {
				try {
					LogUtil.i("TAG ", "透传TCP连接执行1  ip="+ip);
					socket.connect(new InetSocketAddress(ip, port), 5000);
					//					Log.i("TAG ", "透传TCP连接执行2");
					inputStream = new BufferedInputStream(socket.getInputStream());
					outputStream = new BufferedOutputStream(socket.getOutputStream());
					if (listener != null) {
						listener.onConnect(true);
					}

					int length;
					while (flag) {

						try {
							length = inputStream.read(buffer);
							//							Log.i("TAG tcpclient 收数据次数"+(time++), "数据长度："+length+"数据头第四位："+ByteUtils.Byte2HexString(buffer[3]));
							if (length > 0) {
								onReceive(buffer, length);
							}
						} catch (Exception e) {
							break;
						}

						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					listener.onConnect(false);
				}
			}
		}).start();
		return true;
	}

	@Override
	public void close() {
		if (socket != null) {
			try {
				socket.close();
				socket=null;
				flag=false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public synchronized boolean send(String text) {

		if (outputStream != null) {
			try {
				outputStream.write(text.getBytes(), 0, text.getBytes().length);
				outputStream.flush();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		return false;
	}
	//自定义发送字节数组
	public synchronized boolean send(byte[] cmd) {

		if (outputStream != null) {
			try {
				outputStream.write(cmd, 0, cmd.length);
				outputStream.flush();
				LogUtil.i(TAG, "发送成功:"+Arrays.toString(cmd));
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		return false;
	}

	@Override
	public void onReceive(byte[] buffer, int length) {
		if (listener != null) {
			listener.onReceive(buffer, length);
		}
	}

	public interface TCPClientListener {
		public void onConnect(boolean success);
		public void onReceive(byte[] buffer, int length);
	}
}
