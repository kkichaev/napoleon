package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price implements Comparable<PriceEx> {
    public String barcode = "";

    @Scale(value = Consts.SUM_SCALE)
    public int mrc = 0;

    public String bcBox = "";

    @Scale(value = Consts.QTY_SCALE)
    public int boxInPack = 0;

    @Override
    public int compareTo(PriceEx priceEx) {
        return name.compareTo(priceEx.name);
    }

    @Override public String toString() { return name; }
}
