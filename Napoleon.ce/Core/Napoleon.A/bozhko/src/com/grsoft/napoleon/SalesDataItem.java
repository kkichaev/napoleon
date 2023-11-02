package com.grsoft.napoleon;

import java.util.Date;

public class SalesDataItem {
	public SalesDataItem(Date cd, int qty) {
		this.date = cd;
		this.qty = qty;
	}
	
	public Date date;
	public int qty;
}
