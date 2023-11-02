package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	@Scale(value=Consts.QTY_SCALE)
	public int pack;
	
	public int license = 0;
	
	public List<MpcyItem> mult = new ArrayList<MpcyItem>();
}
