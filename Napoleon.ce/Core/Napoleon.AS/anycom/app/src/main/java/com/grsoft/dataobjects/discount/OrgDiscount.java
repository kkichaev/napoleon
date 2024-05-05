package com.grsoft.dataobjects.discount;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="OrgDiscount", keyFields = "id,idOrg")
@ServerInfo(name="OrgDiscount")
public class OrgDiscount extends DataObject {
    public String id = "";
    public String idOrg = "";
}
