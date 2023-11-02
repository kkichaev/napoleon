package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="ArchiveMessage", keyFields="date,userid")
public class ArchiveMessage extends Message {
	public String userid = "";
}
