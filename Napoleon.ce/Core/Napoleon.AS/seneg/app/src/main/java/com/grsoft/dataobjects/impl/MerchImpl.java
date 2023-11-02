package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.VisitEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.MerchDoc;

public class MerchImpl extends VisitImpl {

    @Override
    public void open(Context context) {
        DocType.setCurDoc(MerchDoc.instance());
        super.open(context);
    }

    @Override
    public void postInit() {
        super.postInit();
        ((VisitEx)data).docType = VisitEx.MERCH_TYPE;
    }
}
