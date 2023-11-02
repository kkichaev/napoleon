package com.grsoft.napoleon;

import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;

public class WarehouseEx extends Warehouse {
    @Override
    protected FoldersAdapter createAdapterInstance() {
        FoldersAdapter ret = super.createAdapterInstance();
        if(document instanceof SalesImplEx && ((SalesEx)document.getData()).tabak >= 0)
            ret.putFilter(new TabakFilter("Tabak", ((SalesEx)document.getData()).tabak));
        return ret;
    }
}

class TabakFilter extends Filter {

    int tabak;
    public TabakFilter(String name, int value) {
        super(name + Integer.toString((value)));
        tabak = value;
    }

    @Override
    public String getWhereStr() {
        return "tabak=" + Integer.toString(tabak);
    }
}