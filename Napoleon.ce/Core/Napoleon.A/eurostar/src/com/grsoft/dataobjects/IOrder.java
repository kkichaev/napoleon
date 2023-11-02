package com.grsoft.dataobjects;

import java.util.Date;


public interface IOrder {
	static final String DEFAULT_TIME_VAL = "00:00";
	int getDlvType();
	void setDlvType(int val);
	String getDlvAddress();
	void setDlvAddress(String val);
	String getZone();
	void setZone(String val);
	String getTimeStart();
	void setTimeStart(String val);
	String getTimeFinish();
	void setTimeFinish(String val);
	String getDlvInfo();
	void setDlvInfo(String val);
	String getCarrier();
	void setCarrier(String val);
	int getPlace();
	void setPlace(int val);
	String getCarAddress();
	void setCarAddress(String val);
	String getPriceType();
	void setPriceType(String val);
	String getWhCode();
	void setWhCode(String val);
	String getAgreement();
	void setAgreement(String val);
	String getStatus();
	void setStatus(String val);
	String getAdrCode();
	void setAdrCode(String val);
	String getRemark();
	void setRemark(String val);
	int getParams();
	void setParams(int val);
	Date getDate();
	void setDate(Date val);
	int getDelay();
	void setDelay(int val);
	boolean isEmty();
	int getSumType();
	void setSumType(int val);
	String getFirmCode();
	void setFirmCode(String val);
	void setDocNumber(String val);
	String getDocNumber();
}
