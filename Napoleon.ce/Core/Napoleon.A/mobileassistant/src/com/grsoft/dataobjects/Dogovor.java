package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@ServerInfo(name="Dogovor")
@TableInfo(name="dogovor", keyFields="id")
public class Dogovor extends DataObject {
	public String id = "";
	public String name = "";
	public List<DogovorItem> items = new ArrayList<DogovorItem>();
}
