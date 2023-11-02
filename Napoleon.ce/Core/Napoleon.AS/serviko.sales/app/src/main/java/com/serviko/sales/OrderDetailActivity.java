package com.serviko.sales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.serviko.dataobjects.Order;
import com.serviko.dataobjects.OrderItem;
import com.serviko.dataobjects.Partner;
import com.serviko.dataobjects.PartnerList;

public class OrderDetailActivity extends BaseActivityOld {
    static final String ORDER_TAG = "orderTag";

    Order order;

    public static void open(Context context, Order order) {
        Intent i = new Intent(context, OrderDetailActivity.class);
        i.putExtra(ORDER_TAG, order.uid);
        context.startActivity(i);
    }

    @Override protected int getLayoutID() { return R.layout.order_items; }
    @Override protected int getBottomMenuID() { return 0; } //R.id.itOrder; }
    @Override protected void refreshPartner() { super.onPartnerSelect(PartnerList.getCurrent()); }
    @Override protected void onPartnerSelect(Partner newPartner) { }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PartnerList.removeHandler(selectPartner);

        Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        String uid = b.getString(ORDER_TAG);

        order = PartnerList.getCurrent().getOrder(uid);
        MaterialToolbar mtb = findViewById(R.id.topAppBar);
        mtb.setTitle(order.text);
        mtb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });

        Adapter adapter = new Adapter();
        ListView lv = findViewById(R.id.lvItems);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORDER_TAG, order.uid);
    }

    class Adapter extends BaseAdapter {

        @Override public int getCount() { return order.items.size(); }
        @Override public Object getItem(int position) { return order.items.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null)
                view = View.inflate(OrderDetailActivity.this, R.layout.order_item_row, null);

            OrderItem oi = (OrderItem) getItem(position);
            TextView tv;
            tv = view.findViewById(R.id.tvName);
            tv.setText(oi.name);

            String text;
            text = String.format("%d רע. %.2f &#x20bd", (int)(oi.qty + 0.005), oi.sum);
            tv = view.findViewById(R.id.tvPlan);
            tv.setText(Html.fromHtml(text));

            text = String.format("%d רע. %.2f &#x20bd", (int)(oi.qtyFact + 0.005), oi.sumFact);
            tv = view.findViewById(R.id.tvFact);
            tv.setText(Html.fromHtml(text));
            return view;
        }
    }
}
