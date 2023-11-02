package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="deliveryroute", keyFields="id")
@ServerInfo(name="DeliveryRoute")
public class DeliveryRoute extends DataObject {
	public String id = "";
	public String name = "";
	
	@Override
	public String toString() {
		return name;
	}
}
