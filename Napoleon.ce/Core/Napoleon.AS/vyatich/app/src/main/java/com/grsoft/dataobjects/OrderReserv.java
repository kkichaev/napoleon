package com.grsoft.dataobjects;

import java.util.Date;
import java.util.List;

public class OrderReserv extends DataObject {
	public String id;
	public Date date;
	public String number;
	public Date created;
	
	public List<OrderReservItem> items;
}
