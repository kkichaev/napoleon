package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;

public class PriceEx extends Price {
    public String analog = "";

    @Scale(value= Consts.QTY_SCALE)
    public int freeQty = 0;

    @Scale(value=Consts.QTY_SCALE)
    public int rezervQty = 0;

    @Scale(value=Consts.QTY_SCALE)
    public int orderQty = 0;

    public int dlvDays = 0;
    public int delist = 0;

    public int clrbak = 0;

    @Override public String toString() { return name; }
}
