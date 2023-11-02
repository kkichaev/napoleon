package com.grsoft.manager.documents;

import com.grsoft.dataobjects.impl.MDistribImpl;
import com.grsoft.manager.R;

public class MDistribDoc extends MDocType{
    static MDistribDoc instance = null;
    static final String OBJ_NAME = "Distrib";

    MDistribDoc() {
        super(OBJ_NAME, MDistribImpl.class);
    }

    public static MDistribDoc instance() {
        if(instance == null)
            instance = new MDistribDoc();
        return instance;
    }

    @Override
    public int getDocTitle() {
        return R.string.distrib_doc_title;
    }
}
