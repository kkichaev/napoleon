package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.Date;

@ServerInfo(name="PriceCost")
public class PriceCostRcv extends DataObject {
    public String id = "";
    public String idItem = "";
    public Date date = new Date();

    @Scale(value = Consts.SUM_SCALE)
    public int cost = 0;
}
