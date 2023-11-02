package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class StoreData extends DataObject{
    @FieldOrder(order = 0)
    public int bmark = 0;
    @FieldOrder(order = 1)
    @Scale(value = Consts.SUM_SCALE)
    public int cost = 0;
    @Scale(value = Consts.QTY_SCALE)
    @FieldOrder(order = 2)
    public int qty = 0;
}
