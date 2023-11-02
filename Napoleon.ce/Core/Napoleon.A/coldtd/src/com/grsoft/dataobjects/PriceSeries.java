package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="PriceSeries", keyFields="id,ntd,party,prdDate")
@ServerInfo(name="PriceSeries")
public class PriceSeries extends DataObject {
	public String id = "";
	
	public Date prdDate = new Date();

	public String country = "";

	public String countryCode = "";

	public String ntd = "";
	
	public String party = "";

	@Scale(value=Consts.QTY_SCALE)
	public int qty;
}
