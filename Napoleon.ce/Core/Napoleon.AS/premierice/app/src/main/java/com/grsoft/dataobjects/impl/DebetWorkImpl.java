package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.DebetWork;
import com.grsoft.napoleon.DebetWorkEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DebetWorkDoc;

public class DebetWorkImpl extends CreatableDocument<DebetWork> {
    @Override
    public void open(Context context) {
        DebetWorkEdit.open(context, this);
    }

    @Override
    public boolean isEmpty() {
        return data.remark.length() == 0;
    }


    @Override
    public long write() {
        long res = super.write();
        DebetWorkDoc.instance().refreshDocSum(data.id);
        return res;
    }
}
