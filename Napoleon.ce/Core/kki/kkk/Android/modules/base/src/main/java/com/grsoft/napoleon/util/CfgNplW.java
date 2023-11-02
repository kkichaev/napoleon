package com.grsoft.napoleon.util;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Environment;

import java.util.Calendar;
import java.util.Date;

import com.grsoft.napoleon.Features;
import com.grsoft.util.Consts;

public class CfgNplW extends Config
{
	private static final long serialVersionUID = 1L;

	public static final int NO_ONLINE_IP = 2;
	 
	public boolean checkPrice = Features.CONFIG_CHECK_PRICE_QTY;
	public boolean vibration = false;
	@DefaultValue(value="true")
	public boolean allowRotateScreen = true;
	public boolean useAutoFocus = true;
	/**
	 * фоновая передача
	 */
	public boolean dataSendInBackground = Features.SEND_IN_BACKGROUND;
	public int gpsSendInterval = 30;
	public int priceClmn2Type = 1;
	public int priceClmn3Type = 3;
	public boolean isAutostart = false;
	public boolean isService = false;
	public boolean isComplexSalesHistory = false;
	public boolean isPackView = false;
	public boolean idInPriceList = false;
	public String printSource = "HP";
	//public boolean scriptOff = false;
	public int onLineIP = 0; 
	/**
	 * Ожидание координат, сек
	 */
	public int waitGpsCoordOnRequest = 60;
	public int day_to_del_visit = 0;
	public boolean variableOrgHeight = false;
	public boolean isNewPriceNavType = true; 
	public String presentpath = Environment.getExternalStorageDirectory().getPath() +
			"/Napoleon/prezent";
	
	@DefaultValue(value="5000000")
	public long max_packet_len = 5000000L;
	
	@DefaultValue(value="30")
	public int updatePriceTime = 30;
	public boolean useUpdatePrice = false;
	
	/*Фотографировать внутренним приложением*/
	public boolean androidPhoto = false;
	
	@DefaultValue(value="false")
	public boolean keepAwayInOrder = false;
	
	@DefaultValue(value="0")
	public int priceLevel = 0;
	
	/**
	 * не задаю в настройках.
	 */
	@DefaultValue(value="true")
	public boolean saveReportsToCard = true;
	
	/***
	 * Время в милисекундах когда 
	 * не будет спашивать GPS для документа в одной и той же 
	 * организации
	 */
	public static final int DEF_VAL_FOR_TIME_GPS_IN_ORG = 5 * Consts.ONE_SECOND * Consts.SEC_PER_MIN ; 
	
	/**
	 * Помнить координаты, мсек
	 */
	@DefaultValue(value="300000")
	public int gps_valid_in_org = DEF_VAL_FOR_TIME_GPS_IN_ORG;
	
	public void setOrientation(Activity a) {
		a.setRequestedOrientation( (allowRotateScreen) ? 
				ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED : 
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	public void resetToDefault(){
		day_to_del_visit = 30;
		gpsDistance = 100;
		gpsFrequience = Consts.ONE_SECOND * 60;
		gpsSendInterval = 30;
		priceClmn2Type = 1;
		priceClmn3Type = 3;
		printSource = "HP";
		waitGpsCoordOnRequest = 60;
		monthsToRecreate = 1;
		max_packet_len = 5000000L;
		gps_valid_in_org = DEF_VAL_FOR_TIME_GPS_IN_ORG;
		updatePriceTime=30;
		allowRotateScreen=true;
		keepAwayInOrder = false;
		checkPrice = Features.CONFIG_CHECK_PRICE_QTY;
	}
	
	public Date getRestoreDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -monthsToRecreate);
		calendar.add(Calendar.DATE, -daysToRecreate);
		return calendar.getTime();
	}
}
