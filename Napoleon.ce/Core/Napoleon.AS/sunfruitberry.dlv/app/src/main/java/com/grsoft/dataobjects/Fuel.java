package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="fuel", keyFields = "id")
@ServerInfo(name="Fuel")
public class Fuel extends DataObject{
    public String id = "";
    public String name = "";
}
