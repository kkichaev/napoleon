package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price{
	public List<UnitItem> units = new ArrayList<UnitItem>();
	public int piece = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public int kgCost = 0;
	
	public List<PriceQty> whQty = new ArrayList<PriceQty>();
	public int bkgcolor = 0;
}
