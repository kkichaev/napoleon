package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceSalesQty extends DataObject implements Comparable<PriceSalesQty> {
	static Date CHECK_DATE = new Date(10 * 24 * 3600 * 1000);

	@FieldOrder(order=0)
	public String date = "";

	@FieldOrder(order=1)
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;

	@FieldOrder(order=2)
	public String number = "";

	@FieldOrder(order=3)
	public int pack = 0;

	@Override
	public int compareTo(PriceSalesQty arg0) {
		return date.compareTo(arg0.date);
	}
}
