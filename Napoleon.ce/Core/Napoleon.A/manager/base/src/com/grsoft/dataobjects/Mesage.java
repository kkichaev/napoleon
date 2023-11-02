package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="Message", keyFields="date")
public class Mesage extends DataObject {
	public Date date;
	public String message;
	public String userid;
}
