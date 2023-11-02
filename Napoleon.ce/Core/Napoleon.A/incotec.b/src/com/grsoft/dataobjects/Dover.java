package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;
import com.grsoft.util.Util;

@TableInfo(name="dover")
public class Dover extends DataObject {
	public String number = "";
	public Date date;
	
	@Override
	public String toString() {
		return String.format("%s (%s)", number, Util.simpleDateFormat.format(date));
	}
}
