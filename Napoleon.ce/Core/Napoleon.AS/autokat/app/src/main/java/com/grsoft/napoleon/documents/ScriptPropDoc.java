package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.ScriptPropImpl;
import com.grsoft.network.DocExportListener;

public class ScriptPropDoc extends DocType {
    static ScriptPropDoc instance;

    public static ScriptPropDoc instance() {
        if(instance == null)
            instance = new ScriptPropDoc();
        return instance;
    }

    ScriptPropDoc() {
        super("", "ScriptPropDoc", ScriptPropImpl.class);
    }

    @Override
    public DocExportListener getDirtyDocuments() {
        return null;
    }
}
