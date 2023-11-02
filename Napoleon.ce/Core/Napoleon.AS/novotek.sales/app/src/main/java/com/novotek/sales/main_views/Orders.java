package com.novotek.sales.main_views;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novotek.dataobjects.Order;
import com.novotek.dataobjects.Partner;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Orders extends BaseView  {

    static final int MODE_ALL = 0;
    static final int MODE_ACTIVE = 1;
    static final int MODE_FINISHED = 2;

    int viewMode = MODE_ALL;
    Adapter adapter;
    View v;

    public static String TAG = Orders.class.toString();

    @Override
    protected int getResourceId() { return R.layout.orders_view; }

    @Override
    public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);

        RecyclerView rv = v.findViewById(R.id.orders);

        TextView active = v.findViewById(R.id.active);
        TextView finished = v.findViewById(R.id.finished);
        active.setOnClickListener(view -> {
            if(viewMode == MODE_ACTIVE) {
                viewMode = MODE_ALL;
            } else {
                viewMode = MODE_ACTIVE;
            }
            updateMode(active, finished);
        });
        finished.setOnClickListener(view -> {
            if(viewMode == MODE_FINISHED) {
                viewMode = MODE_ALL;
            } else {
                viewMode = MODE_FINISHED;
            }
            updateMode(active, finished);
        });

        model.getOrders().observe(getViewLifecycleOwner(), src -> {
            int pos = 0;
            if(rv.getLayoutManager() != null) {
                pos = ((LinearLayoutManager) rv.getLayoutManager()).findFirstVisibleItemPosition();
            }
            adapter = new Adapter(src);
            rv.setAdapter(adapter);
            LinearLayoutManager lmm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rv.setLayoutManager(lmm);
            lmm.scrollToPosition(pos);
        });

        model.getRequestError().observe(getViewLifecycleOwner(), err -> {
            if(err != null)
                Toast.makeText(getContext(), err.message, Toast.LENGTH_LONG).show();
        });

        model.getRequestInProgress().observe(getViewLifecycleOwner(), progress -> {
            v.findViewById(R.id.llWait).setVisibility(progress ? View.VISIBLE : View.GONE);
        });

        model.getCancelResult().observe(getViewLifecycleOwner(), res -> {
            if(res != null) {
                if (res.result == 0) {
                    Toast.makeText(getContext(), res.message, Toast.LENGTH_LONG).show();
                } else {
                    int pos = adapter.getItemPos(res.doc);
                    if (pos >= 0) {
                        adapter.notifyItemChanged(pos, res.doc);
                    }
                }
                model.clearCancelResult();
            }
        });

        v.findViewById(R.id.btn1).setOnClickListener(view -> ((MainActivity)getActivity()).openItem(R.id.itCatalog));
        v.findViewById(R.id.btn2).setOnClickListener(view -> ((MainActivity)getActivity()).openItem(R.id.itCatalog));
        return v;
    }

    void updateMode(TextView active, TextView finished) {
        Resources r = getResources();

        active.setBackgroundResource(viewMode == MODE_ACTIVE ? R.drawable.rounded_order_primary :
                R.drawable.rounded_order_gray);
        finished.setBackgroundResource(viewMode == MODE_FINISHED ? R.drawable.rounded_order_primary :
                R.drawable.rounded_order_gray);

        active.setTextColor(r.getColor(viewMode == MODE_ACTIVE ? R.color.white : R.color.black, null));
        finished.setTextColor(r.getColor(viewMode == MODE_FINISHED ? R.color.white : R.color.black, null));

        adapter.refresh();
        v.findViewById(R.id.no_orders).setVisibility(adapter.getItemCount() == 0 && viewMode != MODE_FINISHED ?
                View.VISIBLE : View.GONE);
        v.findViewById(R.id.no_orders_finished).setVisibility(adapter.getItemCount() == 0 && viewMode == MODE_FINISHED ?
                View.VISIBLE : View.GONE);
    }

    class Holder extends RecyclerView.ViewHolder {

        public Holder(@NonNull View itemView) {
            super(itemView);
        }

        public void update(Order o) {
            SimpleDateFormat src = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat dest = new SimpleDateFormat("dd MMMM");

            TextView tv = itemView.findViewById(R.id.order_number);
            tv.setText(getString(R.string.order_no, o.id));

            o.updateStatusText(itemView.findViewById(R.id.order_status));

            itemView.findViewById(R.id.order_body).setOnClickListener(view -> ((MainActivity)getActivity()).openOrder(o));

            tv = itemView.findViewById(R.id.order_info);
            tv.setText(getString(R.string.order_info, o.count(), o.sum()));
            try {
                Date dlvDate = src.parse(o.dateDelivery);
                String dstr = dest.format(dlvDate);
                ((TextView)itemView.findViewById(R.id.order_dlv_info)).setText(dstr);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            TextView b = itemView.findViewById(R.id.button1);
            int vsbl = View.VISIBLE;
            if(o.canCancel()) {
                b.setText(R.string.cancel);
                b.setOnClickListener(view -> model.cancelOrder(o, getContext()));
            } else if(o.canDelete()) {
                b.setText(R.string.delete_order);
                b.setOnClickListener(view -> model.deleteOrder(o));
            } else {
                vsbl = View.INVISIBLE;
            }
            b.setVisibility(vsbl);

            View v = itemView.findViewById(R.id.button2);
            if(o.canCopy()) {
                v.setVisibility(View.VISIBLE);
                v.setOnClickListener(view -> {
                    model.copyOrder(o);
                    ((MainActivity)getActivity()).openItem(R.id.itBasket);
                });
            } else {
                v.setVisibility(View.INVISIBLE);
            }
        }
    }

    class Adapter extends RecyclerView.Adapter<Holder> {

        List<Order> orders = new ArrayList<>();
        List<Order> src;

        public Adapter(List<Order> src) {
            this.src = src;
            refresh();
        }

        public int getItemPos(Order o) { return orders.indexOf(o); }

        public void refresh() {
            orders = new ArrayList<>();
            for(Order o : src) {
                if(viewMode == MODE_ALL || ((viewMode == MODE_ACTIVE && o.isActivew()) || (viewMode == MODE_FINISHED && !o.isActivew())) )
                    orders.add(o);
            }

            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.order_tile, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Order o = orders.get(position);
            holder.update(o);
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }
    }
}
