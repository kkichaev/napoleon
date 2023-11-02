package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Distrib;
import com.grsoft.manager.DistribDetail;

public class MDistribImpl extends MOrderImplBase<Distrib> {
    @Override
    public void open(Context context) {
        DistribDetail.open(context, this);
    }
}
