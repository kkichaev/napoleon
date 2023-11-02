package com.grsoft.dataobjects;


import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="ClientType", keyFields = "id")
@ServerInfo(name="ClientTypes")
public class ClientType extends DataObject{
    public String id = "";
    public String name = "";
    public int pos = 0;

    @Override
    public String toString() { return name; }
}
