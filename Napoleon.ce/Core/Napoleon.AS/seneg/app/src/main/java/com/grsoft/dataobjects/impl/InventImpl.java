package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Invent;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.InventDetail;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.InventDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;

public class InventImpl extends OrderImplBase<Invent> {
    @Override
    public void editProperties(Context ctx, boolean isOldOrder) {
    }

    @Override
    public CreatableDocument<Invent> createInstance() { return new InventImpl(); }

    @Override
    public void open(Context context) {
        InventDetail.open(context, this);
    }

    @Override
    public long sum() { return 0; }

    @Override
    public boolean init(Context context, String orgId, GpsCoord coord) {
        super.initSilent(context, orgId, coord);
        return true;
    }

    @Override
    public void editItem(final long itemRowid, final Context context) {
        if( !isEditable() )
            return;

        final PriceImpl priceImpl = new PriceImpl();
        priceImpl.read(itemRowid);
        priceImpl.close();

        InputNumberDlg.open(context, new InputNumber() {

            @Override
            public void applayInput(int value, Object... params) {
                if (!isEditable())
                    return;

                int cost = 0;
                if (updateQty(priceImpl, value, cost, false) && context instanceof DataSetNotify)
                    ((DataSetNotify)context).notifyDataSetChanged();

                InventDoc.instance().refreshDocSum(data.id);
            }

            @Override
            public int getValue() {
                OrderItem ri = (OrderItem)findItem(priceImpl.data.id);
                return ri == null ? 0 : ri.qty;
            }
        });
    }

    @Override protected boolean checkPriceQty() { return false; }
}
