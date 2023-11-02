package com.grsoft.dataobjects;

import com.grsoft.napoleon.BarcodeData;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;

public class OutcomeItem extends DataObject {
    @FieldOrder(order = 0)
    public String id = "";

    @FieldOrder(order = 1)
    @Scale(value = Consts.QTY_SCALE)
    public int qty = 0;

    @FieldOrder(order = 2)
    @Scale(value = Consts.QTY_SCALE)
    public int inputQty = 0;

    @FieldOrder(order = 3)
    public List<ScannedItems> barcodes = new ArrayList<>();

    @FieldOrder(order = 4)
    @Scale(value = Consts.QTY_SCALE)
    public int packQty = 0;

    @FieldOrder(order = 5)
    @Scale(value = Consts.QTY_SCALE)
    public int inputPackQty = 0;

    @FieldOrder(order = 6)
    @Scale(value = Consts.QTY_SCALE)
    public int boxQty = 0;

    @FieldOrder(order = 7)
    @Scale(value = Consts.QTY_SCALE)
    public int inputBoxQty = 0;


    public  boolean isCompleete() { return qty == inputQty && packQty == inputPackQty && boxQty == inputBoxQty; }

    public boolean haveBC(String bc) {
        for(ScannedItems si : barcodes)
            if(si.code.equals(bc))
                return true;

        return false;
    }

    public boolean canAdd(BarcodeData bc) {
        if(bc.isItemCode)
            return qty > inputQty;
        if(bc.isBox)
            return boxQty > inputBoxQty;
        return packQty > inputPackQty;
    }
}
