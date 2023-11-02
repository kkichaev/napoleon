package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class RelatedItem extends DataObject {
    @FieldOrder(order = 0)
    public String id = "";

    @FieldOrder(order = 2)
    @Scale(value= Consts.QTY_SCALE)
    public int qty = 0;
}
