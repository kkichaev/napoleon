package com.grsoft.dataobjects;

import com.grsoft.dataobjects.LayoutDefItem;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name = "layoutdef", indexes = "id")
@ServerInfo(name = "LayoutDef")
public class LayoutDef extends DataObject {
    public String id = "";
    public String idOrg = "";
    public String name = "";
    public int pos = 0;
    public List<LayoutDefItem> items = new ArrayList<LayoutDefItem>();
}
