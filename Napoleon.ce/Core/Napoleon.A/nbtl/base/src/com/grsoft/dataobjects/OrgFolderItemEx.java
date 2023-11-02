package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.FieldOrder;

public class OrgFolderItemEx extends OrgFolderItem {
	@FieldOrder(order= 100)
	public List<RouteScriptItem> scripts = new ArrayList<RouteScriptItem>();
}
