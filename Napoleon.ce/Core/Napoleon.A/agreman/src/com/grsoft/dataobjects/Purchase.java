package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="purchase", keyFields="id")
public class Purchase extends DocDataObject{
	public List<PurchaseItem> items = new ArrayList<PurchaseItem>();
}
