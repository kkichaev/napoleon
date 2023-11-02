package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class Dogovor extends DataObject {
    @FieldOrder(order=0)
    public String id = "";

    @FieldOrder(order=1)
    public String name = "";

    @FieldOrder(order=2)
    public String firm = "";

    @FieldOrder(order = 3)
    @Scale(value = Consts.SUM_SCALE)
    public int limit = 0;

    @FieldOrder(order = 4)
    @Scale(value = Consts.SUM_SCALE)
    public int balance = 0;

    @Override public String toString() { return name; }
}
