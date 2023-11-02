package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgTare;
import com.grsoft.dataobjects.Tare;
import com.grsoft.napoleon.OrgTares;
import com.grsoft.napoleon.TareDetail;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;

public class TareImpl extends OrderImplBase<Tare> {
    @Override
    public void open(Context context) {
        TareDetail.open(context, this);
    }

    @Override
    public void editProperties(Context ctx, boolean isOldOrder) {}

    @Override
    public CreatableDocument<Tare> createInstance() {
        return new TareImpl();
    }

    @Override
    public void editItem(long itemRowid, Context context) {
        OrgTares.open(context, this);
    }

    public void update(OrgTare item) {
        OrderItem oi = (OrderItem) findItem(item.id);
        if(oi == null) {
            oi = new OrderItem();
            oi.id = item.id;
            data.items.add(oi);
        } else {
            data.items.remove(oi);
        }
        write();
    }

    @Override
    public boolean init(Context context, String orgId, GpsCoord coord) {
        super.init(context, orgId, coord);
        return true;
    }
}
