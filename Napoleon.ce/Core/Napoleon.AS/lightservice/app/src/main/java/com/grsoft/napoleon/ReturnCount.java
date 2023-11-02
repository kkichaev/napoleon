package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.util.ArrayList;

public class ReturnCount extends PriceCount implements OrderImplBase.UpdateQtyHandler {

    public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
        Intent i = new Intent(context, ReturnCount.class);

        i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ReturnImpl)document).setUpdateQtyHandler(this);
    }

    @Override protected int getContentViewId() { return R.layout.returncount; }

    @Override
    protected int getStartValue() {
        return Consts.QTY_SCALE;
    }

    @Override
    protected void refreshData() {
        super.refreshData();

        ConfigImpl ci = new ConfigImpl();
        ReturnItem ri = (ReturnItem) ((ReturnImplEx)document).findItem(price.getData().id);

        DialogHelper.loadSpinnerWithKey(ci, "КачествоВозврат", new ArrayList<KeyValue>(), (Spinner) findViewById(R.id.spQuality), (ri != null) ? ri.quality : "");
        ((TextView)findViewById(R.id.edRemark)).setText(ri == null ? "" : ri.remark);
    }

    @Override protected boolean havePriceMover() { return false; }
    @Override protected void setItemImage(String fileName) { }

    @Override
    public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
        ReturnItem ri = (ReturnItem)item;
        KeyValue kv = (KeyValue) ((Spinner)findViewById(R.id.spQuality)).getSelectedItem();
        if(kv != null)
            ri.quality = kv.key.toString();

        ri.remark = ((EditText)findViewById(R.id.edRemark)).getText().toString();
    }
}
