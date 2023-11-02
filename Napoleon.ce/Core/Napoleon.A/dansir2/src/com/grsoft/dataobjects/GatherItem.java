package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class GatherItem extends DataObject {
	@FieldOrder(order=0)
	public String item = "";
	
	@FieldOrder(order=1)
    public String i_id = "";
	@FieldOrder(order=2)
    public String unit = "";
    
    @Scale(value=Consts.QTY_SCALE)
    @FieldOrder(order=3)
    public int qty;
    
    @Scale(value=Consts.QTY_SCALE)
    @FieldOrder(order=4)
    public int newQty;
    
    @Scale(value=Consts.SUM_SCALE)
    @FieldOrder(order=5)
    public int cost;
    
    @FieldOrder(order=6)
    public String new_id = "";
}
