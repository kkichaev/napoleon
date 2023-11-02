package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrgCostItem extends DataObject {
    @FieldOrder(order=0)
    public String firm = "";

    @FieldOrder(order=1)
    public String cost = "";
}
