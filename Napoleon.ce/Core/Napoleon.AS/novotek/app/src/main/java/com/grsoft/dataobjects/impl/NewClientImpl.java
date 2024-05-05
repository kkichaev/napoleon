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

    @Override
    public boolean isEmpty() {
        return data.profile.length() == 0 || data.typeTT.length() == 0 || data.salesChannel.length() == 0 || emptyLocation();
    }

    public boolean emptyLocation() {
        return data.latitude == 0;
    }
}
