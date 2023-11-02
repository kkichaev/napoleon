package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="scrassign", keyFields="id")
@ServerInfo(name="ScrAssign")
public class ScrAssign extends DataObject {
	public String id = "";
	public List<ScrAssignItem> items = new ArrayList<ScrAssignItem>(); 
	
}
