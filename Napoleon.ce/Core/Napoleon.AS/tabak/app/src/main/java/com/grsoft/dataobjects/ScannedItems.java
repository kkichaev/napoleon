package com.grsoft.dataobjects;

import androidx.annotation.NonNull;

import com.grsoft.types.FieldOrder;

public class ScannedItems extends DataObject {
    public ScannedItems() {}
    public ScannedItems(String bc) { barcode = bc; }

    @FieldOrder(order = 0)
    public String barcode = "";

    @NonNull
    @Override public String toString() { return barcode; }

    public boolean isPackCode() {
        return barcode.startsWith("01");
    }
}
