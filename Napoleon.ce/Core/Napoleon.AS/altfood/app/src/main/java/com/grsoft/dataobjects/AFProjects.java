package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="AFProjects", keyFields = "id")
@ServerInfo(name="AFProjects")
public class AFProjects extends DataObject {
    public String id = "";
    public String name = "";

    @Override public String toString() { return name; }
}
