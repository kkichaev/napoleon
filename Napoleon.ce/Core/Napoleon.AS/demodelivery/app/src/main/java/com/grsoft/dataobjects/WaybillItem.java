package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class WaybillItem extends DeliveryItemEx {
    @FieldOrder(order = 101)
    public String barcode = "";
}
