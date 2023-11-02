package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

@TableInfo(name="Sklads", keyFields = "id")
@ServerInfo(name="Sklads")
public class Sklad extends DataObject implements Comparable<Sklad> {
    public String id = "";
    public String name = "";
    public int index = 0;
    public String base = "";

    @Override public String toString() { return name; }

    @Override
    public int compareTo(Sklad sklad) { return index - sklad.index; }
}
