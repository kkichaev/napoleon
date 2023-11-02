package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.grsoft.database.TableInfo;


@TableInfo(name="managerroute", keyFields="date")
public class ManagerRoute extends DataObject {
	public Date date;
	public List<ManagerRouteItem> items = new ArrayList<ManagerRouteItem>();
}
