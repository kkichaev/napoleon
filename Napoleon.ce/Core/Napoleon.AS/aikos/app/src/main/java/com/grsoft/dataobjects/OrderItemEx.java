package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrderItemEx extends OrderItem {
    @FieldOrder(order = USER_FIELDS)
    public String remark = "";

    @FieldOrder(order = USER_FIELDS + 1)
    public String whCode = "";
}
