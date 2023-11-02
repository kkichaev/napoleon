package com.grsoft.aceteam.grass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceUnit;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napmobile.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.Holder> {

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    Context context;
    List<OrderItemEx> items = new ArrayList<>();

    ItemClickListener clickListener;
    public OrderAdapter(Context context, ItemClickListener listener) {
        this.context = context;
        clickListener = listener;
    }
    public void refresh(List<OrderItem> src) {
        items.clear();
        for(OrderItem oi : src) {
            items.add((OrderItemEx) oi);
        }
        notifyDataSetChanged();
    }

    public OrderItemEx removeItem(int position) {
        OrderItemEx oe = items.get(position);
        items.remove(position);
        notifyItemRemoved(position);
        return oe;
    }

    public OrderItemEx getItem(int position) {
        return items.get(position);
    }

    public void insertItem(OrderItemEx oie, int positioin) {
        items.add(positioin, oie);
        notifyItemInserted(positioin);
    }

    static class Holder extends RecyclerView.ViewHolder {
        PriceImpl price = new PriceImpl();

        @Override
        protected void finalize() throws Throwable {
            price.close();
            super.finalize();
        }

        public Holder(@NonNull View itemView) {
            super(itemView);
        }

        public void update(int i, OrderItemEx item) {
            PriceEx pe = (PriceEx) price.getData();
            pe.id = item.id;
            price.read();

            TextView tv = itemView.findViewById(R.id.tvPos);
            tv.setText(Integer.toString( i + 1 ));

            tv = itemView.findViewById(R.id.tvName);
            tv.setText(price.getData().name.replace("\\n", " "));

            tv = itemView.findViewById(R.id.tvQty);
            PriceUnit pu = pe.getUnit(item.unit);
            String text;
            int q = (int)((long)item.qty * Consts.QTY_SCALE / pu.inpack);
            text = String.format("Кол-во %s %s", Util.IntToScaleStr(q,Consts.QTY_SCALE), pu.name);
            tv.setText(text);

            long s = item.qty * item.cost / Consts.QTY_SCALE;
            tv = itemView.findViewById(R.id.sum);
            tv.setText(Util.IntToScaleStr(s, Consts.SUM_SCALE, Util.DEC_DELIM, false));
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.doc_item_row, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.update(position, items.get(position));
        if(clickListener != null) {
            holder.itemView.setOnClickListener(view -> clickListener.onItemClick(view, position));
        }
    }

    @Override public int getItemCount() {return items.size();}
}
