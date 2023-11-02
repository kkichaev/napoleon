package com.grsoft.util;

import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.napoleon.Warehouse;

import java.util.List;

public class OrgMatrixAdapter extends MatrixBaseAdapter{

    OrgMatrix src;
    public OrgMatrixAdapter(Warehouse owner, OrgMatrix src) {
        super(owner);
        this.src = src;
    }

    @Override
    public String getName() {
        return "OrgMatrix" + src.id;
    }

    @Override
    protected List<? extends MatrixItem> getMatrixItems() {
        return src.items;
    }
}
