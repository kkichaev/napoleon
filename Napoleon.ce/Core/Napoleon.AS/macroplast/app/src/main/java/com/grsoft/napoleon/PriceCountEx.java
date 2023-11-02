package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {

    int minCost = 0;
    boolean started = true;
    String selPrc = "";

    @Override protected int getContentViewId() { return R.layout.pricecountex; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(document instanceof OrderImpl) {
            ((OrderImpl) document).setUpdateQtyHandler(this);
//            ((Spinner)findViewById(R.id.spPrices)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    if(started) {
//                        started = false;
//                    } else {
//                        if(position > 0) {
//                            int cost = CostStrategy.defaultInstance.getPriceCost(price.getData(), position-1, document);
//                            PriceCountEx.super.onChangeCost(cost);
//                        }
//                    }
//                }
//
//                @Override public void onNothingSelected(AdapterView<?> parent) {}
//            });
        }
    }

    @Override
    protected void refreshData() {
        super.refreshData();
        PriceEx pe = (PriceEx)price.getData();
        minCost = pe.minCost;

        if(document instanceof OrderImpl) {
            String prcType = ((Order)document.getData()).prcType;
            OrderItemEx oie = (OrderItemEx) ((OrderImpl) document).findItem(pe.id);
            if(oie != null) {
                prcType = oie.prcType;
            }
            selPrc = prcType;
            Adapter a = new Adapter(prcType);
            ((ListView)findViewById(R.id.lvPrice)).setAdapter(a);
//            Spinner sp = findViewById(R.id.spPrices);
//            ConfigImpl ci = new ConfigImpl();
//            DialogHelper.loadSpinnerWithKeyW(ci, "¬ид÷ены", new ArrayList<>(), sp, prcType, true);
//            ci.close();
        } else {
            findViewById(R.id.llPrice).setVisibility(View.GONE);
        }
    }

    static class CostRow {
        public KeyValue name;
        public int cost;
        public boolean selected = false;

        public CostRow(KeyValue src, int cost) {
            this.name = src;
            this.cost = cost;
        }
    }

    class Adapter extends BaseAdapter {
        List<CostRow> data;
        public Adapter(String prcType) {
            data = new ArrayList<>();
            List<KeyValue> cost = new ArrayList<>();
            ConfigImpl ci = new ConfigImpl();
            ci.read("key", "¬ид÷ены");
            DialogHelper.makeListWithKey(ci.getData().value, cost, "");

            Price p = price.getData();
            for(int i=0; i< cost.size(); i++) {
                KeyValue kv = cost.get(i);
                int c = CostStrategy.defaultInstance.getPriceCost(p, i, document);
                CostRow cr = new CostRow(kv, c);
                if((prcType == null && i == 0) || (prcType != null && prcType.equals(kv.key))) {
                    cr.selected = true;
                }
                data.add(cr);
            }
        }

        @Override public int getCount() {return data.size();}
        @Override public Object getItem(int position) {return data.get(position);}
        @Override public long getItemId(int position) {return position;}

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = View.inflate(PriceCountEx.this, R.layout.price_cost_row, null);
            }
            CostRow cr = (CostRow) getItem(position);
            TextView tv;
            tv = view .findViewById(R.id.name);
            tv.setText(cr.name.value.toString());

            tv = view.findViewById(R.id.cost);
            tv.setText(Util.IntToScaleStr(cr.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false));

            CheckBox cb = view.findViewById(R.id.selected);
            cb.setChecked(cr.selected);
            view.setOnClickListener(v -> {
                selPrc = cr.name.key.toString();
                onChangeCost(cr.cost);
                for(CostRow cri : data) {
                    cri.selected = (cri == cr);
                }
                notifyDataSetChanged();
            });
            return view;
        }
    }

    @Override
    protected void onChangeCost(int newCost) {
        if(newCost < minCost) {
            Toast.makeText(this, "÷ена ниже минимальной", Toast.LENGTH_SHORT).show();
            return;
        }
//        ((Spinner)findViewById(R.id.spPrices)).setSelection(0);
        super.onChangeCost(newCost);
    }

    @Override
    public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
        ((OrderItemEx)item).prcType = selPrc;

//        KeyValue kv = (KeyValue) ((Spinner)findViewById(R.id.spPrices)).getSelectedItem();
//        if(kv != null) {
//            ((OrderItemEx)item).prcType = kv.key.toString();
//        }
    }
}
