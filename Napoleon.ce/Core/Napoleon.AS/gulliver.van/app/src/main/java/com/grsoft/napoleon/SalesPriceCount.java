package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.PriceSalesQty;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.SalesImpl;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FPOperation;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SalesPriceCount extends PriceCount implements View.OnClickListener {
    Adapter adapter;
    static String POSITION = "pos";

    public static class AdapterData implements Parcelable {
        String id = UUID.randomUUID().toString().replace("-","");
        String number;
        String date;
        String qty;
        boolean pack;

        public AdapterData(){
        }

        protected AdapterData(Parcel in) {
            id = in.readString();
            number = in.readString();
            date = in.readString();
            qty = in.readString();
            pack = in.readByte() != 0;
        }

        public static final Creator<AdapterData> CREATOR = new Creator<AdapterData>() {
            @Override
            public AdapterData createFromParcel(Parcel in) {
                return new AdapterData(in);
            }

            @Override
            public AdapterData[] newArray(int size) {
                return new AdapterData[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(number);
            dest.writeString(date);
            dest.writeString(qty);
            dest.writeByte((byte) (pack ? 1 : 0));
        }
    }

    public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
        Intent i = new Intent(context, SalesPriceCount.class);

        i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

        context.startActivity(i);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.salespricecount;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.btnAddItem).setOnClickListener(this);

        adapter = new Adapter();
        ListView list = findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener((a,v,p,i)->{
            AdapterData d = (AdapterData) a.getItemAtPosition(p);
            PartyItemEdit.open(SalesPriceCount.this, d);
        });

        list.setOnItemLongClickListener((a,v,p,i)->{
            adapter.data.remove(p);
            adapter.notifyDataSetChanged();
            qtyItems = adapter.getCountValue();
            updateSumTextView();
            return true;
        });

        llKeyboard.setVisibility(View.GONE);

        View btn = findViewById(R.id.btnFinish);
        btn.setEnabled(document.isEditable());
        btn.setOnClickListener(new BtnOKClickListenet());

        findViewById(R.id.btnCancel).setOnClickListener((v)->finish());
    }

    protected boolean updateOrder() {
        List<PriceSalesQty> items = new ArrayList<>();

        for(AdapterData a : adapter.data){
            int qty = Util.StrToScale(a.qty, Consts.QTY_SCALE);

            if (qty <= 0)
                continue;

            PriceSalesQty i = new PriceSalesQty();
            i.number = a.number;
            i.date = a.date;
            i.pack = a.pack ? 1 : 0;

            if (a.pack)
                i.qty = (int)FPOperation.itemMul(qty, qtyInPack, Consts.QTY_SCALE);
            else
                i.qty = qty;

            items.add(i);
        }

        ((SalesImplEx)document).updateItem(price, items, priceVal);

        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAddItem)
            PartyItemEdit.open(this, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            AdapterData d = data.getParcelableExtra(PartyItemEdit.DATA);
            if (requestCode == PartyItemEdit.ADD) {
                adapter.data.add(d);
            } else if (requestCode == PartyItemEdit.EDIT){
                adapter.replace(d);
            }

            adapter.notifyDataSetChanged();
            qtyItems = adapter.getCountValue();
            updateSumTextView();
        }
    }

    class Adapter extends BaseAdapter{
        public AdapterData createAdapterData() {
            return new AdapterData();
        }

        public int getCountValue() {
            int ret = 0;

            for (AdapterData d : data){
                int qty = Util.StrToScale(d.qty, Consts.QTY_SCALE);

                if (d.pack)
                    qty = (int) FPOperation.itemMul((int)qty, qtyInPack, Consts.QTY_SCALE);

                ret += qty;
            }

            return ret;
        }

        List<AdapterData> data = new ArrayList<>();

        public Adapter() {
            SalesItemEx se = (SalesItemEx) ((SalesImpl) document).findItem(price.getData().id);

            if (se != null) {
                for (PriceSalesQty i : se.party) {
                    AdapterData d = new AdapterData();
                    d.number = i.number;
                    d.date = i.date;
                    d.qty = "";
                    d.pack = false;

                    int qty = i.pack != 0 ? (int) ((long) i.qty * Consts.QTY_SCALE / (price.getData()).qtyInPack) : i.qty;
                    d.pack = i.pack != 0;
                    d.qty = Util.IntToScaleStr(qty, Consts.QTY_SCALE);

                    data.add(d);
                }
            }
        }

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
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null){
                view = View.inflate(SalesPriceCount.this, R.layout.sales_price_row, null);
            }

            AdapterData item = (AdapterData) getItem(position);

            TextView tv = view.findViewById(R.id.tvNumber);
            tv.setText(item.number);

            tv = view.findViewById(R.id.tvData);
            tv.setText(item.date);

            tv = view.findViewById(R.id.edQty);
            tv.setText(item.qty);

            CheckBox cb = view.findViewById(R.id.cbPack);
            cb.setChecked(item.pack);
            cb.setEnabled(false);

            return view;
        }

        public void replace(AdapterData d){
            for (int i = 0; i < data.size(); i ++){
                if (data.get(i).id.equals((d.id))){
                    data.remove(i);
                    data.add(i, d);
                    return;
                }
            }
        }
    }

    @Override
    protected int getCountValue() {
        return qtyItems;
    }

    protected long getSum(int count) {
        long val = (long)getInputCost(price.getData()) * count / Consts.QTY_SCALE;
        return val;
    }
}
