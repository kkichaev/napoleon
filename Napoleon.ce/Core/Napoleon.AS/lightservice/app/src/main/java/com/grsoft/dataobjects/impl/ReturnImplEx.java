package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.napoleon.ReturnCount;

public class ReturnImplEx extends ReturnImpl{
    @Override
    public void editItem(long itemRowid, Context context) {
        ReturnCount.open(context, itemRowid, this);
    }
}
