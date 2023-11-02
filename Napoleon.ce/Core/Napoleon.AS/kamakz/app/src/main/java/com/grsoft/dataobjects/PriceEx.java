package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
    @Scale(value = Consts.SUM_SCALE)
    public int width = 0;
    @Scale(value = Consts.SUM_SCALE)
    public int height = 0;
    @Scale(value = Consts.SUM_SCALE)
    public int dia = 0;

    public int studded = 0;

    public String season = "";
    public String axe = "";
}
