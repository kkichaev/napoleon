package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.Date;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="message", indexes="read", keyFields = "date")
@ServerInfo(name="Message")
public class MessageNew extends DataObject {
	public long rowid = -1;
	public Date date;
	public String message = "";
	public int read = 0;
}
