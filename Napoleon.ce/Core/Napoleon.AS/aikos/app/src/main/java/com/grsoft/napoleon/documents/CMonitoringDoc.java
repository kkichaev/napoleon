package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.MonitoringImpl;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.modules.MonitoringInit;

public class CMonitoringDoc extends DateDocType{
    static CMonitoringDoc instance;

    protected CMonitoringDoc() {
        super("Мониторинг", "CMonitoring", MonitoringImpl.class);
    }

    public static DocType instance() {
        if( instance == null ) {
            instance = new CMonitoringDoc();
        }
        return instance;
    }

    @Override public int getResurceId() { return R.drawable.monitor_doc; }
    @Override public int getResurce2Id() { return R.drawable.monitor_doc_2; }
}
