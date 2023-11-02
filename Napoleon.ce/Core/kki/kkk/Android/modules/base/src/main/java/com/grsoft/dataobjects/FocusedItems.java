package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.TableInfo;

@TableInfo(name="FocusedItems",keyFields="id")
public class FocusedItems extends DataObject {
	public String id = "";

	public List<FocusedItemsItem> items = new ArrayList<FocusedItemsItem>();

}
