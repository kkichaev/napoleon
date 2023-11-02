package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.BankIncass;
import com.grsoft.dataobjects.PicStore;
import com.grsoft.napoleon.BankIncassEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

public class BankIncassImpl extends CreatableDocument<BankIncass> {
    @Override
    public void open(Context context) {
        BankIncassEdit.open(context, this);
    }

    @Override
    public boolean isEmpty() {
        return data.sum == 0 || data.picture.length() == 0;
    }

    @Override
    public boolean delete() {
        if(!super.delete())
            return false;

        PicStoreImpl.delete(data.picture);
        return true;
    }
}
