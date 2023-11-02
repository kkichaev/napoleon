package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.Date;

public class MerchItem extends DataObject implements QtyItem {
    @FieldOrder(order = 0)
    public String id = "";

    @FieldOrder(order = 1)
    public Date bestBefore = new Date(0);

    @FieldOrder(order = 2)
    @Scale(value = Consts.QTY_SCALE)
    public int qty = 0;

    @FieldOrder(order = 3)
    public Date manufactoring = new Date(0);

    @FieldOrder(order = 4)
    public int expDay = 0;

    @FieldOrder(order = 5)
    public String remark = "";

    @Override public int getQty() { return qty; }

    @Override public int getFlags() { return 0; }
}
