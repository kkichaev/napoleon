package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class SalesEx extends Sales {
	public int isBlack = 0;
	public Date buddy;
	public String orgName = "";
	public String orgAddress = "";
	
	@Scale(value=Consts.SUM_SCALE)
	public int incass = 0;
}
