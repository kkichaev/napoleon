package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.ReturnItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import java.util.Date;

public class ReturnCount extends PriceCount implements OrderImplBase.UpdateQtyHandler {

    static int EXP_DATE = 100;
    static int PROD_DATE = 101;
    static long MIN_DATE = 1000 * 1000;

    Date expDate = new Date(100);
    Date prodDate = new Date(100);

    public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
        Intent i = new Intent(context, ReturnCount.class);

        i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

        context.startActivity(i);
    }

    @Override protected int getContentViewId() { return R.layout.returncount; }
    @Override protected boolean getStartInPack() { return false; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.tvExpDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReturnCount.this, CalendarActivity.class);
                i.putExtra(ExtrasConst.DATE_TAG, expDate.getTime() < MIN_DATE ? (new Date()).getTime() : expDate.getTime());
                startActivityForResult(i, EXP_DATE);
            }
        });

        findViewById(R.id.tvProdDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReturnCount.this, CalendarActivity.class);
                i.putExtra(ExtrasConst.DATE_TAG, prodDate.getTime() < MIN_DATE ? (new Date()).getTime() : prodDate.getTime());
                startActivityForResult(i, PROD_DATE);
            }
        });

        ((ReturnImplEx)document).setUpdateQtyHandler(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( data != null && resultCode == RESULT_OK) {
            Date curDate = new Date();
            long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
            Date newDate = new Date(ct);

            if(requestCode == EXP_DATE)
                expDate = newDate;
            else
                prodDate = newDate;
            refreshDate();
        }
    }

    @Override
    protected void refreshData() {
        super.refreshData();
        ReturnItem ri = (ReturnItem) ((ReturnImplEx)document).findItem(price.getData().id);
        if(ri != null) {
            prodDate = ri.production;
            expDate = ri.expired;
        } else {
            prodDate = new Date(100);
            expDate = new Date(100);
        }

        refreshDate();
    }

    @Override
    protected boolean isInputValid(Runnable r) {
        if(expDate.getTime() < MIN_DATE) {
            Toast.makeText(this, "¬ведите срок годности", Toast.LENGTH_LONG).show();
            return false;
        }
        return super.isInputValid(r);
    }

    private void refreshDate() {
        String text;
        text = dateToString(expDate);
        ((TextView)findViewById(R.id.tvExpDate)).setText(Html.fromHtml(text));

        text = dateToString(prodDate);
        ((TextView)findViewById(R.id.tvProdDate)).setText(Html.fromHtml(text));
    }

    private String dateToString(Date date) {
        if(date.getTime() < MIN_DATE) {
            return "<font color='blue'><u>¬ведите дату</u></font>";
        }
        return "<font color='blue'><u>" + Util.simpleDateFormat.format(date) +  "</u></font>";
    }

    @Override
    public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
        ReturnItem ri = (ReturnItem) item;
        ri.expired = expDate;
        ri.production = prodDate;
    }
}
