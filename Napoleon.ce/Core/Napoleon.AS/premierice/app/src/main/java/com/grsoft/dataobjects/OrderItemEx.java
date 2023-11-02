package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrderItemEx extends OrderItem {
    @FieldOrder(order =  USER_FIELDS)
    public int bonus = 0;
}
