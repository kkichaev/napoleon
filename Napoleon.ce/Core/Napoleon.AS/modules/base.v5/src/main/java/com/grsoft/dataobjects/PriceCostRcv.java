package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@ServerInfo(name="PriceCost")
public class PriceCostRcv extends DataObject {
    public String id = "";
    public String idItem = "";

    @Scale(value = Consts.SUM_SCALE)
    public int cost = 0;
}
