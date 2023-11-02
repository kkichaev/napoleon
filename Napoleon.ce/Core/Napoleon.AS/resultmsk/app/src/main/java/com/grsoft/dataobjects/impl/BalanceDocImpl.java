package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.BalanceDoc;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.ExtrasConst;

public class BalanceDocImpl extends Document<BalanceDoc> {

    public BalanceDocImpl(BalanceDoc src) {
        data = src;
    }

    @Override
    public long sum() {
        return data.src.sum;
    }

    @Override
    public String getNumber() {
        return data.src.number;
    }

    @Override
    public void open(Context context) {
        if (data.src.isDelivery()) {
            DeliveryImpl di = new DeliveryImpl();
            Delivery d = di.getData();
            d.id = data.id;
            d.number = data.src.number;
            d.date = data.src.date;
            if(di.read()) {
                di.open(context);
            }
        }
    }
    @Override
    public long write() {
        return ExtrasConst.INVALID_ROWID;
    }
}
