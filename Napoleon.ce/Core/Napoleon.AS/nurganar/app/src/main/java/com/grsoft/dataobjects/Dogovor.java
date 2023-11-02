package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name = "dogovor", keyFields = "id,ido", indexes = "ido")
@ServerInfo(name="Dogovor")
public class Dogovor extends DataObject {
    public String id = "";
    public String ido = "";
    public String name = "";

    @Override
    public String toString() {
        return name;
    }
}
