package com.grsoft.dataobjects;

import com.grsoft.types.Scale;

public class OrderEx extends Order {
	@Scale(value=10)
	public int discount;

}
