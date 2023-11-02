package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="DistrDoc", keyFields="created")
public class DistrDoc extends CreateDocDataObject {
	public String name;	
	public List<DistrItem> items;
}
