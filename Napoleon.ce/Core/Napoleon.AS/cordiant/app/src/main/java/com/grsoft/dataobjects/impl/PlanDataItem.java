package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PlanDataItem extends DataObject {
    @FieldOrder(order = 0)
    public String name = "";

    @FieldOrder(order = 1)
    @Scale(value = Consts.SUM_SCALE)
    public int plan = 0;

    @FieldOrder(order = 2)
    @Scale(value = Consts.SUM_SCALE)
    public int fact = 0;
}
