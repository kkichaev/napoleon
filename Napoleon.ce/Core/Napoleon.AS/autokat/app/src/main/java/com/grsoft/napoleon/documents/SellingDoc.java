package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.SellingImpl;
import com.grsoft.napoleon.R;

public class SellingDoc extends OrderDoc {
    static SellingDoc instance  = null;

    public static SellingDoc instance() {
        if(instance == null)
            instance = new SellingDoc();
        return instance;
    }

    SellingDoc() {
        super("", "SellingDoc", SellingImpl.class);
    }

    @Override
    public int getDocTitle() {
        return R.string.selling_title;
    }

    @Override
    public int getResurceId() {
        return R.drawable.ic_sell;
    }
}
