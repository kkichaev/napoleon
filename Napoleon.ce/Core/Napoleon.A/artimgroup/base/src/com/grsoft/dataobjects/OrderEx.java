package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends OrderPrint {
	public String iddog = "";
	public String dlvman = "";
	public String discid = "";
	
	public String sklad="";
	public int skladIndex;
	
	@Scale(value = Consts.SUM_SCALE)
	public int discval = 0;
}