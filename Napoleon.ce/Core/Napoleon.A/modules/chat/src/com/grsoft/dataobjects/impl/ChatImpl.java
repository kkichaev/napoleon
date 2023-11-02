package com.grsoft.dataobjects.impl;

import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import com.grsoft.dataobjects.ChatData;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.util.Util;


public class ChatImpl extends DbObject<ChatData> {
	public void init(){
		data.id = UUID.randomUUID().toString().replace("-", "");
		data.created = Util.getDateTime();
		TimeZone tz = TimeZone.getDefault();
		Date now = new Date();
		data.timeZone = -tz.getOffset(now.getTime()) / (60*1000);
	}
}
