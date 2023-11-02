package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="topsel", keyFields="userid,period", indexes="period" )
@ServerInfo(name="TopSel")
public class TopSel extends DataObject {
	public String userid = "";
	public int period;
	
	public List<TopSelItem> items = new ArrayList<TopSelItem>();
}
