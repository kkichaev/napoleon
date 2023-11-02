package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;

public class PriceEx extends Price implements Comparable<PriceEx> {
    public int tabak = 0;
    public String barcode = "";

    @Scale(value = Consts.SUM_SCALE)
    public int mrc = 9;

    @Override
    public int compareTo(PriceEx o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() { return name; }
}
