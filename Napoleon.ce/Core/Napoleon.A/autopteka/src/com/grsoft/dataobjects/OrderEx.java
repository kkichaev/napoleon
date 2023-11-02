package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {

	public Date supplDate;
	
	@Scale(value=Consts.SUM_SCALE)
	public int paySum;
	
	public String dogNum = "";
//	
//	public String sumTypeID = "";
}
