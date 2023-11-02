package com.grsoft.dataobjects.discount;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="StoreDiscount", keyFields = "id")
@ServerInfo(name="StoreDiscount")
public class StoreDiscount extends DataObject {
    public String id = "";
    public String idStore = "";
}
