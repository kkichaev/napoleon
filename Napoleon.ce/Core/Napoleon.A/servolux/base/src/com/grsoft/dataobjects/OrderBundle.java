package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="OrderBundle", keyFields="created")
public class OrderBundle extends CreateDocDataObject {
	public List<OrderBundleItem> items = new ArrayList<OrderBundleItem>();
	
	public long sum = 0;
}
