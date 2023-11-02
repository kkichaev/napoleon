package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.impl.VisitImplEx;

public class VisitDocEx extends VisitDoc {
    public static void init() {
        instance = new VisitDocEx();
    }

    VisitDocEx() {
        super(DOC_NAME, OBJ_NAME, VisitImplEx.class);
    }

    @Override
    public DocList docList(String orgId, String order, String where) {
        if(where == null)
            where = new String();
        else if(where.length() > 0)
            where += " and ";
        where += "docType = " + Integer.toString(VisitEx.VISIT_TYPE);
        return super.docList(orgId, order,  where);
    }

    @Override
    protected void updateTodayDocs() {
        super.updateTodayDocs();
        TodayHelper.addRootOrgs(todays);
    }
}
