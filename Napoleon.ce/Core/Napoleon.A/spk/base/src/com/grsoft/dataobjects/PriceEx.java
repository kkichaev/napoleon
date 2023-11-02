package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class PriceEx extends Price {
	public List<UnitItem> units = new ArrayList<UnitItem>();
	public Date expired;
	
	@Scale(value=1000)
	public int avgWeight;
	
	/***
	 * Коэффициент для автозаказа
	 */
	@Scale(value=Consts.SUM_SCALE)
	public int coeff = 0;
}
