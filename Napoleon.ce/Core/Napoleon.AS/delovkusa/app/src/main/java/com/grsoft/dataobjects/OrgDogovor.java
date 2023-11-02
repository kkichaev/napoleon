package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrgDogovor extends DataObject {
    @FieldOrder(order = 0)
    public String id = "";

    @FieldOrder(order = 1)
    public String firm = "";

    @FieldOrder(order = 2)
    public String sklad = "";

    @FieldOrder(order = 3)
    public String name = "";

    @FieldOrder(order = 4)
    public int costype = 0;

    @Override
    public String toString() {return name;}
}
