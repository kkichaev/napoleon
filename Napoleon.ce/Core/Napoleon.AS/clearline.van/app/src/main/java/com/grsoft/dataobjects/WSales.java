package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="wsales", keyFields="barcode")
public class WSales extends DataObject{
	public String barcode = "";
	public String firm_sender = "";
	public String firm_recipient = "";
	public String org_recipient = "";
	
	public List<WSalesItem> items = new ArrayList<WSalesItem>();
}
