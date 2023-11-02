package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.FieldOrder;

@TableInfo(name="routeitem", keyFields="itemid")
@ServerInfo(name="RouteItem")
public class RouteItem extends DataObject{
	public String route = "";
	public String id = "";
	public String itemid = "";
	public long pos = 0;
	public String remark = "";
	public String title = "";
	@FieldOrder(order=4)
	public List<ItemDef> docs = new ArrayList<ItemDef>();
	
}
