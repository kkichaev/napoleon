package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.impl.MerchImpl;
import com.grsoft.napoleon.R;
import com.grsoft.network.DocExportListener;

public class MerchDoc extends VisitDoc {
    static MerchDoc docInstance = null;

    public static MerchDoc instance() {
        if(docInstance == null)
            docInstance = new MerchDoc();
        return docInstance;
    }

    MerchDoc() {
        super("Ìונק.","MerchDoc", MerchImpl.class);
    }

    @Override
    public DocExportListener getDirtyDocuments() { return null; }

    @Override
    public int getResurceId() {
        return R.drawable.merch;
    }

    @Override
    public int getDocTitle() {
        return R.string.merch;
    }

    @Override
    public int getResurce2Id() {
        return R.drawable.merch_2;
    }

    @Override
    public DocList docList(String orgId, String order, String where) {
        if(where == null)
            where = new String();
        else if(where.length() > 0)
            where += " and ";
        where += "docType = " + Integer.toString(VisitEx.MERCH_TYPE);
        return super.docList(orgId, order, where);
    }

    @Override
    protected void updateTodayDocs() {
        super.updateTodayDocs();
        TodayHelper.addRootOrgs(todays);
    }
}
