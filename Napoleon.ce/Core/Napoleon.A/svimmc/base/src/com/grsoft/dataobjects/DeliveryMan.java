package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="deliveryman", keyFields="id")
public class DeliveryMan extends DataObject {
	public String id = "";
	public String name = "";
}
