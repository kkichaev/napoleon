package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.TareImpl;
import com.grsoft.napoleon.R;

public class TareDoc extends OrderDoc {
    static TareDoc instance = null;

    static public TareDoc instance() {
        if(instance == null) {
            instance = new TareDoc();
        }
        return instance;
    }

    TareDoc() {
        super("", "TareDoc", TareImpl.class);
    }

    @Override
    public int getDocTitle() {
        return R.string.tare_doc;
    }

    @Override
    public int getResurce2Id() {
        return R.drawable.tare_doc2;
    }

    @Override
    public int getResurceId() {
        return R.drawable.tare_doc;
    }
}
