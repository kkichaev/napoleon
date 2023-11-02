package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrgPriceType extends DataObject {
    @FieldOrder(order = 0)
    public String group = "";

    @FieldOrder(order = 1)
    public int costype = 0;
}
