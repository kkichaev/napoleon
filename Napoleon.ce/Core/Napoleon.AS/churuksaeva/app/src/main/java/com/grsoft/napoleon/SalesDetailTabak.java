package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class SalesDetailTabak extends SalesDetailEx {
    private static final String TAG = "MainEx";
    BarcodeScannerHelper scanner;
    Adapter adapter;
    Uri failTone;
    Uri goodTone;
    MediaPlayer mediaPlayer = null;

    static public void open(Context context, SalesImplEx order) {
        Intent i = new Intent(context, SalesDetailTabak.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
        if(cfg.uriGood.length() > 0)
            goodTone = Uri.parse(cfg.uriGood);
        if(cfg.uriFail.length() > 0)
            failTone = Uri.parse(cfg.uriFail);
    }

    @Override
    protected void onResume() {
        super.onResume();

        CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
        if (doc.isEditable() && cfg.scannerAddress.length() > 0) {
            scanner = new BarcodeScannerHelper();
            scanner.scanning(this, cfg.scannerAddress, handler);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(scanner != null) {
            scanner.close();
            scanner = null;
        }
    }

//    @Override
//    protected void doPrint() {
//        if(!((SalesImplEx)doc).isCompleete()) {
//            Toast.makeText(this, "Не все товары отсканированы в документе", Toast.LENGTH_LONG).show();
//            return;
//        }
//        super.doPrint();
//    }

    BarcodeScannerHelper.Event handler = new BarcodeScannerHelper.Event() {
        @Override
        public void onRead(final String barcode) {
        Log.d(TAG, "Read " + barcode);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Uri u = null;
                boolean res = ((SalesImplEx)doc).addBarcode(barcode);
                if(res) {
                    if(((SalesImplEx)doc).isCompleete()) {
                        u = goodTone;
                    }
                } else {
                    u = failTone;
                }

                u = null; // stub player
                if(u != null) {
                    if(mediaPlayer != null) {
                        mediaPlayer.stop();
                    }

                    mediaPlayer = MediaPlayer.create(SalesDetailTabak.this, u);
                    mediaPlayer.setLooping(false);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.reset();
                            mp.stop();
                        }
                    });
                    mediaPlayer.start();
                }
                if(res) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetInvalidated();
                        }
                    });
                }
//                BarcodeData bc = new BarcodeData(barcode);
//                String str = String.format("товар: %s цена %s", bc.itemBC, Util.IntToScaleStr(bc.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
//                Toast.makeText(SalesDetailTabak.this, str, Toast.LENGTH_LONG).show();
            }
        });
        }
    };

    @Override
    protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
        SalesItemEx se = (SalesItemEx)item;
        String text = "";
        if(se.packQty > 0)
            text += "уп." + Util.IntToScaleStr(se.packQty, Consts.QTY_SCALE);

        if(se.itemQty > 0) {
            if(text.length() > 0) {
                text += "<br/>";
            }
            text += "шт " + Util.IntToScaleStr(se.itemQty, Consts.QTY_SCALE);
        }
        tvQty.setText(Html.fromHtml(text));
    }

    void drawFactQty(int color, OrderItem item, TextView tvQty) {
        SalesItemEx se = (SalesItemEx)item;
        String text = "";
        int qty = se.factPack();
        if(qty > 0)
            text += "уп." + Util.IntToScaleStr(qty, Consts.QTY_SCALE);
        qty = se.factQty();
        if(qty > 0) {
            if(se.packQty > 0) {
                text += "<br/>";
            }
            text += "шт " + Util.IntToScaleStr(qty, Consts.QTY_SCALE);
        }
        tvQty.setText(Html.fromHtml(text));
    }

    @Override protected void setAdapter() {
        adapter = new Adapter();
        lvItems.setAdapter(adapter);
    }

    @Override protected void setContentView() {
        setContentView(R.layout.salesdetail_tabak);
    }

    class Adapter extends OrderItemsAdapter {
        @Override int getResourceID() { return R.layout.salestabak_row; }

        @Override
        protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
            super.drawInternal(view, name, color, item, pos);
            drawFactQty(color, item, (TextView)view.findViewById(R.id.tvQtyFact));
        }
    }
}
