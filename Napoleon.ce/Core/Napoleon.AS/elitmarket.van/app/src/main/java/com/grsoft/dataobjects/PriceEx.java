package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
    public int tabak = 0;
    public String barcode = "";

    @Scale(value = Consts.SUM_SCALE)
    public int mrc = 9;
}
