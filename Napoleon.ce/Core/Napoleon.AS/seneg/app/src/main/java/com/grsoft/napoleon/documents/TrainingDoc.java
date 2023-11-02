package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.impl.MerchImpl;
import com.grsoft.dataobjects.impl.TrainingImpl;
import com.grsoft.napoleon.R;
import com.grsoft.network.DocExportListener;

public class TrainingDoc extends VisitDoc {
    static TrainingDoc docInstance = null;

    public static TrainingDoc instance() {
        if(docInstance == null)
            docInstance = new TrainingDoc();
        return docInstance;
    }

    TrainingDoc() {
        super("Обучение","TrainingDoc", TrainingImpl.class);
    }

    @Override
    public DocExportListener getDirtyDocuments() { return null; }

    @Override
    public DocList docList(String orgId, String order, String where) {
        if(where == null)
            where = new String();
        else if(where.length() > 0)
            where += " and ";
        where += "docType = " + Integer.toString(VisitEx.TRAINING_TYPE);
        return super.docList(orgId, order, where);
    }

    @Override
    public int getResurceId() {
        return R.drawable.training;
    }

    @Override
    public int getDocTitle() {
        return R.string.training;
    }

    @Override
    public int getResurce2Id() {
        return R.drawable.training_2;
    }

    @Override
    protected void updateTodayDocs() {
        super.updateTodayDocs();
        TodayHelper.addRootOrgs(todays);
    }
}
