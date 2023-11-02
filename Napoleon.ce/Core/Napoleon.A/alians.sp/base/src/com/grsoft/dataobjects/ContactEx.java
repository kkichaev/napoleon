package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class ContactEx extends Contact {
	@FieldOrder(order=3)
	public String bday;
	
	@FieldOrder(order=4)
	public String email = "";
}
