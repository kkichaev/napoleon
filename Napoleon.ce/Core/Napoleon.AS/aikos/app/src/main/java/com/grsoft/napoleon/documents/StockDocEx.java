package com.grsoft.napoleon.documents;

import android.annotation.SuppressLint;

import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.network.DocExportListener;

public class StockDocEx extends RemnantsDoc {
    public static void init() {
        instance = new StockDocEx();
    }

    StockDocEx() {
        super(DOC_NAME, OBJ_NAME, RemnantsImplEx.class);
    }

    @Override
    public DocExportListener getDirtyDocuments() {
        @SuppressLint("DefaultLocale") String where = String.format("(params & %d) = 0 and complete=1", getExportFlag());
        return new DocSendListner(getObjectName(), RemnantsImplEx.class, where);
    }
}
