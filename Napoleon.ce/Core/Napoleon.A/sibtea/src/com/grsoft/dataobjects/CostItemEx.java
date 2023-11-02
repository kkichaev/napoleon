package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class CostItemEx extends CostItem {
	@FieldOrder(order=1)
	@Scale(value=Consts.SUM_SCALE)
    public int oldCost; 
}
