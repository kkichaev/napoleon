package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="PriceDescr", keyFields = "id")
@ServerInfo(name="PriceDescription")
public class PriceDescription extends DataObject {
    public String id = "";
    public String description = "";
}
