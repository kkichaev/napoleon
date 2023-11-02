package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderItemEx extends OrderItem{
	@FieldOrder(order=USER_FIELDS)
	public String unit = "";

    @FieldOrder(order = USER_FIELDS + 1)
    public List<ScannedItems> barcodes = new ArrayList<ScannedItems>();

    @FieldOrder(order=USER_FIELDS + 2)
	@Scale(value=Consts.QTY_SCALE)
	public int unitInpack;
    
    @FieldOrder(order=USER_FIELDS + 3)
    public String uid = UUID.randomUUID().toString().replace("-", "");
    
    public boolean isScanned() {
    	return barcodes.size() == qty / unitInpack;
    }
}
