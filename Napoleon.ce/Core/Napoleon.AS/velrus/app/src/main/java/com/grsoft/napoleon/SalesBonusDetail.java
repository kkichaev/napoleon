package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesBonusDoc;
import com.grsoft.napoleon.printsources.SalesBounsPrint;
import com.grsoft.napoleon.printsources.SalesPrint;
import com.grsoft.napoleon.printsources.SalesSource;
import com.grsoft.util.ExtrasConst;

import java.lang.reflect.InvocationTargetException;

public class SalesBonusDetail extends SalesDetail {
    static public void open(Context context, OrderImplBase<? extends Order> order) {
        Intent i = new Intent(context, SalesBonusDetail.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setSalesDoc() {
        DocType.setCurDoc(SalesBonusDoc.instance());
    }

    @Override
    protected String[] createPrintCaption() {
        return new String[] { "Накладная" };
    }

    @Override
    protected SalesSource createPrintSource(Sales sdoc) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        return new SalesSource(new SalesBounsPrint(sdoc));
    }
}
