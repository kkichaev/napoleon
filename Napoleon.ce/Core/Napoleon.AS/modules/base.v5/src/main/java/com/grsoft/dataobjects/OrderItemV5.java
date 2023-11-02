package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrderItemV5 extends OrderItem {
    @FieldOrder(order = 4)
    public String unit = "";
}
