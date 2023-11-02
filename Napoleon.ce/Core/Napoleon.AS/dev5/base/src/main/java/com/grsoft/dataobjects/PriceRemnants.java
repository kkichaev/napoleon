package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceRemnants extends DataObject {
	public String id = "";
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty;
}
