package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class RemnantItemEx extends RemnantItem implements QtyItem {
    @FieldOrder(order = 10)
    public int face = 0;

    @FieldOrder(order = 11)
    @Scale(value = Consts.SUM_SCALE)
    public int cost = 0;

    @Override public int getQty() { return qty; }
    @Override public int getFlags() { return 0; }

    @FieldOrder(order = 12)
    public int mrcChanged = 0;
}
