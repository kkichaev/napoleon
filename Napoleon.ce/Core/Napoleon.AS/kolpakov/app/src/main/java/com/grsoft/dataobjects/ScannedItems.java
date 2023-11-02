package com.grsoft.dataobjects;

import androidx.annotation.NonNull;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class ScannedItems extends DataObject {
    public ScannedItems() {}
    public ScannedItems(String bc, int qty) { barcode = bc; this.qty = qty; }

    @FieldOrder(order = 0)
    public String barcode = "";

    @FieldOrder(order = 1)
    @Scale(value= Consts.QTY_SCALE)
    public int qty = 0;

    @NonNull
    @Override public String toString() { return barcode; }
}
