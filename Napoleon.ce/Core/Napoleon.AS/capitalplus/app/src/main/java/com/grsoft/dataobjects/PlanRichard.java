package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="Plans", keyFields = "title")
@ServerInfo(name="PlanRichard")
public class PlanRichard extends DataObject {
    public String title = "";
    public String text = "";
}
