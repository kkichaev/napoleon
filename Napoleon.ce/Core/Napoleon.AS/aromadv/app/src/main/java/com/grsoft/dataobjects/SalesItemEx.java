package com.grsoft.dataobjects;

import com.grsoft.napoleon.BarcodeData;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SalesItemEx extends SalesItem {
    @FieldOrder(order = USER_FIELDS)
    @Scale(value= Consts.QTY_SCALE)
    public int itemQty = 0;

    @FieldOrder(order = USER_FIELDS + 1)
    @Scale(value= Consts.QTY_SCALE)
    public int packQty = 0;

    @FieldOrder(order = USER_FIELDS + 2)
    public List<ScannedItems> barcodes = new ArrayList<>();

    @FieldOrder(order = USER_FIELDS + 3)
    public String uid = UUID.randomUUID().toString().replace("-", "");

    public int factPack() {
        int qty = 0;
        for(ScannedItems si : barcodes)
            if(si.isPackCode())
                qty += Consts.QTY_SCALE;
        return qty;
    }

    public int factQty() {
        int qty = 0;
        for(ScannedItems si : barcodes)
            if(!si.isPackCode())
                qty += Consts.QTY_SCALE;
        return qty;
    }

    public boolean haveBC(String bc) {
        for(ScannedItems si : barcodes)
            if(si.barcode.equals(bc))
                return true;

        return false;
    }

    public boolean canAdd(BarcodeData bc) {
        if(bc.isItemCode)
            return itemQty > factQty();
        return packQty > factPack();
    }
}
