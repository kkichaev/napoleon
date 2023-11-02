package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgCost extends DataObject {
    public static final int TYPE_ITEM = 0;
    public static final int TYPE_FOLDER = 1;

    @FieldOrder(order = 0)
    public String id = "";

    @FieldOrder(order = 1)
    public int type = TYPE_ITEM;

    @FieldOrder(order = 2)
    @Scale(value = Consts.SUM_SCALE)
    public int discount = 0;
}
