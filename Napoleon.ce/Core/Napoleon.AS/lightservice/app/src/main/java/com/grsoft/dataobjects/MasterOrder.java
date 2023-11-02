package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name = "masterorder", keyFields = "id")
@ServerInfo(name = "MasterOrder")
public class MasterOrder extends DataObject{
    public String id;
}
