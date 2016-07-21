package com.printer.util;

import com.gprinterio.GpCom.ERROR_CODE;

public class Client {
	public static void main(String[] args) {
		String[] codes = new String[]{"1234"};
		GpDeviceManager instance = GpDeviceManager.getInstance();
		instance.setEthernetPort("192.168.199.123",9100);
		instance.openPort();
		ERROR_CODE err = instance.sendBarcodeImmediately(codes);
		instance.closePort();
	}
}
