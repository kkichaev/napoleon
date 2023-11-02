package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

@TableInfo(name="PurchaseTemplate", keyFields = "id")
@ServerInfo(name="PurchaseTemplate")
public class PurchaseTemplate extends DataObject {
    public String id = "";
    public int pos = 0;
}
