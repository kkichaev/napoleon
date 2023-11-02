package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrderItemEx extends OrderItem{
    @FieldOrder(order = USER_FIELDS)
    public String card = "";

    @FieldOrder(order = USER_FIELDS + 1)
    public int discount = 0;

    @FieldOrder(order = USER_FIELDS + 2)
    public int costWOD = 0;
}
