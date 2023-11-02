package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class PriceEx extends Price {
	public static final int UNIT_PACK = 1;
	public static final int UNIT_ITEM = 2;
	public static final int UNIT_NONE = 0;

	public int newitem = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public int akc1 = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public int akc2 = 0;

	@Scale(value=Consts.SUM_SCALE)
	public int minCost = 0;

	public String gost = "";
	public String cert = "";
	public String stdcond = "";
	public String bestBfr = "";

	public String article = "";

	public String barcode = "";
	public String barcodePack = "";
	public String barcodeType = "";

	public int unitType = 0;
}
