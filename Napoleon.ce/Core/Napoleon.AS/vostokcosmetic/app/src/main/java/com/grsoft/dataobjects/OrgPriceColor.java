package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="opcolor", keyFields = "ido,id")
@ServerInfo(name="OrgPriceColor")
public class OrgPriceColor extends DataObject{
    public String ido = "";
    public String id = "";
    public int color = 0;
}
