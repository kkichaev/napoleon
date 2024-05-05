package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.Date;

@ServerInfo(name="StoreQty")
public class StoreQty extends DataObject {
    public String id = "";
    public String idItem = "";

    @Scale(value = Consts.QTY_SCALE)
    public int qty = 0;

    public Date date = new Date();

    @Scale(value = Consts.QTY_SCALE)
    public int arrival = 0;
}
