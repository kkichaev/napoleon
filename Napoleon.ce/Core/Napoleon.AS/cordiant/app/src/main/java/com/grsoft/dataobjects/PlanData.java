package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.PlanDataItem;

import java.util.ArrayList;

@TableInfo(name = "plandata", keyFields = "id")
@ServerInfo(name = "PlanData")
public class PlanData extends DataObject{
    public String id = "";

    public java.util.List<PlanDataItem> items = new ArrayList<>();
}
