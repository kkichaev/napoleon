package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="wsorder", keyFields="created")
public class WSOrder extends OrderPrint{
	public List<WSOrderLoadedItem> loadedItems = new ArrayList<WSOrderLoadedItem>();
}
