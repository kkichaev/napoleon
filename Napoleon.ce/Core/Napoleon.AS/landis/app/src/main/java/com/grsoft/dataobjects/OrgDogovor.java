package com.grsoft.dataobjects;

public class OrgDogovor extends DataObject implements Comparable<OrgDogovor>{

    public String id = "";
    public String name = "";

    @Override
    public int compareTo(OrgDogovor o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
