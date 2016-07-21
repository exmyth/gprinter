package com.printer.util;

import java.util.Vector;

import com.gprinterio.GpCom;
import com.gprinterio.GpDevice;
import com.printer.TscCommand;
import com.printer.TscCommand.BARCODETYPE;
import com.printer.TscCommand.DENSITY;
import com.printer.TscCommand.DIRECTION;
import com.printer.TscCommand.FONTMUL;
import com.printer.TscCommand.FONTTYPE;
import com.printer.TscCommand.READABEL;
import com.printer.TscCommand.ROTATION;
import com.printer.TscCommand.SPEED;

public class GpDeviceManager {

	private static GpDevice device;
	//打印机ip固定值
	private String ip = "192.168.199.122";
	private int port = 9100;
	private static GpDeviceManager instance;
	
	private GpDeviceManager() {
		super();
	}
	
	public static GpDeviceManager getInstance(){
		if(instance == null){
			synchronized (GpDeviceManager.class) {
				if(instance == null){
					instance = new GpDeviceManager();
				}
			}
		}
		return instance;
	}
	
	public GpDevice getDevice() {
		if(null == device){
			device = new GpDevice();
		}
        return device;
    }
	public void setEthernetPort(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	public GpCom.ERROR_CODE openPort() {
		return getDevice().openEthernetPort(ip, port);
	}
	public GpCom.ERROR_CODE closePort(){
		if(device != null){
			return device.closePort();
		}
		else{
			return GpCom.ERROR_CODE.FAILED;
		}
	}
	
	/**
	 * 打印文字+条形码
	 * @param text
	 * @param codes
	 * @return
	 */
	public GpCom.ERROR_CODE sendTextBarcodeImmediately(String text, String[] codes) {
		if(codes == null || codes.length == 0){
			return GpCom.ERROR_CODE.FAILED;
		}
		TscCommand tsc = buildTscCommand(codes);
		
		tsc.addCls();// 清除打印缓冲区
		tsc.addText(0, 0, FONTTYPE.FONT_5, ROTATION.ROTATION_0,FONTMUL.MUL_1, FONTMUL.MUL_1, text);// 绘制文字
		tsc.addPrint(1, 1);// 加入打印标签命令
		
		for(int i = 0; i < codes.length; i++){
			tsc.addCls();//清除打印缓冲区 
			tsc.add1DBarcode(0, 0, BARCODETYPE.CODE128, 120, READABEL.EANBEL, ROTATION.ROTATION_0, codes[i]);//绘制一维条码 
			tsc.addPrint(1,1);//加入打印标签命令 
		}
		Vector<Byte> Command = new Vector<Byte>(4096, 1024); 
		Command = tsc.getCommand();//获取上面编辑的打印命令 
		return getDevice().sendDataImmediately(Command);//发送命令
	}
	
	public GpCom.ERROR_CODE sendBarcodeImmediately(String[] codes) {
		if(codes == null || codes.length == 0){
			return GpCom.ERROR_CODE.FAILED;
		}
		
		TscCommand tsc = buildTscCommand(codes);
		
		for(int i = 0; i < codes.length; i++){
			tsc.addCls();//清除打印缓冲区 
			tsc.add1DBarcode(0, 0, BARCODETYPE.CODE128, 120, READABEL.EANBEL, ROTATION.ROTATION_0, codes[i]);//绘制一维条码 
			tsc.addPrint(1,1);//加入打印标签命令 
		}
		Vector<Byte> Command = new Vector<Byte>(4096, 1024); 
		Command = tsc.getCommand();//获取上面编辑的打印命令 
		return getDevice().sendDataImmediately(Command);//发送命令
	}
	
	private TscCommand buildTscCommand(String[] codes) {
		int x = 40;
		if(codes[0].length() >= 17){
			x = 15;
		}
		TscCommand tsc = new TscCommand(40,30,3);//设置标签尺寸宽度、高度、间隙 
		tsc.addReference(x, 50); //设置原点坐标 
		tsc.addSpeed(SPEED.SPEED1DIV5);//设置打印速度 
		tsc.addDensity(DENSITY.DNESITY15);//设置打印浓度 
		tsc.addDirection(DIRECTION.BACKWARD);//设置打印方向 
		tsc.addSound(2, 100);
		return tsc;
	}
}
