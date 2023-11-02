package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="Brand", keyFields = "id")
@ServerInfo(name="Brand")
public class Brand extends DataObject {
    public String id = "";
    public String name = "";
    public int pos = 0;
}
