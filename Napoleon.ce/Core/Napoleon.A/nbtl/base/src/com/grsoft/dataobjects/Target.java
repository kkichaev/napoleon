package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="target", keyFields="created", indexes="id,scriptCreated")
public class Target extends CreateDocDataObject {
	public Date scriptCreated;
	public int defid = 0;
}
