package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class DeliveryItemEx extends DeliveryItem {

    @FieldOrder(order=10)
    @Scale(value = Consts.SUM_SCALE)
    public int reqQty = 0;
}
