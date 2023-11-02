package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.PlanogramDoc;
import com.grsoft.napoleon.PlanogramEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

public class PlanogramDocImpl extends CreatableDocument<PlanogramDoc> {
    @Override
    public void open(Context context) {
        PlanogramEdit.open(context, this);
    }

    @Override public boolean isEmpty() { return super.isEmpty() || data.planogram.length() == 0; }
}
