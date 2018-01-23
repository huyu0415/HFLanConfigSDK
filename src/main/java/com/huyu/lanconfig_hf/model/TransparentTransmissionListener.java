package com.huyu.lanconfig_hf.model;

public interface TransparentTransmissionListener {

	public void onOpen(boolean success);
//	public void onClose(boolean success);
	public void onReceive(byte[] data, int length);
}
