package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class OrderEx extends Order {
	public String mfr = "";
	@Scale(value=Consts.SUM_SCALE)
	public int discount = 0;
	public int plan = 0;
	public int sidx = 0;
	public String ship = "";
	public int pidx = 0;
	public String pay = "";
	public String city = "";
	
}
