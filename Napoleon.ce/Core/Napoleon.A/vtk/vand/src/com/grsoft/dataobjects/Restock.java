package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="Restocking", keyFields="created")
public class Restock extends CreateDocDataObject {
	public List<RestockItem> items = new ArrayList<RestockItem>();
}
