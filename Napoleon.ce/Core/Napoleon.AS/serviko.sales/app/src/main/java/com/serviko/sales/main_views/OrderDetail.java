package com.serviko.sales.main_views;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.serviko.dataobjects.Order;
import com.serviko.dataobjects.OrderItem;
import com.serviko.sales.MainActivity;
import com.serviko.sales.R;

public class OrderDetail extends BaseView {

    public static String TAG = OrderDetail.class.toString();
    View v;


    @Override
    int getResourceId() { return R.layout.order_view; }

    @Override
    public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);

        model.getOrder().observe(getViewLifecycleOwner(), o -> { refreshOrder(o); });

        v.findViewById(R.id.back).setOnClickListener(view -> getParentFragmentManager().popBackStack() );
        v.findViewById(R.id.copy).setOnClickListener(this::copyOrder);
        return v;
    }

    void copyOrder(View v) {
        model.getBasket().setFrom(model.getOrder().getValue());
        ((MainActivity) getActivity()).openItem(R.id.itBasket);
    }

    void refreshOrder(Order o) {
        TextView tv = v.findViewById(R.id.tvNumber);
        tv.setText(o.text);

        ((ListView)v.findViewById(R.id.lvItems)).setAdapter(new Adapter(o));
    }

    class Adapter extends BaseAdapter {
        Order order;
        public Adapter(Order o) {
            this.order = o;
        }

        @Override
        public int getCount() { return order.items.size(); }

        @Override
        public Object getItem(int i) { return order.items.get(i); }

        @Override
        public long getItemId(int i) { return i; }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                view = View.inflate(getContext(), R.layout.order_view_row, null);
            }
            OrderItem oi = (OrderItem) getItem(i);
            TextView tv;
            tv = view.findViewById(R.id.tvName);
            tv.setText(oi.name);

            tv = view.findViewById(R.id.tvSum);
            String text = String.format("%.2f &#x20bd", oi.sumFact);
            tv.setText(Html.fromHtml(text));

            tv = view.findViewById(R.id.tvQty);
            tv.setText(Integer.toString((int)(oi.qty + 0.001)));

            view.setBackgroundResource((i % 2) == 0 ? R.drawable.order_row_even : R.drawable.order_row_odd);
            return view;
        }
    }
}
