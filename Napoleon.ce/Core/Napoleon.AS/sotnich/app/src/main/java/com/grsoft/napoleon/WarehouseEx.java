package com.grsoft.napoleon;

import android.widget.BaseAdapter;

import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.AssortmentMatrixAdapter;

public class WarehouseEx extends Warehouse{
    static final int PERIOD_FOR_DELIVERY = 12;

    protected AssortmentMatrixAdapter createAssortementMatrixAdapter() {
        if (DocType.getCurDoc() == ReturnDoc.instance()) {
            AssortmentMatrixAdapter.MATRIX_DOC = DeliveryDoc.instance();
            AssortmentMatrixAdapter.PERIOD_IN_MONTH = PERIOD_FOR_DELIVERY;
        }

        return new AssortmentMatrixAdapter(this, document.getId());
    }

    @Override
    protected BaseAdapter createListAdapter() {
        if( DocType.getCurDoc() == ReturnDoc.instance())
            return createAssortementMatrixAdapter();
        else
            return super.createListAdapter();
    }
}
