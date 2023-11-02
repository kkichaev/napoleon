package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.VisitDoc;

public class VisitImplEx extends VisitImpl {
    @Override
    public void open(Context context) {
        DocType.setCurDoc(VisitDoc.instance());
        super.open(context);
    }
}
