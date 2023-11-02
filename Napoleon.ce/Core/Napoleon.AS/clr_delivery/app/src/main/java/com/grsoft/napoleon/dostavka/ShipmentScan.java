package com.grsoft.napoleon.dostavka;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.grsoft.camera.BarcodeHandler;
import com.grsoft.camera.CameraActivity;
import com.grsoft.dataobjects.DWaybillDocumentItem;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ShipmentItem;
import com.grsoft.dataobjects.impl.DShipmentImpl;
import com.grsoft.dataobjects.impl.DShipmentImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RoutePointImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.OrderDetail;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ShipmentScan extends BaseActivity {
    CameraActivity camera;
    Timer errorTimer = null;
    // block repeat fail
    String lastBC = null;
    Adapter adapter;

    DShipmentImplEx doc;
    RoutePointImpl org = new RoutePointImpl();
    PriceImpl price = new PriceImpl();

    protected ListView lvItems;
    protected ImageButton btnLines;
    protected LinesCountController linesController;

    public static void openScan(Context context, DShipmentImpl doc) {
        Intent i = new Intent(context, ShipmentScan.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
        context.startActivity(i);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.shipment_scan);
        doc = new DShipmentImplEx();

        init(savedInstanceState);

        findViewById(com.grsoft.napoleon.R.id.btnScan).setOnClickListener(view -> {
            startCamera();
        });
        if(!doc.isScanned())
            startCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        price.close();
    }

    void init(Bundle savedInstanceState) {
        long docrid;
        if( savedInstanceState == null )
            docrid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
        else
            docrid = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);

        doc.read(docrid);
        org.getData().id = doc.getId();
        org.read();
        org.close();

        TextView tvOrg = (TextView) findViewById(com.grsoft.napoleon.R.id.tvOrg);
        tvOrg.setText(Html.fromHtml(org.getData().name));

        lvItems = (ListView) findViewById(com.grsoft.napoleon.R.id.lvItems);
        btnLines = (ImageButton) findViewById(com.grsoft.napoleon.R.id.btnLines);
        LinesOnClickListener linesOnClickListener = new LinesOnClickListener(lvItems, btnLines, this, true);
        linesController = linesOnClickListener.getController();
        adapter = new Adapter();
        lvItems.setAdapter(adapter);

        registerForContextMenu(lvItems);
//        updateTotalSum();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.refresh();
    }

    private void startCamera() {
        CameraActivity.openBCScanner(ShipmentScan.this, new BarcodeHandler() {
            @Override
            public boolean onReadBarcode(Activity owner, String barcode, int type, long elapsesMs) {
                camera = (CameraActivity) owner;
                if(type == Barcode.FORMAT_DATA_MATRIX && !barcode.equals(lastBC)) {
                    int reason = doc.addBarcode(barcode);
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
            case DShipmentImplEx.ITEM_COMPLETE: return "Позиция подобрана";
            case DShipmentImplEx.ITEM_ADDED: return "Товар добавлен";
            case DShipmentImplEx.FAIL_NO_ITEM: return "Нет такого товара в документе";
            case DShipmentImplEx.FAIL_ALREADY_HAVE: return "КМ уже добавлен";
            case DShipmentImplEx.FAIL_ITEM_COMPLETE: return "КМ по строке набраны";
            case DShipmentImplEx.FAIL_DOC_FINISHED: return "Документ нельзя менять";
            case DShipmentImplEx.FAIL_BC_PARSING: return "Ошибка чтения ШК";
        }
        return "";
    }

    private void updateCamera(final Activity camera, int failReason) {
        TextView tv;
        tv = camera.findViewById(R.id.tvVersion);
        String text = String.format("Всего / введено: %s / %s", Util.IntToScaleStr(doc.need_scanned(), 0),
                Util.IntToScaleStr(doc.scanned(), 0));

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
            String color = failReason == DShipmentImplEx.FAIL_ALREADY_HAVE ? "fuchsia" :
                    failReason == DShipmentImplEx.ITEM_COMPLETE || failReason == DShipmentImplEx.ITEM_ADDED ? "green" :
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
        Adapter adapter = (Adapter) lvItems.getAdapter();
        ShipmentItem i = (ShipmentItem) adapter.getItem(((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position);

        if(item.getItemId() == R.id.reject_missing) {
            i.rejectMissing(doc);
            if(i.outqty == 0) {
                doc.getData().items.remove(i);
            }
            doc.write();
            adapter.notifyDataSetChanged();
        }
        return super.onContextItemSelected(item);
    }

    class Adapter extends BaseAdapter {

        List<ShipmentItem> items = new ArrayList<>();

        public Adapter() {
            refresh();
        }

        public void refresh() {
            items.clear();
            PriceImpl pi = new PriceImpl();
            PriceEx pe = (PriceEx) pi.getData();
            for(DWaybillDocumentItem i : doc.getData().items) {
                pe.id = i.id;
                pi.read();
                if(pe.barcode.length() == 0)
                    continue;

                items.add((ShipmentItem) i);
            }
            pi.close();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null)
                view = View.inflate(ShipmentScan.this, R.layout.scan_row, null);

            ShipmentItem i = (ShipmentItem) getItem(position);
            PriceEx p = (PriceEx) price.getData();
            p.id = i.id;
            price.read();

            int color = i.isScanned() ? Color.BLACK : Color.RED;

            TextView tvName = (TextView)view.findViewById(com.grsoft.napoleon.R.id.tvName);
            TextView tvSum = (TextView)view.findViewById(com.grsoft.napoleon.R.id.tvSum);

            linesController.prepareTextView(tvName);
            tvName.setText(p.name);
            tvName.setTextColor(color);

            long sum = i.barcodes.size() * i.cost;

            tvSum.setText(Util.IntToScaleWStr(sum, Consts.SUM_SCALE, Consts.PRICE_DEC_WIDTH, false));
            tvSum.setGravity(Gravity.RIGHT);
            tvSum.setTextColor(color);

            TextView tvQty = (TextView)view.findViewById(com.grsoft.napoleon.R.id.tvQty);

            tvQty.setText(Util.IntToScaleStr(i.outqty, Consts.QTY_SCALE));
            tvQty.setGravity(Gravity.RIGHT);
            tvQty.setTextColor(color);

            TextView tvDispatch = (TextView) view.findViewById(com.grsoft.napoleon.R.id.tvDispatch);
            tvDispatch.setText(Util.IntToScaleStr(i.barcodes.size(), 0));
            tvDispatch.setGravity(Gravity.RIGHT);
            tvDispatch.setTextColor(color);

            return view;
        }
    }
}
