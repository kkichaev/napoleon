package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="DistribGroup", keyFields="id")
public class DistribGroup extends DataObject {
	public String id = "";
	public String name = "";
	public int pos = 0;
	
	public List<DistrGroupItem> items = new ArrayList<DistrGroupItem>();
}
