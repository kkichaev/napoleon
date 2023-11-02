package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	public int mask = 1;
	public List<PriceWhData> whQty = new ArrayList<PriceWhData>();
	
	@Scale(value=Consts.QTY_SCALE)
	public int tqty = 0;
	
	public Date tdate = null;
	
	public int disc = 0;
}
