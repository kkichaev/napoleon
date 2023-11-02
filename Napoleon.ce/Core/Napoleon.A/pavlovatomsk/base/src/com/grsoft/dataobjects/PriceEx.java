package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	public int boxed;
	
	@Scale(value=Consts.QTY_SCALE)
	public int limit = 0;
	
	public List<PriceWhData> whQty = new ArrayList<PriceWhData>();
}
