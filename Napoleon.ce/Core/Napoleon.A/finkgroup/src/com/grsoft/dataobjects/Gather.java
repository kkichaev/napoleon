package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="gather", keyFields="created")
public class Gather extends CreateDocDataObject {
	
	public static int COMPLEETE = 0x80;
	public static int IN_WORK = 0x100;
	
    public List<GatherItem> items;
}
