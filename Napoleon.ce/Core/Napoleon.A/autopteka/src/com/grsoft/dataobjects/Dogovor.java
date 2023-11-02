package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.FieldOrder;

public class Dogovor extends DataObject {
	
	@FieldOrder(order=0)
	public String number = "";
	
	@FieldOrder(order=1)
	public String name = "";
	
	@FieldOrder(order=2)
	public Date from;
	
	@FieldOrder(order=3)
	public Date till;
	
//	@FieldOrder(order=4)
//	public String costType = "";

}
