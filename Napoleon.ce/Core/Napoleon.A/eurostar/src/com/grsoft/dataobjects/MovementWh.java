package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="movements", keyFields = "created")
public class MovementWh extends CreateDocDataObject {
	public String whSrc = "";
	public String whDest = "";
	public String firma = "";
	
	public List<MovementItem> items = new ArrayList<MovementItem>();
}
