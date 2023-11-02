package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;

public class PriceEx extends Price {
    public int pos = 0;

    public List<StoreData> stores = new ArrayList<>();
//    public int bmark = 0;
//
//    @Scale(value= Consts.QTY_SCALE)
//    public int bqty = 0;
}
