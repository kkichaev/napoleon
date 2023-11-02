package com.grsoft.database;

import java.util.Date;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="textlog", keyFields="userid,date")
@ServerInfo(name="TextLog")
public class TextLog extends DataObject {
	public String userid = "";
	public Date date = new Date();
	public String text = "";
}
