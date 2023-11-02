package com.grsoft.database;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class PriceHitchingEx extends PriceHitching {

    @Override
    protected void beforeInsert(Price dobj) {
        super.beforeInsert(dobj);

        PriceEx pe = PriceQtyHitching.price.get(dobj.id);
        if(pe != null) {
            dobj.qty = pe.qty;
            ((PriceEx)dobj).whQty = pe.whQty;
        }
        dobj.updateWhState();
    }
}
