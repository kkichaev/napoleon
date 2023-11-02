package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class ScannedItems extends DataObject {
    public ScannedItems() {}
    public ScannedItems(String bc) { barcode = bc; }

    @FieldOrder(order = 0)
    public String barcode = "";

    @Override public String toString() { return barcode; }

    public boolean isPackCode() {
        return barcode.startsWith("01");
    }
}
