package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.MerchImpl;
import com.grsoft.napoleon.R;

public class MerchDoc extends DateDocType {
    public static MerchDoc instance = null;
    public static MerchDoc instance() {
        if(instance == null)
            instance = new MerchDoc();
        return instance;
    }

    MerchDoc() {
        super("Merch", "Merch", MerchImpl.class);
    }

    @Override
    public int getResurceId() {
        return R.drawable.remnants_doc;
    }

    @Override
    public int getResurce2Id() {
        return R.drawable.remnants_doc_2;
    }

    @Override
    public int getDocTitle() {
        return R.string.merch_doc;
    }
}
