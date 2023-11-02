package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

@TableInfo(name="goodprojects", keyFields = "id")
@ServerInfo(name="GoodsProjects")
public class GoodProject extends DataObject {
    public String id = "";
    public String idOrg = "";
    public String name = "";
    public String base = "";

    @Override public String toString() { return name; }
}
