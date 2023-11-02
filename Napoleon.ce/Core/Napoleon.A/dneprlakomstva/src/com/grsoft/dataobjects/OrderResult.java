package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderResult extends DataObject {
	public String created;
	public String orgid;
	public String ordstatus;
	public String ordnumber;
	public String doctype;

	public String dlvstatus;
	public String dlvnumber;
	public Date dlvdate = new Date();
	public Date dlvpaydate = new Date();
	
	@Scale(value=Consts.SUM_SCALE)
	public long balance;
	
	public String message;
	
	public List<OrderResultItem> items = new ArrayList<OrderResultItem>();
}
