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
	
	public int taxType = 0;
	
	public String orderBaseNumber = "";
	public Date orderBaseDate = new Date();
	
	public int isExpired = 0;
	public int isExchange = 0;
	
	public String barcode = "";
	
	public int totalPageUPD = 0;
	public int totalPageSF = 0;
	public int totalPageNakl = 0;
}
