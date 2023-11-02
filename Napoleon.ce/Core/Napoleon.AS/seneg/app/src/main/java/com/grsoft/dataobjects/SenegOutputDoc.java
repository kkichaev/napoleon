package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="SenegOutputDoc", keyFields = "created")
@ServerInfo(name="SenegOutputDoc")
public class SenegOutputDoc extends CreateDocDataObject {
    public String link = "";
    public int docType = 0;
}
