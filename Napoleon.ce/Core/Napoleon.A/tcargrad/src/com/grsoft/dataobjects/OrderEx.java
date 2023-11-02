package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	public List<OrderPropData> props;
	public String taxType;
	public String dlvType;
	public String payType;
	public String shipType;
	public String ido;
	public String account;
	
	@Scale(value=Consts.SUM_SCALE)
	public int discount;
}
