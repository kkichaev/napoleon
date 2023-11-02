package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

@TableInfo(name = "dlvgroup", keyFields = "id", indexes="whCode")
@ServerInfo(name = "DlvGroup")
public class DlvGroup extends DataObject{
    public String id = "";
    public String name = "";
    public String whCode = "";

    @Override
    public String toString() {
        return name;
    }
}
