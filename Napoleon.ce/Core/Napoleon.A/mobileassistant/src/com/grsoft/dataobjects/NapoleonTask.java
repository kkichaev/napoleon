package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="NplTask", keyFields="id")
public class NapoleonTask extends DataObject {
	
	public static final int CREATED = 1; 
	public static final int SENDED = 2; 
	
	public String id = "";
	public Date start;
	public Date end;
	public String task = "";
	public int params = 0;
	
	public String userid = "";
}
