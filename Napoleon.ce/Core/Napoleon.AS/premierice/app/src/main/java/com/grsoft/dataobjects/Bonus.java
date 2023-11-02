package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="bonus", keyFields = "ids,id")
@ServerInfo(name="Bonus")
public class Bonus extends DataObject {
    public String id = "";
    public String ids = "";

    @Scale(value = Consts.QTY_SCALE)
    public int qty = 0;
}
