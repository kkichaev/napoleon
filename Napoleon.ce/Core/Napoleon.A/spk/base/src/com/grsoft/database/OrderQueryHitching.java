package com.grsoft.database;

import java.util.Date;

import com.grsoft.network.ObjectExportListener;

public class OrderQueryHitching extends DocQueryHitching
implements ObjectExportListener{
	public OrderQueryHitching(Date date) {
		super("OrderQuery", date);
	}
}
