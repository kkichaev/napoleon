package com.grsoft.ads.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.ads.database.OrderItem;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.CreateDocDataObject;

@TableInfo(name="userorders", keyFields = "created")
public class UserOrder extends CreateDocDataObject {
	public List<OrderItem> items = new ArrayList<OrderItem>();
	public String city;
	public String street;
	public String house;
	public String flat;
	public String number;
}
