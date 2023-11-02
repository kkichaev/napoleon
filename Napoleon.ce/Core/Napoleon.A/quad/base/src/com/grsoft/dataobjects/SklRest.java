package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="SklRest", keyFields="id")
public class SklRest extends DataObject {
	public String id = "";
	
	public List<SklRestItem> items = new ArrayList<SklRestItem>();
}
