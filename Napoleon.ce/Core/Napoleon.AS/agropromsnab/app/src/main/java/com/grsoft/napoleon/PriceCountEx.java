package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PriceCountEx extends PriceCount{

    List<KeyValue> sklads = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TextView)findViewById(R.id.tvMinCost)).setText(Util.IntToScaleStr(((PriceEx)price.getData()).minCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
    }

    @Override
    protected void onChangeCost(int newCost) {
        if(newCost < ((PriceEx)price.getData()).minCost) {
            Toast.makeText(this, R.string.below_min_cost, Toast.LENGTH_LONG).show();
            return;
        }
        super.onChangeCost(newCost);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.pricecountex;
    }

    @Override
    protected void refreshData() {
        super.refreshData();

        if(sklads == null) {
            sklads = new ArrayList<>();
            ConfigImpl ci = new ConfigImpl();
            Config cfg = ci.getData();
            cfg.key = "Склады";
            ci.read();
            ci.close();

            DialogHelper.makeListWithKey(cfg.value, sklads, "");
        }
        int idx = 0;
        List<SkladData> data = new ArrayList<>();
        Price p = price.getData();
        for( ; idx < sklads.size(); idx++) {
            KeyValue kv = sklads.get(idx);
            SkladData skd = new SkladData();
            skd.id = kv.key.toString();
            skd.name = kv.value.toString();

            skd.qty = idx == 0 ? p.qty :
                    idx <= p.whQty.size() ? p.whQty.get(idx-1).qty :
                    0;

            data.add(skd);
        }
        Adapter a = new Adapter(data);
        ((ListView)findViewById(R.id.sklads)).setAdapter(a);
    }

    class Adapter extends BaseAdapter {

        List<SkladData> data;

        public Adapter(List<SkladData> src) { data = src; }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = View.inflate(PriceCountEx.this, R.layout.sklad_row, null);
            }

            SkladData item = (SkladData) getItem(position);
            TextView tv = view.findViewById(R.id.name);
            tv.setText(item.name);

            tv = view.findViewById(R.id.qty);
            String text = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
            tv.setText(text);
            return view;
        }
    }

    static class SkladData implements Comparable<SkladData> {
        String id = "";
        String name = "";
        int qty = 0;

        @Override
        public int compareTo(SkladData o) {
            return name.compareTo(o.name);
        }
    }
}
