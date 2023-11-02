package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;

public class PriceEx extends Price {
//	public List<PriceWhData> whQty = new ArrayList<PriceWhData>();

	@Scale(Consts.QTY_SCALE)
	public int res;
}
