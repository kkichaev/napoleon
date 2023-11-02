package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WaybillItem extends DeliveryItemEx {
    @FieldOrder(order = 101)
    public String barcode = "";
}
