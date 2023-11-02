package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Target;
import com.grsoft.napoleon.TargetEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

public class TargetImpl extends CreatableDocument<Target> {
    @Override
    public void open(Context context) {
        if (isEditable())
            TargetEdit.open(context, getRowid());
    }

    public boolean isClosed(){
        return isProceeded() || data.closed != 0;
    }

    @Override
    public boolean isEditable() {
        return super.isEditable() && !isClosed();
    }
}
