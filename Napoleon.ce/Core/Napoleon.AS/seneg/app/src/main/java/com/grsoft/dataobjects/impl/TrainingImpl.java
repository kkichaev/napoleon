package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.VisitEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.TrainingDoc;

public class TrainingImpl extends VisitImpl {

    @Override
    public void open(Context context) {
        DocType.setCurDoc(TrainingDoc.instance());
        super.open(context);
    }

    @Override
    public void postInit() {
        super.postInit();
        ((VisitEx)data).docType = VisitEx.TRAINING_TYPE;
    }

    @Override
    public boolean isEmpty() {
        return data.remark.length() == 0 && (data.items == null || data.items.size() < 4);
    }
}
