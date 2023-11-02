package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceDescription;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQtyItemEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceDescriptionImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.SalesHistory;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {

    static int INFO_DLG = 0x432;
    PriceDescriptionImpl pdi = new PriceDescriptionImpl();
    int dsc = 0;
    int priceCost = 0;

    @Override protected int getContentViewId() { return R.layout.pricecountex; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.ivInfo).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showInfo();
            }
        });
        if(document instanceof  OrderImplEx) {
            ((OrderImplEx)(document)).setUpdateQtyHandler(this);
        }

        findViewById(R.id.tvDiscount).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                DiscountInputDlg.open(PriceCountEx.this, new InputNumber() {
                    @Override public void applayInput(int value, Object... params) { onDiscountChange(-value); }
                    @Override public long getValue() { return -dsc; }
                }, Consts.SUM_SCALE, true, getString(R.string.discount), DiscountInputDlg.Type.OnlyDiscount);
            }
        });

    }

    void onDiscountChange( int newDsc ) {
        dsc = newDsc;
        priceVal = CostStrategy.costWithDiscount(priceCost, dsc, Consts.SUM_SCALE);
        updateChangedFields();
    }

    void showInfo() {
        View v = View.inflate(this, R.layout.price_info, null);
        TextView tv = v.findViewById(R.id.tvTitle);
        tv.setText(price.getData().name);

        tv = v.findViewById(R.id.tvInfo);
        tv.setText(pdi.getData().description);

        final PopupWindow pw = new PopupWindow(v, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
//        pw.setTouchable(true);

        pw.showAtLocation(v, Gravity.CENTER, 0, 0);

        v.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) { pw.dismiss(); }
        });
    }

    @Override
    protected void createSimpleHistory(Price p, LinearLayout ll) {
        String historyItems[] = SalesHistory.getHistory(document.getData().id, p.id, Features.SALES_FROM_ORDERS);

        for (int i = 0; i < historyItems.length -1; i += 3) {
            TextView tvSaleItem = new TextView(this);
            tvSaleItem.setText(Html.fromHtml(
                    String.format("%s<br/>%s<br/>%s", historyItems[i], historyItems[i+1], historyItems[i+2])));
            tvSaleItem.setLines(3);
            tvSaleItem.setTextColor(getResources().getColor(R.color.black));
            tvSaleItem.setPadding(5, 3, 5, 3);
            ll.addView(tvSaleItem);

            Log.d("makeSaleHistory", tvSaleItem.getText().toString());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        pdi.close();
    }

    @Override
    protected void refreshData() {
        super.refreshData();

        if(ivPresent2.getVisibility() == View.INVISIBLE) {
            ivPresent2.setVisibility(View.GONE);
        }

        PriceEx p = (PriceEx) price.getData();
        int cost = p.cost.size() == 0 ? 0 : p.cost.get(0).cost;
        ((TextView)findViewById(R.id.tvMinPrice)).setText(Util.IntToScaleStr(cost, Consts.SUM_SCALE, Util.DEC_DELIM, false));

        int dd = p.dlvDays;
        if(document instanceof OrderImplEx && ((OrderImplEx)document).getWhIndex() > 0) {
            dd += 14;
        }
        ((TextView)findViewById(R.id.tvDlvDays)).setText(dd == 0 ? "" : Integer.toString(dd) + " дней");

        View vinfo = findViewById(R.id.ivInfo);
        PriceDescription pd = pdi.getData();
        pd.id = p.id;
        if(pdi.read()) {
            vinfo.setVisibility(View.VISIBLE);
        } else {
            vinfo.setVisibility(View.GONE);
        }

        LinearLayout ll = findViewById(R.id.llQty);
        ll.removeAllViews();

        StringBuilder sb = new StringBuilder();
        ConfigImpl ci = new ConfigImpl();
        if(ci.getValue(sb,"Склады")) {
            List<KeyValue> sklads = new ArrayList<>();
            DialogHelper.makeListWithKey(sb.toString(), sklads, "");

            int idx = 0;
            for(KeyValue kv : sklads) {
                if(idx > p.whQty.size())
                    break;

                Data data = idx == 0 ? new Data(kv.value.toString(), p) : new Data(kv.value.toString(), p, (PriceQtyItemEx) p.whQty.get(idx-1));
                View v = createView(data);

                ll.addView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                idx++;
            }
        }

//        ListView lv = findViewById(R.id.lvQty);
//        lv.setAdapter(new QtyAdapter(p));

        if (document != null) {
            priceCost = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass()).getPriceCost(price.getData(), document.getSumType(), document);
            if (document instanceof OrderImplEx) {
                OrderItemEx i = (OrderItemEx) ((Itemsable) document).findItem(price.getData().id);

                if (i != null) {
                    dsc = i.discount;
                } else
                    dsc = ((OrderEx) document.getData()).discount;
            }
        }

        updateChangedFields();
    }

    @Override
    protected void onChangeCost(int newCost) {
        super.onChangeCost(newCost);
        dsc = priceCost != 0 ? (int)(10000 - (long)priceVal * 10000 / priceCost) : 0;
        updateChangedFields();
    }

    void updateChangedFields() {
        updateCost();
        updateSumTextView();
        updateDsc();
    }

    void updateDsc() {
        String text = "<u><font color='red'>" + Util.IntToScaleStr(dsc, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</font></u>";
        TextView tv;
        tv = (TextView)findViewById(R.id.tvDiscount);
        tv.setText(Html.fromHtml(text));
    }

    View createView(Data item) {
        View view = View.inflate(this, R.layout.price_qty_row, null);

        String text;

        text = item.sklad;
        ((TextView)view.findViewById(R.id.tvName)).setText(text);

        if(item.qty == 0) text = "";
        else text= Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
        ((TextView)view.findViewById(R.id.tvFree)).setText(text);

        if(item.total == 0) text = "";
        else text= Util.IntToScaleStr(item.total, Consts.QTY_SCALE);
        ((TextView)view.findViewById(R.id.tvTotal)).setText(text);

        if(item.rezerv == 0) text = "";
        else text= Util.IntToScaleStr(item.rezerv, Consts.QTY_SCALE);
        ((TextView)view.findViewById(R.id.tvRezerv)).setText(text);

        if(item.order == 0) text = "";
        else text= Util.IntToScaleStr(item.order, Consts.QTY_SCALE);
        ((TextView)view.findViewById(R.id.tvOrder)).setText(text);

        return view;
    }

    @Override
    public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
        CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
        Features.CAN_CHANGE_COST = false;

        ((OrderItemEx)item).costNoDsc = cs.getPriceCost(price.getData(), document.getSumType(), document);
        ((OrderItemEx)item).discount = dsc;

        Features.CAN_CHANGE_COST = true;
    }

    static class Data {
        String sklad;
        int qty;
        int total;
        int rezerv;
        int order = 0;

        public Data(String sklad, PriceEx p) {
            this.sklad = sklad;
            qty = p.freeQty;
            total = p.qty;
            rezerv = p.rezervQty;
            order = p.orderQty;
        }

        public Data(String sklad, PriceEx p, PriceQtyItemEx pqi) {
            this.sklad = sklad;
            qty = pqi.freeQty;
            total = pqi.qty;
            rezerv = pqi.rezervQty;
//                order = p.ordervQty;
        }
    }


//    class QtyAdapter extends BaseAdapter {
//
//        List<Data> data = new ArrayList<>();
//        public QtyAdapter(PriceEx p) {
//
//            StringBuilder sb = new StringBuilder();
//            ConfigImpl ci = new ConfigImpl();
//            if(ci.getValue(sb,"Склады")) {
//                List<KeyValue> sklads = new ArrayList<>();
//                DialogHelper.makeListWithKey(sb.toString(), sklads, "");
//
//                int idx = 0;
//                for(KeyValue kv : sklads) {
//                    if(idx > p.whQty.size())
//                        break;
//                    if(idx == 0 ) {
//                        data.add(new Data(kv.value.toString(), p));
//                    } else {
//                        data.add(new Data(kv.value.toString(), p, p.whQty.get(idx-1)));
//                    }
//                    idx++;
//                }
//            }
//        }
//
//        @Override public int getCount() { return data.size(); }
//        @Override public Object getItem(int position) { return data.get(position); }
//        @Override public long getItemId(int position) { return position; }
//
//        @Override
//        public View getView(int position, View view, ViewGroup parent) {
//            if(view == null) {
//                view = View.inflate(PriceCountEx.this, R.layout.price_qty_row, null);
//            }
//            Data item = (Data) getItem(position);
//
//            String text;
//
//            text = item.sklad;
//            ((TextView)view.findViewById(R.id.tvName)).setText(text);
//
//            if(item.qty == 0) text = "";
//            else text= Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
//            ((TextView)view.findViewById(R.id.tvFree)).setText(text);
//
//            if(item.total == 0) text = "";
//            else text= Util.IntToScaleStr(item.total, Consts.QTY_SCALE);
//            ((TextView)view.findViewById(R.id.tvTotal)).setText(text);
//
//            if(item.rezerv == 0) text = "";
//            else text= Util.IntToScaleStr(item.rezerv, Consts.QTY_SCALE);
//            ((TextView)view.findViewById(R.id.tvRezerv)).setText(text);
//
//            if(item.order == 0) text = "";
//            else text= Util.IntToScaleStr(item.order, Consts.QTY_SCALE);
//            ((TextView)view.findViewById(R.id.tvOrder)).setText(text);
//            return view;
//        }
//    }
}
