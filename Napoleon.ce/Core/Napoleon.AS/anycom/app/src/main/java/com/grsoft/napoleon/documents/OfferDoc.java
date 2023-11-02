package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.OfferImpl;
import com.grsoft.napoleon.R;

public class OfferDoc extends DateDocType {
    static OfferDoc instance;

    public static OfferDoc instance() {
        if(instance == null)
            instance = new OfferDoc();
        return instance;
    }

    OfferDoc() {
        super("Offer", "Offer", OfferImpl.class);
    }

    @Override
    public int getResurceId() {
        return R.drawable.shop;
    }

    @Override
    public int getResurce2Id() {
        return R.drawable.shop2;
    }

    @Override
    public int getDocTitle() {
        return R.string.offer_doc;
    }
}
