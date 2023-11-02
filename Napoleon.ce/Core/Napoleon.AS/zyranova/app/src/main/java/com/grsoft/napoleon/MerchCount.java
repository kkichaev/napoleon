package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Merch;
import com.grsoft.dataobjects.MerchItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.MerchImpl;
import com.grsoft.napoleon.documents.MerchDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import java.util.Date;

public class MerchCount extends PriceCount {
    static String ITEM_POSITION = "ITEM_POSITION";
    static int DIALOG_DATE_PICKER_ID = 1;
    static int DIALOG_MAN_DATE_PICKER_ID = 2;
    Date bestBefore  = new Date(0);
    Date manufactoring  = new Date(0);
    EditText edExpDay;
    MerchItem item = null;
    EditText edRemark;

    public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
        open(context, priceRoid, doc, -1);
    }

    public static void open(Context context, long rowid, DbObject<? extends DataObject> doc, int position) {
        Intent i = new Intent(context, MerchCount.class);

        i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, rowid);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
        i.putExtra(ITEM_POSITION, position);

        context.startActivity(i);
    }

    @Override protected int getContentViewId() { return R.layout.merchitem; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.ivPresent2).setVisibility(View.GONE);

        findViewById(R.id.tvBestBefore).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MerchCount.this, CalendarActivity.class);
                i.putExtra(ExtrasConst.DATE_TAG, isBestBeforeValid() ? bestBefore.getTime() : new Date().getTime());
                startActivityForResult(i, DIALOG_DATE_PICKER_ID);
            }
        });

        findViewById(R.id.tvManufactoringDate).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MerchCount.this, CalendarActivity.class);
                i.putExtra(ExtrasConst.DATE_TAG, isManufactoringValid() ? manufactoring.getTime() : new Date().getTime());
                startActivityForResult(i, DIALOG_MAN_DATE_PICKER_ID);
            }
        });

        edExpDay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if( hasFocus ) {
                    keypadHelper.setTargetID(R.id.edExpDay);
                    edExpDay.selectAll();
                }
            }
        });

        edExpDay.setInputType(InputType.TYPE_NULL);


    }

//    @Override
//    protected boolean isInputValid(Runnable r) {
//        if(isBestBeforeValid() == false) {
//            Toast.makeText(this, "¬ведите срок годности", Toast.LENGTH_LONG).show();
//            return false;
//        }
//        return super.isInputValid(r);
//    }

    boolean isBestBeforeValid() {
        return (bestBefore.getTime() > 24 * 3600 * 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( data != null) {
            Date curDate = new Date();
            long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
            Date newDate = new Date(ct);
            if (requestCode == DIALOG_DATE_PICKER_ID)
                bestBefore = newDate;
            else if (requestCode == DIALOG_MAN_DATE_PICKER_ID)
                manufactoring = newDate;

            refreshDate();
        }
    }

    @Override
    protected boolean updateOrder() {
        //((MerchImpl)document).update(price.getData().id, qtyItems, bestBefore);
        Merch data = ((MerchImpl)document).getData();

        if(qtyItems == 0) {
            data.items.remove(item);
        } else {
            if(item == null) {
                item = new MerchItem();
                item.id = price.getData().id;
                data.items.add(item);
            }
            item.qty = qtyItems;
            item.bestBefore = bestBefore;
            item.manufactoring = manufactoring;
            item.expDay = getExpDayInt();
            item.remark = edRemark.getText().toString().trim();
        }

        document.write();
        document.close();

        MerchDoc.instance().refreshDocSum(data.id);

        return false;
    }

    private int getExpDayInt() {
        int res = 0;
        try {
            res = Integer.parseInt(edExpDay.getText().toString().trim());
        }catch (Exception e){
            e.printStackTrace();
        }

        return res;
    }

    @Override
    protected void refreshData() {
        super.refreshData();

        edExpDay = findViewById(R.id.edExpDay);
        edRemark = findViewById(R.id.edRemark);

        bestBefore = new Date(0);
        manufactoring = new Date(0);

        int pos = getIntent().getIntExtra(ITEM_POSITION, -1);
        Merch merch = ((MerchImpl)document).getData();

        if (pos != -1 && merch.items.size() >= pos)
            item = merch.items.get(pos);

        edCount.setText("");

        if (item != null){
            bestBefore = item.bestBefore;
            manufactoring = item.manufactoring;
            edCount.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE));
            edExpDay.setText(Integer.toString(item.expDay));
            edRemark.setText(item.remark);
        }

        refreshDate();
    }

    void refreshDate() {
        final  String INPUT_DATE = "<u>введите дату</u>";

        TextView tv = findViewById(R.id.tvBestBefore);
        String text = (isBestBeforeValid() == false) ? INPUT_DATE : "<u>" + Util.simpleDateFormat.format(bestBefore) + "</u>";
        tv.setText(Html.fromHtml(text));

        tv = findViewById(R.id.tvManufactoringDate);
        text = (isManufactoringValid() == false) ? INPUT_DATE : "<u>" + Util.simpleDateFormat.format(manufactoring) + "</u>";
        tv.setText(Html.fromHtml(text));
    }

    private boolean isManufactoringValid() {
        return (manufactoring.getTime() > 24 * 3600 * 1000);
    }
}
