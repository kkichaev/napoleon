package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class ReturnEx extends Return {
	public String iddog = "";
	public String dlvman = "";
	public String discid = "";
	
	@Scale(value = Consts.SUM_SCALE)
	public int discval = 0;
	
	public String ordnumber = "";
}
