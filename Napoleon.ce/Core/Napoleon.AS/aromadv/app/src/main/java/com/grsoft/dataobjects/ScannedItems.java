package com.grsoft.dataobjects;

import androidx.annotation.NonNull;

import com.grsoft.napoleon.BarcodeData;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;

public class ScannedItems extends DataObject {
    public ScannedItems() {}
    public ScannedItems(String bc, BarcodeData bd) {
        barcode = bc;
        if(!bd.isItemCode)
            packed = 1;
        else
            packed = 0;
    }

    @FieldOrder(order = 0)
    public String barcode = "";

    @FieldOrder(order = 1)
    @FieldVersion(version = 2)
    public int packed = -1;

    @NonNull
    @Override public String toString() { return barcode; }

    public boolean isPackCode() {
        if(packed < 0)
            packed = barcode.startsWith("01") || barcode.startsWith("02") ? 1 : 0;
        return packed > 0;
    }
}
