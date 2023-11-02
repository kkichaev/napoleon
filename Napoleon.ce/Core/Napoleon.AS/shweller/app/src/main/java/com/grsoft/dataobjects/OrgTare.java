package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrgTare extends DataObject {
    @FieldOrder(order = 0)
    public String id = "";

    @FieldOrder(order = 1)
    public String number = "";

    @FieldOrder(order = 2)
    public String name = "";
}
