package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	public String idStore="";
	
	@Scale(value = Consts.SUM_SCALE)
	public int agentSum = 0;
	
	public Date dlvDate = new Date();
	public int idxTypeImpl = 0;
	
	public String mid = "";
}
