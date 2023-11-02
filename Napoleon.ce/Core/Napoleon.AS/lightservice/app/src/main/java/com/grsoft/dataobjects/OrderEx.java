package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
    public int moneyProc = 0;
    public int itemsProc = 0;
    public int cert = 0;

    public String dogovor = "";
    public String address = "";

    @Scale(Consts.SUM_SCALE)
    public int discount;
}
