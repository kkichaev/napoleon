package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceQty extends DataObject {
    public String id = "";
    public String idsklad = "";

    @Scale(value = Consts.QTY_SCALE)
    public int qty = 0;
}
