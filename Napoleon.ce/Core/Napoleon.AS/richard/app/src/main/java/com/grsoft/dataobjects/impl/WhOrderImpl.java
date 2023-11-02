package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.WhOrder;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.WhOrderDetail;
import com.grsoft.napoleon.WhOrderProp;
import com.grsoft.napoleon.documents.CreatableDocument;

public class WhOrderImpl extends OrderImplBase<WhOrder> {
    @Override
    public void editProperties(Context ctx, boolean isOldOrder) {
        WhOrderProp.open(ctx, this, isOldOrder);
    }

    @Override
    public CreatableDocument<WhOrder> createInstance() {
        return new WhOrderImpl();
    }

    @Override
    public void editItem(long itemRowid, Context context) {
        PriceCount.open(context, itemRowid, this);
    }

    @Override
    public void open(Context context) {
        WhOrderDetail.open(context, this);
    }

    @Override
    public int getItemValue(Price item) {
        PriceEx pe = (PriceEx)item;
        int whIndex = ((OrderEx)data).whIndex;

        if( whIndex <= 0 || whIndex > pe.whQty.size())
            return super.getItemValue(item);

        return pe.whQty.get(whIndex-1).qty;
    }
}
