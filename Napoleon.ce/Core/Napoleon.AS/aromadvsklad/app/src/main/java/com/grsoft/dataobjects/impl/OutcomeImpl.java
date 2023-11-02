package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Outcome;
import com.grsoft.napoleon.OutcomeEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

public class OutcomeImpl extends CreatableDocument<Outcome> {
    @Override
    public void open(Context context) {
        OutcomeEdit.open(context, data.id, data.number);
    }
}
