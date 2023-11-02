package com.grsoft.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DistirbItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.MOrderImplBase;
import com.grsoft.manager.documents.MDistribDoc;

public class DistribDetail extends OrderDetail {
    public static void open(Context context, MOrderImplBase<? extends Order> doc) {
        Intent intent = new Intent(context, DistribDetail.class);

        intent.putExtra(DocDetailDecorator.DOCTYPE, doc.getClass());
        intent.putExtra(DocDetailDecorator.ROWID, doc.getRowid());

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.tvQtyTitle).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.tvCost)).setText("Наличие");
    }

    @Override
    public String getTitle(CreateDocDataObject exdata) {
        return getString(MDistribDoc.instance().getDocTitle());
    }

    @Override
    protected View getItemView(View view, OrderItem item) {
        if(view == null)
            view = View.inflate(DistribDetail.this, R.layout.distr_row, null);

        ((TextView) view.findViewById(R.id.tvName)).setText(priceName(item.id));
        CheckBox cb = view.findViewById(R.id.cnExists);
        cb.setChecked( ((DistirbItem)item).exist > 0);
        return view;
    }
}
