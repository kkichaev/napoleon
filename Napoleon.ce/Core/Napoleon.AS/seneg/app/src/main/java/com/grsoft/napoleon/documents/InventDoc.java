package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.InventImpl;
import com.grsoft.napoleon.R;

public class InventDoc extends OrderDoc {

    static InventDoc docInstance = null;

    public static InventDoc instance() {
        if(docInstance == null)
            docInstance = new InventDoc();
        return docInstance;
    }

    InventDoc() {
        super("Инвентаризация","InventDoc", InventImpl.class);
    }

    @Override
    protected void updateTodayDocs() {
        super.updateTodayDocs();
        TodayHelper.addRootOrgs(todays);
    }


    @Override
    public int getResurceId() {
        return R.drawable.invent;
    }

    @Override
    public int getDocTitle() {
        return R.string.invent;
    }

    @Override
    public int getResurce2Id() {
        return R.drawable.invent_2;
    }

}
