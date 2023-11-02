package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class PricePrint extends Price {
	@Scale(Consts.QTY_SCALE)
	public int vanQty;
	
	@Scale(Consts.WEIGHT_SCALE)
	public int brutto;

	public String country;
	public String countryCode;
	
	public String ntd;
	
	public int tax1;

	public String unit;
	public String unitCode;
	
	public String packName;
	public String packCode;
	
	@Scale(Consts.SUM_SCALE)
	public int akciz;
}
