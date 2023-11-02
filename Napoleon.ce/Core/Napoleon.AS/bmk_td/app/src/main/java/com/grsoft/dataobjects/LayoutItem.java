package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class LayoutItem extends DataObject {
    @FieldOrder(order = 0)
    public String grid = "";

    @FieldOrder(order = 1)
    public String grname = "";

    @FieldOrder(order = 2)
    public String itid = "";

    @FieldOrder(order = 3)
    public String itname = "";

    @FieldOrder(order = 4)
    @Scale(value = Consts.QTY_SCALE)
    public int qty = 0;

    @FieldOrder(order = 4)
    public int grpos = 0;

    @FieldOrder(order = 5)
    public String date = "";

    @FieldOrder(order = 6)
    public String remark;

    @FieldOrder(order = 7)
    public String cause;

    @FieldOrder(order = 8)
    public int pos;

    @FieldOrder(order = 9)
    public int prop;
}
