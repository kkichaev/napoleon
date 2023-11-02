package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name = "Dogovors", keyFields = "id")
@ServerInfo(name = "Dogovors")
public class Dogovors extends DataObject {
    public String id = "";
    public String orgID = "";
    public String priceID = "";
    public String name = "";

    @Override public String toString() { return name; }
}
