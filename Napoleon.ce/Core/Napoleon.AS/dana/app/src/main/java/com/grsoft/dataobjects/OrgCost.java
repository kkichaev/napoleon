package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name="OrgCost", keyFields = "id")
@ServerInfo(name="OrgCostTypes")
public class OrgCost extends DataObject {
    public String id = "";

    public List<OrgCostItem> items = new ArrayList<>();
}
