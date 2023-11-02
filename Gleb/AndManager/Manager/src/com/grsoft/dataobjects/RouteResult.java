package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="agentrouteresult", keyFields="id")
public class RouteResult extends DataObject {
	public String id = "";
	public String html = "";
	public List<RouteResultItem> items = new ArrayList<RouteResultItem>();
}
