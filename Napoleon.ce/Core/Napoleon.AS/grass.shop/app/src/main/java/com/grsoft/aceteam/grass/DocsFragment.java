package com.grsoft.aceteam.grass;

import static com.grsoft.view.ListViewRefresher.refresh;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napmobile.R;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DocsFragment extends BaseFragment{

    Adapter adapter;
    @Override protected int getLayoutID() {return R.layout.documents;}
    @Override public String TAG() {return "DocsFragment";}
    @Override public String getTitle() {return getString(R.string.docs);}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ListView lv = v.findViewById(R.id.documents);
        adapter = new Adapter();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            OrderImpl oi = (OrderImpl) adapter.getItem(i);
            model.setOrder(oi);
            ((Main)getActivity()).openOrder();
        });

        String orderNumber = model.getOrderNumber();
        if(orderNumber != null) {
            View frag = v.findViewById(R.id.numberFragment);
            frag.setVisibility(View.VISIBLE);
            TextView tv = v.findViewById(R.id.number);
            tv.setText(orderNumber);
            v.findViewById(R.id.close).setOnClickListener(view -> frag.setVisibility(View.GONE));
        }

        return v;
    }
    static class CmpDocs implements Comparator<OrderImpl> {

        @Override
        public int compare(OrderImpl order, OrderImpl t1) {
            return t1.getData().created.compareTo(order.getData().created);
        }
    }
    class Adapter extends BaseAdapter {

        List<OrderImpl> docs = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        public Adapter() {
            refresh();
        }

        void refresh() {
            removeUnsent();
            docs.clear();
            for(Document<?> d : OrderDoc.instance().docList(null)) {
//                OrderImpl src = (OrderImpl) d;
//                if(src.isEmpty()) {
//                    continue;
//                }
                OrderImpl oi = new OrderImpl();
                oi.read(d.getRowid());
                oi.close();
                docs.add(oi);
            }

            docs.sort(new CmpDocs());
            notifyDataSetChanged();
        }

        void removeUnsent() {
            try {
                long date = Util.getDate().getTime();
                String stmt = String.format("DELETE FROM [%s] WHERE created < %d and params=0", new Order().getTableName(), date);
                DataBaseManager.getDataBase().execSQL(stmt);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        @Override public int getCount() {return docs.size();}
        @Override public Object getItem(int i) {return docs.get(i);}
        @Override public long getItemId(int i) {return i;}

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                view = View.inflate(getContext(), R.layout.docs_row, null);
            }

            OrderImpl oi = (OrderImpl) getItem(i);
            Order o = oi.getData();
            TextView tv;

            tv = view.findViewById(R.id.number);
            if(oi.isEditable()) {
                tv.setTypeface(null, Typeface.BOLD);
            }
            tv.setText(o.number);

            tv = view.findViewById(R.id.date);
            if(oi.isEditable()) {
                tv.setTypeface(null, Typeface.BOLD);
            }
            tv.setText(sdf.format(o.created));

            tv = view.findViewById(R.id.sum);
            if(oi.isEditable()) {
                tv.setTypeface(null, Typeface.BOLD);
            }
            tv.setText(Util.IntToScaleStr(o.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false));
            return view;
        }
    }
}
