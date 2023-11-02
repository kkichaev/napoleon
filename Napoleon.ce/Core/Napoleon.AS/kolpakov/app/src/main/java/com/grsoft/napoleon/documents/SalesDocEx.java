package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.ExtrasConst;

public class SalesDocEx extends SalesDoc {
    public static void init() {
        instance = new SalesDocEx();
    }

    SalesDocEx() {
        super(SalesImplEx.class);
    }

    @Override
    public DocExportListener getDirtyDocuments() {
        String where = "(params & " + Integer.toString(ParamState.ofExported|ParamState.ofProceeded) + ") = 0 and (compleete <> 0)";
        return new DocSendListner(getObjectName(), SalesImplEx.class, where);
    }
}
