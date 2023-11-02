package com.grsoft.dataobjects;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class FacingItem extends DataObject {
	public String id = "";
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;
	
	public int modified = 0;
}
