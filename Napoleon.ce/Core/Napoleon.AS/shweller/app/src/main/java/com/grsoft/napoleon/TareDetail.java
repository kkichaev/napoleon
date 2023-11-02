package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgTare;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.TareImpl;
import com.grsoft.util.ExtrasConst;

import java.util.HashMap;
import java.util.Map;

public class TareDetail extends OrderDetail {
    Map<String, OrgTare> tares = new HashMap<>();

    public static void open(Context context, TareImpl doc) {
        Intent i = new Intent(context, TareDetail.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
        context.startActivity(i);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.tare_detail);
    }

    @Override
    public void updateTotalSum(long sum, int weight, int count) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OrgImpl oi = new OrgImpl();
        OrgEx oe = (OrgEx) oi.getData();
        oe.id = doc.getId();
        oi.read();
        oi.close();

        btnAddItems.setOnClickListener(v -> {
            OrgTares.open(TareDetail.this, (TareImpl) doc);
        });

        for(OrgTare ot : oe.tare) {
            tares.put(ot.id, ot);
        }

        findViewById(R.id.btnEditOrder).setVisibility(View.GONE);
        findViewById(R.id.tvTotalSum).setVisibility(View.GONE);

        if(doc.isEditable() && doc.isEmpty()) {
            OrgTares.open(TareDetail.this, (TareImpl) doc);
        }
    }

    @Override
    protected void setAdapter() {
        lvItems.setAdapter(new Adapter());
    }

    class Adapter extends OrderItemsAdapter {
        @Override
        public View getView(int pos, View arg1, ViewGroup arg2) {
            OrderItem item = (OrderItem) getItem(pos);
            OrgTare ot = tares.get(item.id);
            String text = ( ot == null ) ? "< " + getString(R.string.id) + " '" + item.id + "' >" : ot.name;

            View view = arg1;
            if (view == null)
                view = View.inflate(TareDetail.this, R.layout.tare_detail_row, null);

            view.setTag(item);
            view.findViewById(R.id.tvName).setTag(item.id);
            TextView tv;
            tv = view.findViewById(R.id.tvName);
            tv.setText(text);

            text = (ot==null) ? "" : ot.number;
            tv = view.findViewById(R.id.tvNumber);
            tv.setText(text);

            return view;
        }
    }
}
