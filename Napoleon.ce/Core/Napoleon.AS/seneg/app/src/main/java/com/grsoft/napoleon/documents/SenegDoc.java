package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.SenegOutputDocImpl;

public class SenegDoc extends DocType {
    public static SenegDoc instance;
    public static SenegDoc instance() {
        if(instance == null)
            instance = new SenegDoc();
        return instance;
    }

    SenegDoc() {
        super("SenegOutputDoc", "SenegOutputDoc", SenegOutputDocImpl.class);
    }

    @Override
    protected void updateTodayDocs() {
        super.updateTodayDocs();
        TodayHelper.addRootOrgs(todays);
    }
}
