package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

@TableInfo(name = "DisableOrg", keyFields = "id,idOrg")
@ServerInfo(name="DisabledOrg")
public class DisableOrg extends DataObject{
    public String id = "";
    public String idOrg = "";
    public int creditDisable = 0;
    public int block = 0;
}
