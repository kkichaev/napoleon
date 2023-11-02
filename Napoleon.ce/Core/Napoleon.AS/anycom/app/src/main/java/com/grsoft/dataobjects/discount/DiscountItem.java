package com.grsoft.dataobjects.discount;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.FieldOrder;

public class DiscountItem extends DataObject {
    public static final int TYPE_ITEM = 0;
    public static final int TYPE_GROUP = 1;

    @FieldOrder(order = 0)
    public String id = "";

    @FieldOrder(order = 1)
    public int type = TYPE_ITEM;
}
