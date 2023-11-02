package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.RemnantsEx;
import com.grsoft.util.MatrixItemsAdapter;

public class RemnantsImplEx extends RemnantsImpl{
    @Override
    public void postInit() {
        super.postInit();

        OrgImpl oi = new OrgImpl();
        oi.read("id", getId());
        OrgEx oe = (OrgEx) oi.getData();
        MatrixImpl mi = new MatrixImpl();
        if(mi.read("name", oe.formatTT))
            ((RemnantsEx)getData()).plan = mi.getData().items.size();
    }
}
