package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrgDogovor extends DataObject {
    @FieldOrder(order = 0)
    public String id = "";

    @FieldOrder(order = 1)
    public String idOrg = "";

    @FieldOrder(order = 2)
    public String name = "";

    @FieldOrder(order = 3)
    public int costype = 0;

    @Override public String toString() { return name; }
}
