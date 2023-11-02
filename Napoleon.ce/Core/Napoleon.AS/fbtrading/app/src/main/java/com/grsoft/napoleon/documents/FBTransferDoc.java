package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.FBTransferImpl;
import com.grsoft.napoleon.R;
import com.grsoft.network.DocExportListener;

public class FBTransferDoc extends DocType {
    static FBTransferDoc instance = null;

    static public FBTransferDoc instance() {
        if(instance == null)
            instance = new FBTransferDoc();
        return instance;
    }

    FBTransferDoc() {
        super("Перемещение", "FBTransfer", (Class<? extends Document<?>>) FBTransferImpl.class);
    }

    @Override
    public int getDocTitle() { return R.string.fb_transfer; }

    @Override
    public int getResurceId() {
        return R.drawable.doc_transfer;
    }

    @Override
    public int getResurce2Id() {
        return R.drawable.doc_transfer_2;
    }

    @Override
    public DocExportListener getDirtyDocuments() {
        return null;
    }
}
