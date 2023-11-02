package com.grsoft.manager.documents;

import com.grsoft.dataobjects.impl.MContractImpl;
import com.grsoft.manager.R;

public class MContractDoc extends MDocType{
    static protected MContractDoc instance = null;
    private static final String OBJ_NAME = "Contract";

    protected MContractDoc() {
        this(MContractImpl.class);
    }

    protected MContractDoc(Class<? extends MContractImpl> type){
        super(OBJ_NAME, type);
    }

    static public MDocType instance() {
        if( instance == null )
            instance = new MContractDoc();
        return instance;
    }

    static public MDocType instance(Class<? extends MContractImpl> type) {
        instance = new MContractDoc(type);
        return instance;
    }

    @Override
    public int getDocTitle() { return R.string.contract_doc_title;	}
}
