package com.grsoft.ads.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="counter", keyFields = "name")
public class Counter extends DataObject {
	public String name = ""; 
}
