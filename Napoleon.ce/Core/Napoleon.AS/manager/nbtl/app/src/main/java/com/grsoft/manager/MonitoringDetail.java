package com.grsoft.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.MOrderImplBase;
import com.grsoft.manager.documents.MMonitoringDoc;

public class MonitoringDetail extends OrderDetail {
    public static void open(Context context, MOrderImplBase<? extends Order> doc) {
        Intent intent = new Intent(context, MonitoringDetail.class);

        intent.putExtra(DocDetailDecorator.DOCTYPE, doc.getClass());
        intent.putExtra(DocDetailDecorator.ROWID, doc.getRowid());

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.tvQtyTitle).setVisibility(View.GONE);
    }

    @Override
    public String getTitle(CreateDocDataObject exdata) {
        return getString(MMonitoringDoc.instance().getDocTitle());
    }

    @Override
    protected View getItemView(View view, OrderItem item) {
        View ret = super.getItemView(view, item);
        ret.findViewById(R.id.tvQty).setVisibility(View.GONE);
        return ret;
    }
}
