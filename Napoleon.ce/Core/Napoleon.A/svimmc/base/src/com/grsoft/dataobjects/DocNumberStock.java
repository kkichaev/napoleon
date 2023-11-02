package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="DocNumberStock", keyFields="type")
public class DocNumberStock extends DataObject{
	public String type;
	public int number;
}
