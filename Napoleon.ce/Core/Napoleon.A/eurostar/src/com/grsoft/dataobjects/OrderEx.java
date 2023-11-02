package com.grsoft.dataobjects;

import java.util.Date;


public class OrderEx extends OrderPrint implements IOrder {
	public String docnumber = "";
	
	//-------------- IOrder firelds -----------------------------
	private static final String DEFAULT_TIME_VAL = "00:00";
	public int dlvtype;
	public String dlvaddress = "";
	public String zone = "";
	public String timeStart = DEFAULT_TIME_VAL;
	public String timeFinish = DEFAULT_TIME_VAL;
	public String dlvinfo = "";
	public String carrier = "";
	public int place = 0;
	public String caraddress = "";
	public String priceType = "";
	public String whCode = "";
	public String agreement = "";
	public String status = "";
	//-------------- IOrder firelds -----------------------------
	
	@Override
	public int getDlvType() {return dlvtype; }
	@Override
	public void setDlvType(int val) { dlvtype = val; }
	@Override
	public String getDlvAddress() {	return dlvaddress;	}
	@Override
	public void setDlvAddress(String val) { dlvaddress = val; }
	@Override
	public String getZone() { return zone; }
	@Override
	public void setZone(String val) { zone = val; }
	@Override
	public String getTimeStart() {	return timeStart; }
	@Override
	public void setTimeStart(String val) { timeStart = val; }
	@Override
	public String getTimeFinish() {	return timeFinish; }
	@Override
	public void setTimeFinish(String val) { timeFinish = val; }
	@Override
	public String getDlvInfo() { return dlvinfo; }
	@Override
	public void setDlvInfo(String val) { dlvinfo = val;}
	@Override
	public String getCarrier() { return carrier; }
	@Override
	public void setCarrier(String val) {carrier = val; }
	@Override
	public int getPlace() {	return place; }
	@Override
	public void setPlace(int val) { place = val; }
	@Override
	public String getCarAddress() {	return caraddress; }
	@Override
	public void setCarAddress(String val) { caraddress = val;}
	@Override
	public String getPriceType() { return priceType; }
	@Override
	public void setPriceType(String val) { priceType = val;}
	@Override
	public String getWhCode() { return whCode; }
	@Override
	public void setWhCode(String val) { whCode = val;}
	@Override
	public String getAgreement() { return agreement; }
	@Override
	public void setAgreement(String val) { agreement = val; }
	@Override
	public String getStatus() { return status;	}
	@Override
	public void setStatus(String val) { status = val; }
	@Override
	public String getAdrCode() { return adrCode;	}
	@Override
	public void setAdrCode(String val) { adrCode = val;	}
	@Override
	public String getRemark() {	return remark; }
	@Override
	public void setRemark(String val) { remark = val; }
	@Override
	public int getParams() { return params;	}
	@Override
	public void setParams(int val) { params = val; }
	@Override
	public Date getDate() { return date; }
	@Override
	public void setDate(Date val) { val = date; }
	@Override
	public int getDelay() { return delay; }
	@Override
	public void setDelay(int val) { delay = val; }
	@Override
	public boolean isEmty() { return items == null || items.size() == 0; }
	@Override
	public int getSumType() { return sumType; }
	@Override
	public void setSumType(int val) { sumType = val; }
	@Override
	public String getFirmCode() { return firmCode;	}
	@Override
	public void setFirmCode(String val) { firmCode = val; }
	@Override
	public void setDocNumber(String val) { docnumber = val;}
	@Override
	public String getDocNumber() { return docnumber; }
	
}
