package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="ServikoActionItems", keyFields="id")
@ServerInfo(name="ServikoActionItems")
public class ServikoActionItems extends DataObject {
	public String id = "";
	public List<ServikoActionItem> items = new ArrayList<ServikoActionItem>();
}
