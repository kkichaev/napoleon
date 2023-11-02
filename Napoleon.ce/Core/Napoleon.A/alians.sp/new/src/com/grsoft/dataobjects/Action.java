package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.TableInfo;


@TableInfo(name="action", keyFields="id")
public class Action extends DataObject{
	public String id = "";
	public String text = "";
	
	public List<ActionItem> items = new ArrayList<ActionItem>();
	
}
