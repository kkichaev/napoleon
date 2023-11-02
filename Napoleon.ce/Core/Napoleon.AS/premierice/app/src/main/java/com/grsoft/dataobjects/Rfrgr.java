package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class Rfrgr extends DataObject {
    @FieldOrder(order = 0)
    public String id = "";

    @FieldOrder(order = 1)
    public String number = "";

    @FieldOrder(order = 2)
    public String name = "";

    @FieldOrder(order = 3)
    public String volume = "";

    public String getText() {
        return name + " " + volume  + "<br/>¹" + number;
    }
}
