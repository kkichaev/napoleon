package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.grsoft.camera.BarcodeHandler;
import com.grsoft.camera.CameraActivity;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.Timer;
import java.util.TimerTask;

public class SalesScan extends OrderDeliveryDetail {
    CameraActivity camera;
    Timer errorTimer = null;
    // block repeat fail
    String lastBC = null;

    public static void openScan(Context context, SalesImplEx doc) {
        Intent i = new Intent(context, SalesScan.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btnScan).setOnClickListener(view -> {
            startCamera();
        });

        if(!((SalesImplEx)doc).isScanned())
            startCamera();
    }

    private void startCamera() {
        CameraActivity.openBCScanner(SalesScan.this, new BarcodeHandler() {
            @Override
            public boolean onReadBarcode(Activity owner, String barcode, int type, long elapsesMs) {
                camera = (CameraActivity) owner;
                if(type == Barcode.FORMAT_DATA_MATRIX && !barcode.equals(lastBC)) {
                    int reason = ((SalesImplEx) doc).addBarcode(barcode);
                    runOnUiThread( () -> updateCamera(owner, reason));
                    lastBC = barcode;
                }
                return false;
            }

            @Override
            public void initActivity(Activity owner) {
                updateCamera(owner, 0);
            }
        });
    }

    private String reasonToText(int reason) {
        switch(reason) {
            case SalesImplEx.ITEM_COMPLETE: return "Позиция подобрана";
            case SalesImplEx.ITEM_ADDED: return "Товар добавлен";
            case SalesImplEx.FAIL_NO_ITEM: return "Нет такого товара в документе";
            case SalesImplEx.FAIL_ALREADY_HAVE: return "КМ уже добавлен";
            case SalesImplEx.FAIL_ITEM_COMPLETE: return "КМ по строке набраны";
            case SalesImplEx.FAIL_DOC_FINISHED: return "Документ нельзя менять";
            case SalesImplEx.FAIL_BC_PARSING: return "Ошибка чтения ШК";
        }
        return "";
    }

    private void updateCamera(final Activity camera, int failReason) {
        TextView tv;
        tv = camera.findViewById(R.id.tvVersion);
        SalesImplEx se = (SalesImplEx)doc;
        String text = String.format("Всего / введено: %s / %s", Util.IntToScaleStr(se.need_scanned(), 0),
                Util.IntToScaleStr(se.scanned(), 0));

        tv.setText(text);

        tv  = camera.findViewById(com.grsoft.camera.R.id.tvInfo);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        if(errorTimer != null) {
            errorTimer.cancel();
            errorTimer = null;
        }

        text = "";
        if(failReason != 0) {
            String ft = reasonToText(failReason);
            String color = failReason == SalesImplEx.FAIL_ALREADY_HAVE ? "fuchsia" :
                    failReason == SalesImplEx.ITEM_COMPLETE || failReason == SalesImplEx.ITEM_ADDED ? "green" :
                            "red";

            text = "<br/><font color='" + color +  "'>" + ft + "</font>";
            errorTimer = new Timer();
            errorTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    lastBC = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateCamera(camera, 0);
                        }
                    });
                }
            }, 5000);
        } else {
            text = "Наведите рамку сканирования на сканируемый код";
            lastBC = null;
        }
        tv.setText(Html.fromHtml(text));

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(doc.isEditable())
            getMenuInflater().inflate(R.menu.scan_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        OrderItemsAdapter adapter = (OrderItemsAdapter) lvItems.getAdapter();
        SalesItemEx i = (SalesItemEx) adapter.getItem(((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position);

        if(item.getItemId() == R.id.reject_missing) {
            i.rejectMissing((SalesImplEx) doc);
            if(i.qty == 0) {
                doc.getData().items.remove(i);
            }
            doc.write();
            adapter.notifyDataSetChanged();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void editItem(OrderItem orderItem) {
    }

    @Override
    protected void loadItems() {
        items.clear();
        for(OrderItem oi : doc.getData().items) {
            int scanned = ((SalesItemEx)oi).barcodes.size();
            if(scanned > 0) {
                DeliveryItem di = new DeliveryItem();
                di.id = oi.id;
                di.qty = scanned * Consts.QTY_SCALE;
                di.sum = scanned * oi.cost;

                items.add(di);
            }
        }
    }

    @Override
    protected void afterDocReaded() {
        loadItems();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.sales_scan);
    }
}
