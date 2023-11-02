package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	public String partExpr = "";
	public int canMinus = 0;
	public List<PriceQtyItem> whQty	 = new ArrayList<PriceQtyItem>();
	
	@Scale(value=Consts.QTY_SCALE)
	public int quant = 0;
}
