package com.grsoft.dataobjects.impl;

import android.content.Context;

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
}
