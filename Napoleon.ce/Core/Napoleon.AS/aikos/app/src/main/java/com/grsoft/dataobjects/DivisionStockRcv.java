package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class DivisionStockRcv extends DataObject{
    public String division = "";
    public String id = "";

    @Scale(value = Consts.QTY_SCALE)
    public int qty;
}
