package com.grsoft.napoleon;

import android.widget.BaseAdapter;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;

public class WarehouseEx extends Warehouse {
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter adapter = (FoldersAdapter) super.createListAdapter();
//		} else if(document instanceof SalesImplEx) {
//			SalesEx oe = (SalesEx)document.getData();
//			adapter.putFilter(new StoreFilter(oe.storeid));
//			if(oe.tabak >= 0)
//				adapter.putFilter(new TabakFilter("Tabak", oe.tabak));
//		}
		return adapter;
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