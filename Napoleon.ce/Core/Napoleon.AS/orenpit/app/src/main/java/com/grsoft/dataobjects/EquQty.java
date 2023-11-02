package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="equqty", keyFields = "id,idItem")
@ServerInfo(name = "EquQty")
public class EquQty extends DataObject{
    public String id = "";
    public String idItem = "";

    @Scale(Consts.QTY_SCALE)
    public int qty;
}
