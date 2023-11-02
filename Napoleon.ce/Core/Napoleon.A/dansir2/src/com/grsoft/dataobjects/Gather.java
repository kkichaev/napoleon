package com.grsoft.dataobjects;

import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="gather", keyFields="id")
public class Gather extends DataObject {
	
	public static int COMPLEETE = 0x80;
	public static int IN_WORK = 0x100;
	
	public String id = "";
	public String name = "";
	public String address = "";
	public String remark = "";

	public Date date;
    public int krug;
    public List<GatherItem> items;
     
    public int params;
}
