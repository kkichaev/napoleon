package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name="PriceCost", keyFields = "priceID")
@ServerInfo(name="PriceCost")
public class PriceCost extends DataObject {
    public String priceID = "";
    public List<SenegCostItem> items = new ArrayList<SenegCostItem>();
}

