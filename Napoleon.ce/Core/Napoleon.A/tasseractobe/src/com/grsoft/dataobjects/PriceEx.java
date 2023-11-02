package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	public List<PriceWhData> whQty = new ArrayList<PriceWhData>();
	
	@Scale(value=Consts.QTY_SCALE)
	public int tare = 0;
}
