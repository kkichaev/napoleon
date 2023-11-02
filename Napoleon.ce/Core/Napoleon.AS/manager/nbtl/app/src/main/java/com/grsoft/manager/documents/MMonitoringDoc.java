package com.grsoft.manager.documents;

import com.grsoft.dataobjects.impl.MMonitoringImpl;
import com.grsoft.manager.R;

public class MMonitoringDoc extends MDocType {
    static MMonitoringDoc instance = null;
    static final String OBJ_NAME = "CMonitoring";

    MMonitoringDoc() {
        super(OBJ_NAME, MMonitoringImpl.class);
    }

    static public MDocType instance() {
        if( instance == null )
            instance = new MMonitoringDoc();
        return instance;
    }

    @Override
    public int getDocTitle() {
        return R.string.monitoring_doc_title;
    }
}
