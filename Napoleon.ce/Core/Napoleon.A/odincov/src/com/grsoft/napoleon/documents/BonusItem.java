package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class BonusItem extends DataObject {
	
	public String id = "";
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;

}
