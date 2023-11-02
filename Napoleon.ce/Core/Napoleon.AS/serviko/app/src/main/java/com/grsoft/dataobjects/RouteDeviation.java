package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

import java.util.Date;

@TableInfo(name="RouteDeviation", keyFields = "date,type")
@ServerInfo(name="RouteDeviation")
public class RouteDeviation extends DataObject{
    public static final int CREATED_FAR_FROM_ORG = 1;
    public static final int OUT_OF_ROUTE_ORDER = 2;
    public static final int SHORT_VISIT_TIME = 3;
    public static final int DONT_WORK = 4;

    public Date date = new Date();
    public String id = "";
    public String orgName = "";
    public int type = 0;

    public int exported = 0;
}
