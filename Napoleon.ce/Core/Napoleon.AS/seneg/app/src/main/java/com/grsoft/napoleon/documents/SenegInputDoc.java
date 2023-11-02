package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.SenegInputDocImpl;

public class SenegInputDoc extends DocType {
    static SenegInputDoc instance = null;
    public static SenegInputDoc instance() {
        if(instance == null) {
            instance = new SenegInputDoc();
        }
        return instance;
    }

    SenegInputDoc() {
        super("SenegInputDoc", "SenegInputDoc", SenegInputDocImpl.class);
    }

    @Override
    protected void updateTodayDocs() {
        super.updateTodayDocs();
        TodayHelper.addRootOrgs(todays);
    }
}
