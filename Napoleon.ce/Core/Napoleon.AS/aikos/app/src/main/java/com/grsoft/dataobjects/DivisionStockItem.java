package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class DivisionStockItem extends DataObject{
    @FieldOrder(order = 0)
    public int division = 0;

    @FieldOrder(order = 1)
    @Scale(value = Consts.QTY_SCALE)
    public int qty = 0;
}
