package com.grsoft.dataobjects;

import com.grsoft.types.Scale;

public class PriceEx extends Price {
	@Scale(value=10)
	public int salesSpeed;
	
	public String barcode = "";
}
