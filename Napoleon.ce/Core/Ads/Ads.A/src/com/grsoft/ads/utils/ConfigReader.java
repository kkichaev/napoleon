package com.grsoft.ads.utils;

import android.content.Context;
import android.content.SharedPreferences.Editor;

import com.grsoft.ads.Setting;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.util.Size;

public class ConfigReader extends CfgNpl {

	private static final long serialVersionUID = 1L;
	
	private Context context;
	
	public ConfigReader(Context context){
		this.context = context;
	
		loadConfig();
	}
	
	public void loadConfig() {
		address = getAddress();
		address2 = getAddress2();
		port = getPort();
		port2 = getPort2();
		
		Size sz = getCameraSize(); 
		cameraWidth = sz.width;
		cameraHeight = sz.hight;
		
		login = getLogin();
		passw = getPassword();
		
		gpsDistance = getGpsDistance();
		gpsFrequience = getGpsFrequency();
	}
	
	public String getAddress() {
		return context.getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
			.getString(Setting.SERV_ADR_1, "");
	}

	public String getAddress2() {
		return context.getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
				.getString(Setting.SERV_ADR_2, "");
	}

	public boolean getAllowRotateScreen() {
		return false;
	}

	public Size getCameraSize() {
		int w = context.getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, 
				Context.MODE_PRIVATE).getInt(Setting.CAMERA_WIDTH, 0);
		int h = context.getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, 
				Context.MODE_PRIVATE).getInt(Setting.CAMERA_HEIGHT, 0);
		
		Size result = new Size(w, h);
		
		return result;
	}

//	public boolean getCheckPrice() {
//		// TODO Auto-generated method stub
//		return false;
//	}

	public int getDataSendInterval() {
		return Integer.parseInt(context.getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
				.getString(Setting.INTERVAL, "15"));
	}

	public int getGpsDistance() {
		return Integer.parseInt(context.getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
				.getString(Setting.DISTANCE, "0"));
	}

	public int getGpsFrequency() {
		return Integer.parseInt(context.getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
				.getString(Setting.FREQUENCE, "0")) * 1000;
	}

	public String getLogin() {
		return context.getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
			.getString(Setting.LOGIN, "");
	}

	public String getPassword() {
		return context.getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
			.getString(Setting.PASSW, "");
	}

	public int getPort() {
		return Integer.parseInt(context.getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
			.getString(Setting.PORT, "0"));
	}

	public int getPort2() {
		return Integer.parseInt(context.getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
				.getString(Setting.PORT, "0"));
	}

//	public int getPriceColumn2Type() {
//		// TODO Auto-generated method stub
//		return 0;
//	}

//	public int getPriceColumn3Type() {
//		// TODO Auto-generated method stub
//		return 0;
//	}

//	public boolean getVibration() {
//		// TODO Auto-generated method stub
//		return false;
//	}

//	public boolean isAutostart() {
//		// TODO Auto-generated method stub
//		return false;
//	}

//	public boolean isComplexSalesHistory() {
//		// TODO Auto-generated method stub
//		return false;
//	}

	public boolean isDataSendInBackground() {
		return context.getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
				.getBoolean(Setting.DATA_SEND_IN_BACKGOUND, false);
	}

//	public boolean isService() {
//		// TODO Auto-generated method stub
//		return false;
//	}

//	public boolean isUseAutoFocus() {
//		// TODO Auto-generated method stub
//		return false;
//	}

//	public void setAddress(String address) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setAddress2(String address) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setAllowRotateScreen(boolean allow) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setAutostart(boolean value) {
//		// TODO Auto-generated method stub
//
//	}

	public void setCameraSize(Size cameraSize) {
		if (cameraSize != null){
			Editor editor = context.getSharedPreferences(Setting.SHARED_PREFERENCES_NAME, 
					Context.MODE_PRIVATE).edit();
			editor.putInt(Setting.CAMERA_WIDTH, cameraSize.width);
			editor.putInt(Setting.CAMERA_HEIGHT, cameraSize.hight);
			editor.commit();
		}

	}

//	public void setCheckPrice(boolean checkPrice) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setComplexSalesHistory(boolean value) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setDataSendInBackground(boolean allow) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setDataSendInterval(int value) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setGpsDistance(int dist) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setGpsFrequency(int time) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setLogin(String login) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setPassword(String passw) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setPort(int port) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setPort2(int port) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setPriceColumn2Type(int value) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setPriceColumn3Type(int value) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setService(boolean value) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setUseAutoFocus(boolean useAutoFocus) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setVibration(boolean enabled) {
//		// TODO Auto-generated method stub
//
//	}

//	public void setPackView(boolean value) {
//		// TODO Auto-generated method stub
//		
//	}

//	public boolean isPackView() {
//		// TODO Auto-generated method stub
//		return false;
//	}

//	public void setIDInPriceList(boolean value) {
//		// TODO Auto-generated method stub
//		
//	}

//	public boolean isIDInPriceList() {
//		// TODO Auto-generated method stub
//		return false;
//	}

//	public void setPrintSource(String value) {
//		// TODO Auto-generated method stub
//		
//	}

//	public String getPrintSource() {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	public boolean isScriptOff() {
//		// TODO Auto-generated method stub
//		return false;
//	}

//	public void setScriptOff(boolean value) {
//		// TODO Auto-generated method stub
//		
//	}

//	public void setProperty(String name, Object value) {
//		// TODO Auto-generated method stub
//		
//	}

	public Object getProperty(String name) {
		if (name.equals("manage_gps"))
			return true;
		return null;
	}
}
