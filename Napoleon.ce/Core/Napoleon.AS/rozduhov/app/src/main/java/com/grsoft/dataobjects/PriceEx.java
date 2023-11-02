package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	@Scale(value=Consts.QTY_SCALE)
	public int minQty;

	public String tradeMark="";
	
	public String expired = "";
	
	public List<QtyDataItem> qtys = new ArrayList<QtyDataItem>();
	
	public int haveParts = 0;
	public List<PartsData> parts = new ArrayList<PartsData>();
}
