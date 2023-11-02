package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class ScannedItems extends DataObject {
    @FieldOrder(order = 0)
    public String code = "";

    public ScannedItems(String c) { code = c;}
    public ScannedItems() {}
}
