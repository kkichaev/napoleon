package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

import java.util.Date;

public class ReturnItem extends OrderItem {
    @FieldOrder(order = USER_FIELDS)
    public Date production = new Date(100);

    @FieldOrder(order = USER_FIELDS + 1)
    public Date expired = new Date(100);
}
