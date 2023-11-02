package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="PlanogramDoc", keyFields = "created")
@ServerInfo(name="PlanogramDoc")
public class PlanogramDoc extends CreateDocDataObject {
    public String planogram = "";
    public String planogramTitle = "";
}
