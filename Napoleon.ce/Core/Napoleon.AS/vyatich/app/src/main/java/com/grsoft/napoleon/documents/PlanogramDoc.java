package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.PlanogramDocImpl;
import com.grsoft.napoleon.R;

public class PlanogramDoc extends DateDocType {
    static PlanogramDoc instance = null;

    static public PlanogramDoc instance() {
        if(instance == null) {
            instance = new PlanogramDoc();
        }
        return instance;
    }

    PlanogramDoc() {
        super("PlanogramDoc", "PlanogramDoc", PlanogramDocImpl.class);
    }

    @Override public int getDocTitle() { return R.string.planogram; }
    @Override public int getResurceId() { return R.drawable.planogram_doc; }
    @Override public int getResurce2Id() { return R.drawable.planogram_doc_2; }
}
