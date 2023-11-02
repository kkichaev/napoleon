package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.util.Consts;

public class SalesImplEx extends SalesImpl {
    @Override
    public boolean isEditable() {
        return (((SalesEx)data).canEdit() && super.isEditable());
    }

    @Override
    public void initDocNumber() {
        data.number = "";
    }

    @Override
    public int getItemValue(Price item) {
        int qty = super.getItemValue(item);

        if(((PriceEx)item).unitType == PriceEx.UNIT_PACK && item.qtyInPack != 0) {
            qty = (int)((long)qty * Consts.QTY_SCALE / item.qtyInPack);
        }
        return qty;

    }
}
