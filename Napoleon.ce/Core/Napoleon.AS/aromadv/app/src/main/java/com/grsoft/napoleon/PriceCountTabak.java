package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.ScannedItems;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.grsoft.util.Consts.QTY_SCALE;

public class PriceCountTabak extends PriceCount implements OrderImplBase.UpdateQtyHandler {
    boolean refreshing = false;

    public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
        Intent i = new Intent(context, PriceCountTabak.class);

        i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

        context.startActivity(i);
    }

    @Override protected int getContentViewId() { return R.layout.pricecounttabak; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EditText ed;
        ed = (EditText) findViewById(R.id.edCountPack);
        ed.setOnFocusChangeListener(changeFocus);
        ed.addTextChangedListener(updateQty);
        ed.setInputType(InputType.TYPE_NULL);

        ed = (EditText) findViewById(R.id.edCountQty);
        ed.setOnFocusChangeListener(changeFocus);
        ed.addTextChangedListener(updateQty);
        ed.setInputType(InputType.TYPE_NULL);

        if(document != null) {
            ((SalesImplEx)document).setUpdateQtyHandler(this);
        }
    }

    View.OnFocusChangeListener changeFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus) {
            keypadHelper.setTargetID(v.getId());
            ((EditText)v).selectAll();
        }
        }
    };

    TextWatcher updateQty = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!refreshing) {
                SalesItemEx se = (SalesItemEx)((SalesImplEx)document).findItem(price.getData().id);
                if(se != null) {
                    se.barcodes.clear();
                    updateScanned();
                }
            }
            updateSumTextView();
        }

        @Override public void afterTextChanged(Editable s) { }
    };

    @Override
    protected void updateSumTextView() {
        super.updateSumTextView();

        PCQtyData qd = getPCQty();
        String qtext = String.format("%d уп./%d шт", qd.pq / QTY_SCALE, qd.iq / QTY_SCALE);
        ((TextView)findViewById(R.id.tvQtyCount)).setText(qtext);
    }

    @Override
    protected int getCountValue() {
        PCQtyData qd = getPCQty();
        return (int)((long)qd.pq * price.getData().qtyInPack / QTY_SCALE) + qd.iq;
    }

    @Override
    protected void refreshData() {
        super.refreshData();

        refreshing = true;
        SalesItemEx se = (SalesItemEx)((SalesImplEx)document).findItem(price.getData().id);
        if(se != null) {
            ((EditText)findViewById(R.id.edCountPack)).setText(Util.IntToScaleStr(se.packQty, QTY_SCALE));
            ((EditText)findViewById(R.id.edCountQty)).setText(Util.IntToScaleStr(se.itemQty, QTY_SCALE));
        }
        updateScanned();
        updateSumTextView();

        refreshing = false;
    }

    @Override
    protected boolean isInputValid(Runnable r) {
        int inputs = getCountValue();
        if(inputs > price.getData().vanQty) {
            Toast.makeText(this, "¬ведено больше остатка", Toast.LENGTH_LONG).show();
            return false;
        }
        return super.isInputValid(r);
    }

    void updateScanned() {
        List<ScannedItems> items = new ArrayList<ScannedItems>();
        String qtext = "";
        SalesItemEx se = (SalesItemEx)((SalesImplEx)document).findItem(price.getData().id);
        if(se != null) {
            items = se.barcodes;
            qtext = String.format("%d уп./%d шт", se.factPack() / QTY_SCALE, se.factQty() / QTY_SCALE);
        }
        ((TextView)findViewById(R.id.tvQtyScan)).setText(qtext);

        ArrayAdapter<ScannedItems> aa = new ArrayAdapter<ScannedItems>(this, R.layout.simple_spinner_layout, items);
        ListView lv = (ListView)findViewById(R.id.barcodeItems);
        lv.setAdapter(aa);
    }

    @Override
    public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
        SalesItemEx se = (SalesItemEx)item;
        se.itemQty = (int)Util.StrToScale(((EditText)findViewById(R.id.edCountQty)).getText().toString(), Consts.QTY_SCALE);
        se.packQty = (int)Util.StrToScale(((EditText)findViewById(R.id.edCountPack)).getText().toString(), Consts.QTY_SCALE);

        ((SalesEx)order).useTax = 1;
        se.countTax((Sales) order, price.getData().tax1);
    }

    PCQtyData getPCQty() {
        PCQtyData ret = new PCQtyData();
        ret.pq = (int)Util.StrToScale(((EditText)findViewById(R.id.edCountPack)).getText().toString(), Consts.QTY_SCALE);
        ret.iq = (int)Util.StrToScale(((EditText)findViewById(R.id.edCountQty)).getText().toString(), Consts.QTY_SCALE);
        return ret;
    }
}

class PCQtyData {
    public int pq;
    public int iq;
}