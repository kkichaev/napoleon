package com.grsoft.dataobjects;

import com.grsoft.types.Scale;

public class DeliveryEx extends Delivery {
	@Scale(value=100)
	public int out_deb;
	
	public int color;
}
