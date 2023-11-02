package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class GatherItem extends DataObject {
	@FieldOrder(order=0)
    public String id = "";
	
    @Scale(value=Consts.QTY_SCALE)
    @FieldOrder(order=3)
    public int qty;
    
    @Scale(value=Consts.QTY_SCALE)
    @FieldOrder(order=4)
    public int weight;
    
    @Scale(value=Consts.QTY_SCALE)
    @FieldOrder(order=5)
    public int factQty;
    
    @Scale(value=Consts.QTY_SCALE)
    @FieldOrder(order=6)
    public int factWeight;
    
    @FieldOrder(order=7)
    public int used;
}
