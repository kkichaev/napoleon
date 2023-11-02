package com.grsoft.dataobjects.impl;
import com.grsoft.aceteam.R;

import android.content.Context;

import com.grsoft.dataobjects.ScanLocation;
import com.grsoft.napoleon.ScanLocationEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

public class ScanLocationImpl extends CreatableDocument<ScanLocation> {
    @Override
    public void open(Context context) {
        ScanLocationEdit.open(context, getRowid());
    }

    @Override
    public void postInit() {
        data.latitude = 0;
        data.longitude = 0;
    }
}
