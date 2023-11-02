package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price{

    @Scale(Consts.SUM_SCALE)
    public int wcost = 0;
}
