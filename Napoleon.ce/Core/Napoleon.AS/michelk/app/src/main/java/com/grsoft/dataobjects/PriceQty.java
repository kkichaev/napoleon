package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name="PriceQty", keyFields = "id")
@ServerInfo(name="PriceSklad")
public class PriceQty extends DataObject {
    public String id = "";

    public List<PQItem> sklads = new ArrayList<>();
}
