package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.types.FieldOrder;

public class ReturnRequestItem extends OrderItem {
	@FieldOrder(order=USER_FIELDS)
	public List<ReturnItemDlv> items = new ArrayList<ReturnItemDlv>();

	@FieldOrder(order = USER_FIELDS + 1)
	public String cause= "";
	
	@FieldOrder(order = USER_FIELDS + 2)
	public Date mfrDate;

	@FieldOrder(order = USER_FIELDS+3)
	public String uid = "";

	@FieldOrder(order = USER_FIELDS+4)
	public String svCause = "";
}
