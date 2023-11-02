package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="DelvieryAddress", indexes="id")
@ServerInfo(name="DeliveryAddress")
public class DeliveryAddress extends DataObject {
	public String id = "";
	public String address = "";
	public String kpp = "";
	
	@Override
	public String toString() {
		return address;
	}
}
