package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="layoutdef", indexes="id")
@ServerInfo(name="LayoutDef")
public class LayoutDef extends DataObject {
	public String id = "";
	public String idOrg = "";
	public String name = "";
	public int pos = 0;
	public List<LayoutDefItem> items = new ArrayList<LayoutDefItem>();
}
