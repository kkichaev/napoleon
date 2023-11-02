package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.util.Util;

@TableInfo(name="dover")
@ServerInfo(name="Dover")
public class Dover extends DataObject {
	public String number = "";
	public Date date = null;
	public int firm = 0;
	
	@Override
	public String toString() {
		if(date == null)
			return "";
		return String.format("%s (%s)", number, Util.simpleDateFormat.format(date));
	}
}
