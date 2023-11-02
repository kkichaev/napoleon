package com.grsoft.dataobjects;

import com.grsoft.types.Scale;

public class OrderEx extends Order {
	@Scale(value=1)
	public String payType;
}
