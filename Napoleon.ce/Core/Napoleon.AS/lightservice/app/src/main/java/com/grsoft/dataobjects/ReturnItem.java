package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class ReturnItem extends OrderItem {
    @FieldOrder(order = USER_FIELDS)
    public String quality = "";

    @FieldOrder(order = USER_FIELDS + 1)
    public String remark = "";
}
