package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.PurchaseImpl;
import com.grsoft.napoleon.R;

public class PurchaseDoc extends OrderDoc {
    static PurchaseDoc insance = null;

    public static PurchaseDoc instance() {
        if(insance == null)
            insance = new PurchaseDoc();
        return insance;
    }

    PurchaseDoc() {
        super("", "PurchaseDoc", PurchaseImpl.class);
    }

    @Override
    public int getDocTitle() {
        return R.string.purchase;
    }

    @Override
    public int getResurceId() {
        return R.drawable.ic_purchase;
    }
}
