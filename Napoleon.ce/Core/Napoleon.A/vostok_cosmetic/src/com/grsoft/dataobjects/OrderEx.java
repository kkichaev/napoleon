package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	@Scale(value=Consts.SUM_SCALE)
	public int discount;
	
	public int whIndex;
	
	public List<OrderBonus> bonus;
	public int autoorder = 0;
}
