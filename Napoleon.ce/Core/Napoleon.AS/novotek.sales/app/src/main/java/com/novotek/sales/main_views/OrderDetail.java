package com.novotek.sales.main_views;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novotek.dataobjects.Order;
import com.novotek.dataobjects.OrderItem;
import com.novotek.dataobjects.Price;
import com.novotek.dataobjects.priceTree.PriceTree;
import com.novotek.sales.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderDetail extends BaseView {

    public static String TAG = OrderDetail.class.toString();
    View v;


    @Override
    protected int getResourceId() { return R.layout.order_view; }

    @Override
    public String getFragmentTag() { return TAG; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);

        model.getOrder().observe(getViewLifecycleOwner(), o -> { refreshOrder(o); });

        model.getCancelResult().observe(getViewLifecycleOwner(), r -> {
            if(r != null) {
                if(r.result == 0) {
                    Toast.makeText(getContext(), r.message, Toast.LENGTH_LONG).show();
                } else {
                    refreshOrder(r.doc);
                }
                model.clearCancelResult();
            }
        });

        v.findViewById(R.id.back).setOnClickListener(view -> getParentFragmentManager().popBackStack() );

        model.getRequestInProgress().observe(getViewLifecycleOwner(), progress -> {
            v.findViewById(R.id.llProgress).setVisibility(progress ? View.VISIBLE : View.GONE);
            v.findViewById(R.id.button1).setEnabled(!progress);
        });

        model.getRequestError().observe(getViewLifecycleOwner(), err -> {
            Toast.makeText(getContext(), err.message, Toast.LENGTH_LONG).show();
        });

        return v;
    }

    void refreshOrder(Order o) {
        if(o == null)
            return;

        SimpleDateFormat src = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat dest = new SimpleDateFormat("dd MMMM");

        RecyclerView rv = v.findViewById(R.id.items);
        rv.setAdapter(new Adapter(o));
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        TextView tv = v.findViewById(R.id.title);
        tv.setText(getString(R.string.order_no,o.id));

        try {
            tv = v.findViewById(R.id.deliveryDate);
            Date dlvDate = src.parse(o.dateDelivery);
            String dstr = dest.format(dlvDate);
            tv.setText(dstr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tv = v.findViewById(R.id.address);
        tv.setText(model.getPartner().getValue().address);

        o.updateStatusText(v.findViewById(R.id.order_status));

        TextView b = v.findViewById(R.id.button1);
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
    }

    class Holder extends RecyclerView.ViewHolder {

        PriceTree price;
        public Holder(@NonNull View itemView) {
            super(itemView);
            price = model.getPartner().getValue().getPrice();
        }

        void update(OrderItem item) {

            Price p = price.get(item.item_id);
            if(p == null)
                p = new Price();
            TextView tv = itemView.findViewById(R.id.name);
            tv.setText(p.name);

            if(p.url.size() > 0)
                images.setImage(p.url.get(0), itemView.findViewById(R.id.image));

            float cost = item.sum / item.count;
            tv = itemView.findViewById(R.id.cost);
            tv.setText(Html.fromHtml(getString(R.string.order_row_cost, cost)));

            tv = itemView.findViewById(R.id.qty);
            tv.setText(getString(R.string.order_row_qty, (int)item.count));

            tv = itemView.findViewById(R.id.sum);
            tv.setText(Html.fromHtml(getString(R.string.order_sum, item.sum)));
        }
    }

    class Adapter extends RecyclerView.Adapter<Holder> {

        List<OrderItem> items;
        public Adapter(Order o) {
            items = o.items;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.order_view_row, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.update(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
