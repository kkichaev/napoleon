package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;

import java.util.List;

@TableInfo(name="suppl", keyFields = "id")
@ServerInfo(name="Supplier")
public class Supplier extends DataObject {
    public String id = "";
    public String name = "";
    public int pos = 0;
    public int aikos = 0;

    public static String aikosId() {
        List<Supplier> s = DbReader.fetch(Supplier.class, "aikos <> 0");
        return s.size() == 0 ? "" : s.get(0).id;
    }
}
