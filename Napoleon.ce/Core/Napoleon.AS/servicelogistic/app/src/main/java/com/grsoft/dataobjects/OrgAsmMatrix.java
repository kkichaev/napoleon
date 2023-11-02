package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="OrgAsmMtx", keyFields = "id")
@ServerInfo(name="OrgAsmMatrix")
public class OrgAsmMatrix extends DataObject{
    public String id = "";

    public static boolean needCheckAssortment(String id) {
        String where = String.format("id='%s'", id);
        return DbReader.fetch(OrgAsmMatrix.class, where).size() == 0;
    }
}
