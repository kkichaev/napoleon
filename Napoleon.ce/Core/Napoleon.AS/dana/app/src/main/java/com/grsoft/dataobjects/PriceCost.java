package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name="PriceCost", keyFields = "idc")
@ServerInfo(name="PriceCost")
public class PriceCost extends DataObject {
    public String idc = "";
    public List<PriceCostItem> items = new ArrayList<>();
}
