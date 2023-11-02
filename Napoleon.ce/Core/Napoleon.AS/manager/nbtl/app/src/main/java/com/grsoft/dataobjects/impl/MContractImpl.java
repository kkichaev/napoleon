package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Contract;
import com.grsoft.manager.ContractDetail;
import com.grsoft.napoleon.documents.CreatableDocument;

public class MContractImpl extends MOrderImplBase<Contract> {
    @Override
    public void open(Context context) {
        ContractDetail.open(context, this);
    }
}
