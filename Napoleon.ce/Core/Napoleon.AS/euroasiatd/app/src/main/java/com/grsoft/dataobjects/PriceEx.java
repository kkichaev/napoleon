package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	@Scale(value=Consts.QTY_SCALE)
	public int freeQty = 0;
	
	@Scale(value=Consts.QTY_SCALE)
	public int quant = 0;
	
	public String barcode = "";
	
	public int outStock = 0;
}
