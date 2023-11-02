package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="notVisited", keyFields="date,id,userid")
public class NotVisited extends DocDataObject {
	public String userid = "";
}
