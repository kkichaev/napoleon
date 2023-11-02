package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.NewClient;
import com.grsoft.napoleon.NewClientEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

public class NewClientImpl extends CreatableDocument<NewClient> {
    @Override
    public void open(Context context) {
        NewClientEdit.open(context, getRowid());
    }
}
