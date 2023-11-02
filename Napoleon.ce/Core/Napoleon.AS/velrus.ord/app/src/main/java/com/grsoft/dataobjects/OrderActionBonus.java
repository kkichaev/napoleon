package com.grsoft.dataobjects;


import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderActionBonus extends DataObject {
    public String id = "";

    @Scale(value = Consts.QTY_SCALE)
    public int qty = 0;
}
