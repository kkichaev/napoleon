package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.TableInfo;


@TableInfo(name="carrier", keyFields="id")
public class Carrier extends DataObject {
	public String id = "";
	public String name = "";
	public List<CarrierItem> items = new ArrayList<CarrierItem>();
}
