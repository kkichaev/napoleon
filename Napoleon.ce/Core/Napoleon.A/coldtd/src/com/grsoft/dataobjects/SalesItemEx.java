package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;

public class SalesItemEx extends SalesItem {
	@FieldOrder(order=USER_FIELDS)
	public Date prdDate = new Date();

	@FieldOrder(order=USER_FIELDS + 1)
	public String country = "";

	@FieldOrder(order=USER_FIELDS + 2)
	public String countryCode = "";

	@FieldOrder(order=USER_FIELDS + 3)
	public String ntd = "";

	@FieldOrder(order=USER_FIELDS + 4)
	public String party = "";
}
