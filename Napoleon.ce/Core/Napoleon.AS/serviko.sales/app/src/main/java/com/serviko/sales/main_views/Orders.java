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
import com.serviko.sales.MainActivity;
import com.serviko.sales.R;
import com.serviko.sales.main_views.order_filter.FilterView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Orders extends BaseView implements ChildFilterFragment.Handler {

    public static String TAG = Orders.class.toString();

    Adapter adapter;

    Fragment filter;

    @Override
    int getResourceId() { return R.layout.orders_view; }

    @Override
    public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        v.findViewById(R.id.back).setOnClickListener(view -> getParentFragmentManager().popBackStack());
        ListView items = v.findViewById(R.id.lvItems);

        adapter = new Adapter();
        items.setAdapter(adapter);
        items.setOnItemClickListener((adapterView, view, i, l) -> {
            Order o = (Order) adapter.getItem(i);
            if(o != null) {
                closeFilter();
                model.setOrder(o);
                ((MainActivity)getActivity()).loadFragment(new OrderDetail(), true);
            }
        });

        v.findViewById(R.id.filter).setOnClickListener(view -> filtering(true));
        model.getPartner().observe(getViewLifecycleOwner(), partner -> { adapter.refresh(); });
        return v;
    }

    @Override
    public void backing() { filtering(false); }

    public void closeFilter() {
        if(filter != null) {
            getChildFragmentManager().beginTransaction()
                    .remove(filter)
                    .commit();
            filter = null;
        }
    }

    public void filtering(boolean closeIfPresents) {
        if(!closeIfPresents || filter == null) {
            filter = new FilterView(closeIfPresents);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.filter_fragment, filter)
                    .commit();
        } else {
            filterOrders();
//            closeFilter();
        }
//        new OrderFilterDlg().show(getParentFragmentManager(), "");
    }

    public void filterOrders() {
        adapter.refresh();
        closeFilter();
    }

    public void filterSetFragment(Fragment newFragment) {
        filter = newFragment;
        getChildFragmentManager().beginTransaction()
                .replace(R.id.filter_fragment, filter)
                .commit();
    }

    class Adapter extends BaseAdapter {

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        List<Order> orders = new ArrayList<>();
        public Adapter() {
            refresh();
        }

        public void refresh() {
            orders.clear();
            for(Order ord : model.getPartner().getValue().orders) {
                if(model.orderFilter.inSet(ord)) {
                    orders.add(ord);
                }
            }

            Collections.sort(orders);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() { return orders.size(); }

        @Override
        public Object getItem(int i) { return orders.get(i); }

        @Override
        public long getItemId(int i) { return i; }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                view = View.inflate(getContext(), R.layout.orders_view_row, null);
            }
            Order o = (Order) getItem(i);
            TextView tv;
            tv = view.findViewById(R.id.tvNumber);
            tv.setText(o.number);

            tv = view.findViewById(R.id.tvState);
            tv.setText(o.getStateText(getContext()));

            tv = view.findViewById(R.id.tvDate);
            tv.setText(sdf.format(o.orderDate));

            String text = String.format("%.2f &#x20bd", o.sumFact());
            tv = view.findViewById(R.id.tvSum);
            tv.setText(Html.fromHtml(text));

            int rs = o.isUnpayed() ? R.string.order_unpayed : R.string.order_payed;
            tv = view.findViewById(R.id.tvPayed);
            tv.setText(rs);

            view.setBackgroundResource((i % 2) == 0 ? R.drawable.order_row_even : R.drawable.order_row_odd);
            return view;
        }
    }
}
