package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class GrassDiscount extends DataObject implements Comparable<GrassDiscount>{
    @Scale(value = Consts.SUM_SCALE)
    @FieldOrder(order = 0)
    public int discount = 0;
    @FieldOrder(order = 1)
    public int qty = 0;
    @FieldOrder(order = 2)
    public String unit = "";

    @Override
    public int compareTo(GrassDiscount other) {
        return qty - other.qty;
    }
}
