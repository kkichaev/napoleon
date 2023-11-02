package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="planroute", keyFields="created")
public class PlanRoute extends DataObject {
	public Date created;
	public List<PlanRouteItem> items = new ArrayList<PlanRouteItem>();
	public int params;
	public Date plan;
}
