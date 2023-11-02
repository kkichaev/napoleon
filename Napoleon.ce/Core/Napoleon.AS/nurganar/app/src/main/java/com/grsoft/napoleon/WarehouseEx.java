package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;

public class WarehouseEx extends Warehouse{
    @Override
    protected void postAdapterInit() {
        if (document.getRowid() != ExtrasConst.INVALID_ROWID && DocType.getCurDoc() == OrderDoc.instance()){
            Filter flt = new Filter("TARE_TYPE"){
                @Override
                public String getWhereStr() {
                    return String.format("tareType=%d", ((OrderEx)document.getData()).tareType);
                }
            };

            adapter.putFilter(flt);
        }

        super.postAdapterInit();
    }
}
