package com.grsoft.napoleon;

import android.view.View;
import android.widget.BaseAdapter;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;

public class WarehouseEx extends Warehouse {
    @Override
    protected void createDocument() {
        document = DocType.getCurDoc().create();
        if (!(document instanceof Itemsable))
            document = RemnantsDoc.instance().create();
    }

    @Override
    protected BaseAdapter createListAdapter() {
        FoldersAdapter res = (FoldersAdapter) super.createListAdapter();

        if (DocType.getCurDoc() == RemnantsDoc.instance())
            res.putFilter(new Filter("DocFilter") {
                @Override
                public boolean inset(long priceRowID, String id) {
                    price.read(priceRowID);
                    PriceEx pe = (PriceEx) price.getData();

                    return pe.docFilter == 1 || pe.docFilter == 2;
                }
            });
        return res;
    }

    @Override
    protected void updateTotalSum() {
        if(document instanceof RemnantsImpl) {
            findViewById(R.id.tvTotalSum).setVisibility(View.GONE);
        } else
            super.updateTotalSum();
    }
}
