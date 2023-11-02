package com.grsoft.napoleon;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AikosDivision;
import com.grsoft.dataobjects.DivisionStock;
import com.grsoft.dataobjects.DivisionStockItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceCountEx extends PriceCount{

    List<StoreData> stores = null;

    Map<String, Integer> stockData;
    StockAdapter adapter;

    @Override protected int getContentViewId() {return R.layout.pricecountex;}

    @Override
    protected void refreshData() {
        super.refreshData();

        if(document == null) {
            document = new OrderImplEx();
        }

        String id = price.getData().id;

        List<DivisionData> divisionStock = loadDivisionStock();
        stockData = ((OrderImplEx)document).getItems(id);

        Price p = price.getData();
        stores = new ArrayList<>();
        ConfigImpl ci = new ConfigImpl();
        ci.read("key", "—клады");
        List<KeyValue> storeSrc = new ArrayList<>();
        DialogHelper.makeListWithKey(ci.getData().value, storeSrc, "" );
        for(int idx=0; idx<storeSrc.size(); idx++) {
            KeyValue kv = storeSrc.get(idx);
            int qty = idx == 0 ? p.qty : p.whQty.size() >= idx ? p.whQty.get(idx-1).qty : 0;
            if(qty > 0 || stockData.containsKey(kv.key)) {
                stores.add(new StoreData(kv, qty, idx));
            }
        }

        OrderItemEx oid = (OrderItemEx) ((OrderImplEx) document).findItem(id);
        ((EditText)findViewById(R.id.remark)).setText(oid != null ? oid.remark : "");

        ((ListView)findViewById(R.id.divisionStock)).setAdapter(new DivisionAdapter(divisionStock));
        adapter = new StockAdapter();
        ((ListView)findViewById(R.id.stock)).setAdapter(adapter);

        updateQtyTotal();
    }

    void inputQty(String id, int maxQty) {
        Integer v = stockData.get(id);
        int startQty = v == null ? 0 : v;

        InputNumberDlg.open(this, new InputNumber() {
            @Override
            public void applayInput(int value, Object... params) {
                if(value < maxQty + startQty) {
                    stockData.put(id, value);
                    updateQtyTotal();
                } else {
                    Toast.makeText(PriceCountEx.this, "¬веденное значение больше остатка", Toast.LENGTH_LONG).show();
                }
                adapter.notifyDataSetChanged();
            }

            @Override public long getValue() { return startQty;}
        });
    }

    class StockAdapter extends BaseAdapter {
        public StockAdapter() {}

        @Override public int getCount() { return stores.size(); }
        @Override public Object getItem(int position) {return stores.get(position);}
        @Override public long getItemId(int position) {return position;}

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = View.inflate(PriceCountEx.this, R.layout.stock_row, null);
            }

            Price p = price.getData();
            StoreData kv = (StoreData) getItem(position);
            String id = kv.key.toString();
            Integer qty = stockData.get(id);

            ((TextView)view.findViewById(R.id.store)).setText(kv.value.toString());

            ((TextView)view.findViewById(R.id.freeQty)).setText(Util.IntToScaleStr(kv.qty, Consts.QTY_SCALE));

            TextView ed = view.findViewById(R.id.stockQty);

            ed.setText(qty == null || qty == 0 ? "" :
                    Util.IntToScaleStr(qty, Consts.QTY_SCALE));
            ed.setOnClickListener(v -> inputQty(id, kv.qty));
            return view;
        }
    }

    class DivisionAdapter extends BaseAdapter {
        List<DivisionData> data;
        public DivisionAdapter(List<DivisionData> data) {
            this.data = data;
        }

        @Override public int getCount() {return data.size();}
        @Override public Object getItem(int position) {return data.get(position);}
        @Override public long getItemId(int position) {return position;}

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = View.inflate(PriceCountEx.this, R.layout.division_stock_row, null);
            }
            DivisionData dd = (DivisionData) getItem(position);
            ((TextView)view.findViewById(R.id.division)).setText(dd.division);
            ((TextView)view.findViewById(R.id.qtyDivision)).setText(Util.IntToScaleStr(dd.qty, Consts.QTY_SCALE));

            return view;
        }
    }

    void updateQtyTotal() {
        int qty = 0;
        for(Integer i : stockData.values()) qty += i;
        ((TextView)findViewById(R.id.qtyTotal)).setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));
    }

    private List<DivisionData> loadDivisionStock() {
        List<DivisionData> ret = new ArrayList<>();
        Map<Object, AikosDivision> ad = DbReader.fetchDic(AikosDivision.class, "id");
        for(DivisionStock ds : DbReader.fetch(DivisionStock.class, String.format("id='%s'", price.getData().id))) {
            for (DivisionStockItem dsi : ds.items) {
                AikosDivision ads = ad.get(dsi.division);
                if(ads == null)
                    continue;
                DivisionData dd = new DivisionData();
                dd.division = ads.name;
                dd.qty = dsi.qty;
                ret.add(dd);
            }
            break;
        }
        return ret;
    }

    @Override
    protected boolean updateOrder() {
        if(document.getRowid() == ExtrasConst.INVALID_ROWID) {
            return false;
        }
        String remark = ((EditText)findViewById(R.id.remark)).getText().toString();
        ((OrderImplEx)document).update(price, getInputCost(price.getData()), stockData, remark, stores);
        return false;
    }

    public static class DivisionData {
        String division = "";
        int qty = 0;
    }
}
