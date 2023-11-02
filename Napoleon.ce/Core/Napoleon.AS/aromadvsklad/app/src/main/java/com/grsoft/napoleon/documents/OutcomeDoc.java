package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.OutcomeImpl;
import com.grsoft.network.DocExportListener;

public class OutcomeDoc extends DocType {
    static OutcomeDoc instance;
    public static OutcomeDoc instance() {
        if(instance == null)
            instance = new OutcomeDoc();
        return instance;
    }

    OutcomeDoc() {
        super("OutDoc", "OutDoc", OutcomeImpl.class);
    }

    @Override
    public DocExportListener getDirtyDocuments() {
        String where = "(compleete = 1) and ((params & " + Integer.toString(ParamState.ofExported) + ") = 0)";
        return new DocSendListner(getObjectName(), OutcomeImpl.class, where);
    }
}
