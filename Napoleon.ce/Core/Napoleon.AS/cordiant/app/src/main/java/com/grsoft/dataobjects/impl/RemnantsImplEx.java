package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.napoleon.RemnantPriceCount;
import com.grsoft.napoleon.documents.RemnantsDoc;

public class RemnantsImplEx extends RemnantsImpl {
    @Override
    public void editItem(long itemRowid, Context context) {
        RemnantPriceCount.open(context, itemRowid, this);
    }

    public void update(String id, int cost, int face, int qty, int mrc) {
        if(isEditable() == false || data.id.length() == 0)
            return;

        RemnantItemEx re = (RemnantItemEx) findItem(id);
        if(re == null)
        {
            re = new RemnantItemEx();
            re.id = id;

            data.items.add(re);
        }
        re.cost = cost;
        re.qty = qty;
        re.face = face;
        re.mrcChanged = mrc;
        write();

        RemnantsDoc.instance().refreshDocSum(data.id);
    }
}
