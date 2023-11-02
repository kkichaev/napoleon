package com.grsoft.dataobjects;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgSegment extends DataObject {
    @FieldOrder(order = 0)
    public String id = "";

    @FieldOrder(order = 1)
    @Scale(value = Consts.SUM_SCALE)
    public int tarif = 0;

    @FieldOrder(order = 2)
    @Scale(value = Consts.SUM_SCALE)
    public int nac = 0;
}
