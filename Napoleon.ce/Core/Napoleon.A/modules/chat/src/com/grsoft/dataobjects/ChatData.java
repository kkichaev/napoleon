package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.database.TableInfo;


@TableInfo(name="chat", keyFields="created,userid")
public class ChatData extends DataObject {
	public Date created;
	public String userid = "";
	public String text = "";
	public String target = "";
	public int params;
	public int timeZone;
	public String id = "";
}
