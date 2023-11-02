package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderAction extends DataObject{
    @FieldOrder(order = 0)
    public String id = "";

    @Scale(value = Consts.QTY_SCALE)
    @FieldOrder(order = 1)
    public int qty = 0;

    @FieldOrder(order = 2)
    public String item = "";

    @FieldOrder(order = 3)
    public String items = "";
}
