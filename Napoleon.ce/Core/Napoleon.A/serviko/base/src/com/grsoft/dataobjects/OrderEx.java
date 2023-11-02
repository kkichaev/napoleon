package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	public String whCode = "";
	public int whIndex = -1;
	
	@Scale(value=Consts.SUM_SCALE)
	public int nac = 0;
}
