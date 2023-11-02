package com.grsoft.dataobjects;

import com.grsoft.dataobjects.impl.DShipmentImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.types.FieldOrder;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShipmentItem extends DWaybillDocumentItem {
    @FieldOrder(order = 102)
    public String uid = UUID.randomUUID().toString().replace("-", "");

    @FieldOrder(order = 103)
    public List<BCItem> barcodes = new ArrayList<>();

    public boolean isScanned() { return barcodes.size() == outqty / Consts.QTY_SCALE; }

    public boolean haveBC(String barcode) {
        for(BCItem b : barcodes) {
            if(b.mark.equals(barcode))
                return true;
        }
        return false;
    }

    public void rejectMissing(DShipmentImplEx doc) {
        int newQty = barcodes.size() * Consts.QTY_SCALE;
        doc.updateQty(id, newQty);
    }
}
