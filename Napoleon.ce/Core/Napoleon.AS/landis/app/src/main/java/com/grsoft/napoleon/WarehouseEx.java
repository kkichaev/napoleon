package com.grsoft.napoleon;

import android.widget.BaseAdapter;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;

public class WarehouseEx extends Warehouse {

    PriceImpl pi = new PriceImpl();

    @Override
    protected BaseAdapter createListAdapter() {
        FoldersAdapter fa = (FoldersAdapter) super.createListAdapter();
        if(document instanceof OrderImpl)
            fa.putFilter(new CostFilter(document, pi));
        return fa;
    }

    @Override
    protected void onStop() {
        super.onStop();
        pi.close();
    }

    public static class CostFilter extends Filter {
        PriceImpl pi;
        Document<?> doc;
        public CostFilter(Document<?> doc, PriceImpl pi) {
            super("CostFilter" + doc.getId());
            this.doc = doc;
            this.pi = pi;
        }

        @Override
        public boolean inset(long priceRowID, String id) {
            if(super.inset(priceRowID, id)) {
                pi.read(priceRowID);
                return CostStrategy.defaultInstance.getItemCost(pi.getData(), doc)> 0;
            }
            return false;
        }
    }
}
