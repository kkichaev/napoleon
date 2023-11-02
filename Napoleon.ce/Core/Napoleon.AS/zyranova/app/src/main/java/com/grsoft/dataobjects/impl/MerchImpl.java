package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Merch;
import com.grsoft.dataobjects.MerchItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.napoleon.CreateMerch;
import com.grsoft.napoleon.MerchCount;
import com.grsoft.napoleon.MerchDetail;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.MerchDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;

import java.util.Date;

public class MerchImpl extends CreatableDocument<Merch> implements Itemsable
{
    @Override public void open(Context context) { MerchDetail.open(context, this); }

    @Override
    public void editItem(long itemRowid, Context context) {
        MerchCount.open(context, itemRowid, this);
    }

    @Override
    public DataObject findItem(String itemId) {
        for(MerchItem mi : data.items) {
            if(mi.id.equals(itemId))
                return mi;
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return data.items.size() == 0 || super.isEmpty();
    }

    @Override
    public int getItemColor() { return R.color.magneta; }

    @Override
    public int getItemValue(Price item) {
        return item.qty;
    }

    @Override
    public int getItemQty(Price item) {
        RemnantItem ri = (RemnantItem) findItem(item.id);
        return ri == null ? 0 : ri.qty;
    }

    @Override
    public long getItemSum(Price item) {
        return 0;
    }

    @Override
    public int qty() {
        int count = 0;
        for(MerchItem oi : data.items)
            count += oi.qty;
        return count/ Consts.QTY_SCALE;
    }

    public void update(String id, int qty, Date bestBefore) {
        MerchItem mi = (MerchItem) findItem(id);
        if(qty == 0) {
            data.items.remove(mi);
        } else {
            if(mi == null) {
                mi = new MerchItem();
                mi.id = id;
                data.items.add(mi);
            }
            mi.qty = qty;
            mi.bestBefore = bestBefore;
        }
        write();
        MerchDoc.instance().refreshDocSum(data.id);
    }

    @Override
    public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
        super.init(context, orgId, gpsCoord);
        CreateMerch.open(context, getRowid());
        return false;
    }

    @Override
    public boolean updateQty(PriceImpl priceImpl, int qty, long cost, boolean inPack) {
        return true;
    }

    public void deleteItem(String id) {
        for (MerchItem mi : data.items) {
            if(mi.id.equals(id)) {
                data.items.remove(mi);
                write();
                break;
            }
        }
    }

    public void editItem(long rowid, Context context, int position) {
        MerchCount.open(context, rowid, this, position);
    }
}
