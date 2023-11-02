package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RelatedItem;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class PriceCountEx extends PriceCount {
    CheckBox cbRelated;
    List<RelatedItemData> relatedItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cbRelated = findViewById(R.id.cbRelated);
        cbRelated.setVisibility(relatedItems.size() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override protected int getContentViewId() { return R.layout.pricecountex; }

    protected boolean updateQty(boolean inPack, int qty) {
        if (cbRelated.isChecked()) {
            PriceEx pe = (PriceEx) price.getData();
            return ((OrderImplEx) document).updateOrder(price, qty, getInputCost(pe), inPack, pe.related);
        }else
            return !((Itemsable)document).updateQty(price,
                qty, getInputCost(price.getData()), inPack);
    }

    @Override
    protected void refreshData() {
        super.refreshData();

        relatedItems.clear();
        PriceEx pe = (PriceEx) price.getData();

        PriceImpl pi = new PriceImpl();
        Price p = pi.getData();

        for(RelatedItem ri: pe.related) {
            p.id = ri.id;
            if(ri.qty != 0 && pi.read()) {
                relatedItems.add(new RelatedItemData(ri, p));
            }
        }

        pi.close();
        Adapter a = new Adapter();
        ListView lv = findViewById(R.id.lvItems);
        lv.setAdapter(a);


    }

    class Adapter extends BaseAdapter {
        @Override public int getCount() { return relatedItems.size(); }
        @Override public Object getItem(int position) { return relatedItems.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null)
                view = View.inflate(PriceCountEx.this, R.layout.price_related_row, null);

            RelatedItemData i = (RelatedItemData) getItem(position);
            TextView tv = view.findViewById(R.id.tvName);
            tv.setText(i.name);

            tv = view.findViewById(R.id.tvQty);
            tv.setText(Util.IntToScaleStr(i.qty, Consts.QTY_SCALE));
            return view;
        }
    }

    static class RelatedItemData {
        public String name;
        public String id;
        public int qty;

        public RelatedItemData(RelatedItem item, Price p) {
            name = p.name;
            id = p.id;
            qty = item.qty;
        }
    }
}
