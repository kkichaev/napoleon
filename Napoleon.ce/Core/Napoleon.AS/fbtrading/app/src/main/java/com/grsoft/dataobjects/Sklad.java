package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.List;

@TableInfo(name="skalds", keyFields = "id")
@ServerInfo(name="Sklads")
public class Sklad extends DataObject{
    public String id = "";
    public String name = "";
    public String userid = "";
    public String agentid = "";

    @Override
    public String toString() { return name; }

    public static Sklad mySklad() {
        List<Sklad> r = DbReader.fetch(Sklad.class, "userid = agentid");
        return r.size() == 0 ? new Sklad() : r.get(0);
    }
}
