package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.BSellingImpl;
import com.grsoft.dataobjects.impl.SellingImpl;
import com.grsoft.napoleon.R;
import com.grsoft.network.DocExportListener;

public class BlackSellingDoc extends OrderDoc {
    static BlackSellingDoc instance = null;

    public static BlackSellingDoc instance() {
        if(instance == null)
            instance = new BlackSellingDoc();
        return instance;
    }

    BlackSellingDoc() {
        super("", "BSellingDoc", BSellingImpl.class);
    }

    @Override
    public int getDocTitle() {
        return R.string.bselling_title;
    }

    @Override
    public int getResurceId() {
        return R.drawable.ic_sell;
    }

    @Override
    public DocExportListener getDirtyDocuments() {return null;}
}
