package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.network.DocExportListener;

public class SalesDocEx extends SalesDoc {
    public static void init() {
        instance = new SalesDocEx();
    }

    SalesDocEx() {
        super(SalesImplEx.class);
    }

    @Override
    public DocExportListener getDirtyDocuments() {
        String where = "params = 0 and compleete > 0";
        return new DocSendListner(getObjectName(), SalesImplEx.class, where);
    }
}
