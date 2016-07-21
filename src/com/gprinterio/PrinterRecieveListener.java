package com.gprinterio;

import java.util.Vector;

public abstract interface PrinterRecieveListener {
	public abstract GpCom.ERROR_CODE ReceiveData(Vector<Byte> paramVector);
}
