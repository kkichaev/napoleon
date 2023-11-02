package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.InventDoc;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.ExtrasConst;

public class InventDetail extends OrderDetailEx {
    static public void open(Context context, OrderImplBase<? extends Order> order) {
        Intent i = new Intent(context, InventDetail.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
        context.startActivity(i);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.inventdetail);
    }

    @Override
    protected DocType getDocType() {
        return InventDoc.instance();
    }

    @Override
    protected void updateTotalSum() {
        updateTotalSum(0, 0, doc.count());
    }

    @Override
    protected void setAdapter() {
        lvItems.setAdapter(new Adapter());
    }

    class Adapter extends OrderItemsAdapter {
        @Override
        protected void drawInternal(View view, String name, int color, OrderItem item) {
            super.drawInternal(view, name, color, item);
            view.findViewById(R.id.tvSum).setVisibility(View.GONE);
        }
    }
}
