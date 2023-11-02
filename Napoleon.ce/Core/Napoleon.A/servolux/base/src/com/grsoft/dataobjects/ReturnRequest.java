package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="ReturnRequest", keyFields="created")
public class ReturnRequest extends Order {
	public Date visitDoc = new Date(0);
//	public int accepted = 0;
//	public Date svChanged = new Date();
	
	public Date getExpiredDate() {
		return new Date(date.getTime() + 2 * 24 * 3600 * 1000);
	}
}
