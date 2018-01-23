package com.huyu.lanconfig_hf.net;

import android.util.Log;

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
import java.util.Arrays;

public class UdpUnicast implements INetworkTransmission {

	private static final String TAG = "UdpUnicast";
	private static final int BUFFER_SIZE = 2048;
	
	private String ip;
	private int port = Constants.UDP_PORT;
	private DatagramSocket socket;
	private DatagramPacket packetToSend;
	private InetAddress inetAddress;
	private ReceiveData receiveData;
	private UdpUnicastListener listener;
	private byte[] buffer = new byte[BUFFER_SIZE];
	
	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * @param listener the listener to set
	 */
	public void setListener(UdpUnicastListener listener) {
		this.listener = listener;
	}

	/**
	 * @return the listener
	 */
	public UdpUnicastListener getListener() {
		return listener;
	}

	public UdpUnicast(String ip, int port) {
		super();
		this.ip = ip;
		this.port = port;
	}

	public UdpUnicast() {
		super();
	}

	/**
	 * Open udp socket
	 */
	public synchronized boolean open() {
		
		try {
			inetAddress = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
		
		try {
//			socket = new DatagramSocket(port);
			if(socket==null ){
				socket = new DatagramSocket(null);
				socket.setReuseAddress(true);
				socket.bind(new InetSocketAddress(port));
			}
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		}
		
		//receive response
		receiveData = new ReceiveData();
		receiveData.start();
		return true;
	}
	
	/**
	 * Close udp socket
	 */
	public synchronized void close() {
		stopReceive();
		if (socket != null) {
			socket.close();
			socket=null;
		}
	}
	
	/**
	 * send message
	 * @param text
	 * 			the message to broadcast
	 */
	public synchronized boolean send(String text) {

		LogUtil.d(TAG, "send:" + text);
		
		if (socket == null) {
			return false;
		}
		
		if(socket.isClosed()){
			open();
		}
		
		if (text == null) {
			return true;
		}

		packetToSend = new DatagramPacket(
				text.getBytes(), text.getBytes().length, inetAddress, port);
		
		//send data
		try {
			
			socket.send(packetToSend);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
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
	
	public interface UdpUnicastListener {
		public void onReceived(byte[] data, int length);
	}
	
	private class ReceiveData implements Runnable {
		
		private boolean stop;
		private Thread thread;

		private ReceiveData() {
			thread = new Thread(this);
		}
		
		@Override
		public void run() {
			
			while (!stop) {
				try {

					DatagramPacket packetToReceive = new DatagramPacket(buffer, BUFFER_SIZE);
					socket.receive(packetToReceive);
					onReceive(buffer, packetToReceive.getLength());
				} catch (SocketTimeoutException e) {
					LogUtil.w(TAG, "Receive packet timeout!");
				}catch (IOException e1) {
					LogUtil.w(TAG, "Socket is closed!");
				}
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

	@Override
	public void setParameters(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	@Override
	public void onReceive(byte[] buffer, int length) {
		Log.d(TAG, new String(buffer, 0, length));
		if (listener != null) {
			listener.onReceived(buffer, length);
		}
	}

	@Override
	public boolean send(byte[] cmd) {

		LogUtil.d(TAG, "send:" + Arrays.toString(cmd));
		
		if (socket == null) {
			return false;
		}
		
		if(socket.isClosed()){
			open();
		}
		
		if (cmd == null) {
			return true;
		}

		packetToSend = new DatagramPacket(
				cmd, cmd.length, inetAddress, port);
		
		//send data
		try {
			
			socket.send(packetToSend);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
